package org.dubbo.lock.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * @author : cuixiuyin
 * @date : 2019/7/13
 */
@Component
public class LockClient {

    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> valueOperations;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.valueOperations = stringRedisTemplate.opsForValue();
    }

    /**
     * 缓存锁实现方式 1， 利用 incr 操作的原子性。
     * 这种锁的方式，不在乎结果数据。保证只有唯一一个线程能够执行到业务代码。
     */
    public void lockIncr() {
        // 1. 递增指定键对应的数值。
        // 2. 如果不存在key对应的值，那么会先将key的值设置为0，然后执行incr操作，返回1。
        Long lockIncr = valueOperations.increment("lockIncr", 1);
        // 说明拿到了锁
        if (lockIncr == 1) {
            // 业务操作
        }
    }


    /**
     * 缓存实现方式 setnx 方式，只有第一个线程会成功，返回 true，其他线程都是失败 false
     */
    public void lockSetnx() {
        String lock = "lockSetnx";
        long millis = System.currentTimeMillis();
        long timeout = millis + 3000L + 1;
        try {
            while (true) {
                boolean setnx = valueOperations.setIfAbsent(lock, timeout + "");
                if (setnx == true) {
                    break;
                }
                String oldTimeout = valueOperations.get(lock);
                // getSet：把新的值 set 进去，并返回旧的值
                // 这一步是为了解决 redis 异常宕机，锁没有被正常释放的时候。
                // 当 p1、p2 同时执行到这里，发现锁的时间过期了。p1、p2 同时执行 getSet 命名，但是只有一个线程可以拿到锁的时间，表示争抢到了锁。
                // 比如 p1 getSet 命名执行成功，那么它自然可以符合下面的判断式；而 p2 getSet 拿到的是 p1 getSet 进去的值，自然无法符合下面的表达式。
                String oldValue = valueOperations.getAndSet(lock, timeout + "");
                if (millis > Long.valueOf(oldTimeout) && millis > Long.valueOf(oldValue)) {
                    break;
                }
                Thread.sleep(100);
            }

            // 执行业务代码
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (millis < timeout) {
                stringRedisTemplate.delete(lock);
            }
        }

    }

}
