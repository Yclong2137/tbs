package com.ycl.tbs.utils;


import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 支持按任务的优先级去执行,
 * 支持线程池暂停.恢复(批量文件下载，上传) ，
 * 异步结果主动回调主线程
 */
public class HiExecutor {


    private boolean isPaused = false;

    private final ThreadPoolExecutor hiExecutor;

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition pauseCondition;


    private static final class Holder {
        private static final HiExecutor INSTANCE = new HiExecutor();
    }


    public static HiExecutor getInstance() {
        return HiExecutor.Holder.INSTANCE;
    }


    private HiExecutor() {
        this.pauseCondition = lock.newCondition();
        int cpuCount = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cpuCount + 1;
        int maxPoolSize = cpuCount * 2 + 1;
        PriorityBlockingQueue<Runnable> blockingQueue = new PriorityBlockingQueue<>();
        long keepAliveTime = 30L;
        TimeUnit unit = TimeUnit.SECONDS;
        final AtomicLong seq = new AtomicLong();
        ThreadFactory threadFactory = new ThreadFactory() {

            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("tt-executor-" + seq.getAndIncrement());
                return thread;
            }
        };
        hiExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, blockingQueue, threadFactory) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                if (isPaused) {
                    try {
                        lock.lock();
                        pauseCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                //监控线程池耗时任务,线程创建数量,正在运行的数量
            }
        };
    }


    static class PriorityRunnable implements Runnable, Comparable<HiExecutor.PriorityRunnable> {

        final int priority;
        final Runnable runnable;

        public PriorityRunnable(int priority, Runnable runnable) {
            this.priority = priority;
            this.runnable = runnable;
        }

        @Override
        public int compareTo(@NonNull HiExecutor.PriorityRunnable o) {
            if (this.priority < o.priority) {
                return 1;
            } else if (this.priority > o.priority) {
                return -1;
            }
            return 0;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }

    public void execute(@IntRange(from = 0, to = 10) int priority, Runnable runnable) {
        this.hiExecutor.execute(new PriorityRunnable(priority, runnable));
    }


    public void execute(Runnable runnable) {
        this.execute(0, runnable);
    }

    /**
     * 暂停
     */
    public void pause() {
        lock.lock();
        try {
            isPaused = true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 恢复
     */
    public void resume() {
        lock.lock();
        try {
            isPaused = false;
            pauseCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
