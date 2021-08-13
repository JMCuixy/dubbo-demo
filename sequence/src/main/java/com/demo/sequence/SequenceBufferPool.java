package com.demo.sequence;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class SequenceBufferPool<K, V> {

	/**
	 * 序列号缓冲区
	 */
	private final Map<K, Queue<V>> SEQUENCE_BUFFER = new ConcurrentHashMap<>();

	/**
	 * 程序并发锁
	 */
	private ReadWriteLock rwl = new ReentrantReadWriteLock();

	/**
	 * 入队
	 */
	public void offer(K appName, V value) {
		
		Queue<V> queue = null;

		// 当线程开始读时，首先开始加上读锁
        rwl.readLock().lock();

		try {
			if (!SEQUENCE_BUFFER.containsKey(appName)) {
				// 在开始写之前，首先要释放读锁，否则写锁无法拿到
                rwl.readLock().unlock();
                // 获取写锁开始写数据
                rwl.writeLock().lock();

                try {
                	// 再次判断该值是否为空，因为如果两个写线程都阻塞在这里，
                    // 当一个线程被唤醒后value的值为null则进行数据加载，当另外一个线程也被唤醒如果不判断就会执行两次写
                	if (!SEQUENCE_BUFFER.containsKey(appName)) {
                    	// 使用ConcurrentLinkedQueue来创建一个线程安全的非阻塞队列
        				queue = new ConcurrentLinkedQueue<V>();
        				// 将queue放入缓冲区
        				SEQUENCE_BUFFER.put(appName, queue);
                	}
                } finally {
                	// 释放写锁
                	rwl.writeLock().unlock();
                }
                // 写完之后降级为读锁
                rwl.readLock().lock();
			}

			SEQUENCE_BUFFER.get(appName).offer(value);
		} finally {
			// 释放读锁
			rwl.readLock().unlock();
		}

	}

	/**
	 * 弹出一个值
	 */
	public V poll(K appName) {
		if (SEQUENCE_BUFFER.get(appName) == null) {
			return null;
		}

		return SEQUENCE_BUFFER.get(appName).poll();
	}

	public void clear(K appName) {
		if (SEQUENCE_BUFFER.get(appName) == null) {
			return;
		}

		SEQUENCE_BUFFER.get(appName).clear();
	}

	public int size(K appName) {
        if (SEQUENCE_BUFFER.get(appName) == null) {
            return 0;
        }

        return SEQUENCE_BUFFER.get(appName).size();
    }

}
