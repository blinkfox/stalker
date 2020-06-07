package com.blinkfox.stalker.runner.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Stalker 中使用到的线程池执行器工具类.
 *
 * @author blinkfox on 2020-06-02.
 * @since v1.2.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StalkerExecutors {

    private static final int MAX_QUEUE_SIZE = 524280;

    public static final int MAX_POOL_SIZE = 1024;

    /**
     * 根据线程名称创建新的单线程线程池.
     *
     * @param threadName 线程名称
     * @return 线程池
     */
    public static ThreadPoolExecutor newSingleThreadExecutor(String threadName) {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2), r -> new Thread(r, threadName), new StalkerRejectedHandler());
    }

    /**
     * 根据线程名称创建固定数量线程的线程池，最大线程数最多 1024 个.
     *
     * @param corePoolSize 核心线程数
     * @param threadName 线程名称
     * @return 线程池
     */
    public static ThreadPoolExecutor newFixedThreadExecutor(int corePoolSize, String threadName) {
        int fixedPoolSize = Math.min(corePoolSize, MAX_POOL_SIZE);
        return new ThreadPoolExecutor(fixedPoolSize, fixedPoolSize, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(MAX_QUEUE_SIZE), r -> new Thread(r, threadName),
                new StalkerRejectedHandler());
    }

    /**
     * 根据线程名称创建固定数量线程的线程池，最大线程数最多 1024 个.
     *
     * @param corePoolSize 核心线程数
     * @param maxPoolSize 最大线程数
     * @param threadName 线程名称
     * @return 线程池
     */
    public static ThreadPoolExecutor newThreadExecutor(int corePoolSize, int maxPoolSize, String threadName) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(MAX_QUEUE_SIZE), r -> new Thread(r, threadName),
                new StalkerRejectedHandler());
    }

    /**
     * 根据线程名称创建新的可调度定时任务的程线程池.
     *
     * @param corePoolSize 核心线程数
     * @param threadName 线程名称
     * @return 线程池
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, String threadName) {
        return new ScheduledThreadPoolExecutor(corePoolSize,
                r -> new Thread(r, threadName), new StalkerRejectedHandler());
    }

    /**
     * 等待所有线程执行完毕，并最终关闭这若干个线程池集合.
     *
     * @param executorServices 若干个待关闭的线程池执行器集合
     */
    public static void shutdown(ExecutorService... executorServices) {
        for (ExecutorService executorService : executorServices) {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
        }
    }

    /**
     * 立即安静的关闭若干个线程池集合.
     *
     * @param executorServices 若干个待立即关闭的线程池执行器集合
     */
    public static void shutdownNow(ExecutorService... executorServices) {
        for (ExecutorService executorService : executorServices) {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdownNow();
            }
        }
    }

}
