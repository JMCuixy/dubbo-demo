package com.demo.sequence.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class SequenceBufferPoolExecutor {

    private static ExecutorService executor = Executors.newFixedThreadPool(2);

    private static Map<String, Boolean> bufferLoading = new ConcurrentHashMap<String, Boolean>(2);

    private static Lock lock = new ReentrantLock();

    public static void execute(Runnable runnable) {

        String taskName = runnable.getClass().getSimpleName();

        lock.lock();
        try {
            // 防止同一任务没有执行完还被多次提交
            if (bufferLoading.get(taskName) != null && bufferLoading.get(taskName)) {
                return;
            }

            bufferLoading.put(taskName, true);
        } finally {
            lock.unlock();
        }

        try {
            Future<?> future = executor.submit(runnable);
            // 让线程阻塞等待返回是为了将bufferLoading还原
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            bufferLoading.put(taskName, false);
        }
    }


}
