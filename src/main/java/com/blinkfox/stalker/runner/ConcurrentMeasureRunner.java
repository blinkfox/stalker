package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.kit.ConcurrentHashSet;
import com.blinkfox.stalker.result.bean.OverallResult;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;

/**
 * 当待执行的实例方法是在多线程并发情况时的测量运行实现类.
 *
 * @author blinkfox on 2019-01-08.
 * @since v1.0.0
 */
@Slf4j
public class ConcurrentMeasureRunner extends AbstractMeasureRunner {

    private static final int N_1024 = 1024;

    /**
     * 用于异步移除已经执行完成的线程的后台任务线程池.
     */
    protected final ExecutorService backExecutorService;

    /**
     * 用于存放正在运行中的 Future 线程，便于在手动"停止"运行时，能取消正在执行中的任务.
     */
    protected final Set<CompletableFuture<Void>> runningFutures;

    /**
     * 构造方法.
     *
     * <p>这个类中的属性，需要支持高并发写入.</p>
     */
    public ConcurrentMeasureRunner() {
        super();
        this.backExecutorService = Executors.newSingleThreadExecutor();
        this.runningFutures = new ConcurrentHashSet<>();
    }

    /**
     * 执行 runnable 方法，并将执行结果的耗时纳秒(ns)值存入结果对象中.
     *
     * @param options 运行的配置选项实例
     * @param runnable 可运行实例
     * @return 测量结果
     */
    @Override
    public OverallResult run(Options options, Runnable runnable) {
        int threads = options.getThreads();
        int concurrens = options.getConcurrens();
        int runs = options.getRuns();
        boolean printErrorLog = options.isPrintErrorLog();

        // 初始化存储的集合、线程池、并发工具类中的对象实例等.
        Semaphore semaphore = new Semaphore(Math.min(concurrens, threads));
        CountDownLatch countLatch = new CountDownLatch(threads);
        super.executorService = Executors.newFixedThreadPool(Math.min(threads, N_1024));
        super.startNanoTime = System.nanoTime();

        // 在多线程下控制线程并发量，与循环搭配来一起执行和测量.
        for (int i = 0; i < threads; i++) {
            try {
                semaphore.acquire();
                final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    this.loopMeasure(runs, printErrorLog, runnable);
                    semaphore.release();
                    countLatch.countDown();
                }, super.executorService);

                // 将 future 添加到正在运行的 Future 信息集合中，并在 future 完成时,异步移除已经完成了的 future.
                runningFutures.add(future);
                future.whenCompleteAsync((a, b) -> runningFutures.remove(future), backExecutorService);
            } catch (InterruptedException e) {
                log.error("【Stalker 错误提示】在多线程并发情况下测量任务执行的耗时信息的线程已被中断!", e);
            }
        }

        // 等待所有线程执行完毕，记录是否完成和完成时间，并关闭线程池等资源，最后将结果封装成实体信息返回.
        this.await(countLatch);
        super.endNanoTime = System.nanoTime();
        super.complete.compareAndSet(false, true);
        super.shutdown();
        this.backExecutorService.shutdown();
        return super.buildFinalMeasurement();
    }

    /**
     * 单个线程的循环批量执行测量.
     *
     * @param runs 批量运行次数
     * @param printErrorLog 是否打印输出错误日志
     * @param runnable 可执行实例
     */
    private void loopMeasure(int runs, boolean printErrorLog, final Runnable runnable) {
        for (int j = 0; j < runs; j++) {
            try {
                long eachStart = System.nanoTime();
                runnable.run();
                super.eachMeasures.add(System.nanoTime() - eachStart);
                super.success.increment();
            } catch (Exception e) {
                // 如果待测量的方法，执行错误则失败数 +1,且根据选项参数来判断是否打印异常错误日志.
                super.failure.increment();
                if (printErrorLog) {
                    log.error("测量方法耗时信息在多线程下出错!", e);
                }
            }
        }
    }

    /**
     * 停止相关的运行测量任务.
     *
     * <p>注意：如果任务未完成，则立即停止线程池，但是还不能停止正在运行中的若干任务线程，
     * 暂时还没想到一个更好的、高性能的停止所有运行中的任务的方法.</p>
     *
     * @return 是否成功的布尔值
     * @author blinkfox on 2020-05-25.
     * @since v1.2.0
     */
    public boolean stop() {
        if (!isComplete()) {
            super.endNanoTime = System.nanoTime();
            super.complete.compareAndSet(false, true);

            // 停止时直接关闭线程池.
            super.shutdownNowQuietly();
            this.backExecutorService.shutdownNow();

            // 迭代删除正在运行中的 Future，并取消正在运行中的任务.
            Iterator<CompletableFuture<Void>> futureIterator = this.runningFutures.iterator();
            while (futureIterator.hasNext()) {
                CompletableFuture<Void> future = futureIterator.next();
                this.runningFutures.remove(future);
                if (!future.isDone()) {
                    future.cancel(true);
                }
            }
        }
        return true;
    }

    /**
     * 等待所有线程执行完毕，并最终关闭线程池.
     *
     * @param countLatch 计数锁
     * @author blinkfox on 2020-05-25.
     * @since v1.2.0
     */
    private void await(CountDownLatch countLatch) {
        try {
            if (countLatch != null) {
                countLatch.await();
            }
        } catch (InterruptedException e) {
            log.error("【Stalker 错误提示】在并发执行下等待任务执行结束时出错!", e);
        }
    }

}
