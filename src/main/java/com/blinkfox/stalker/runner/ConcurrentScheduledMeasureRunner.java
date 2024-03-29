package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.config.RunDuration;
import com.blinkfox.stalker.result.MeasureResult;
import com.blinkfox.stalker.runner.executor.StalkerExecutors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;

/**
 * 继承自 {@link ConcurrentMeasureRunner}，在多线程并发情况下的运行指定的持续时间的测量运行器.
 *
 * @author blinkfox on 2020-06-01.
 * @since v1.2.0
 */
@Slf4j
public class ConcurrentScheduledMeasureRunner extends ConcurrentMeasureRunner {

    /**
     * 用于异步定时调度任务的线程池.
     */
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * 执行中的测量任务的 {@link Future} 实例.
     *
     * @since v1.2.0
     */
    protected Future<?> scheduledFuture;

    /**
     * 构造方法.
     *
     * <p>这个类中的属性，需要支持高并发写入.</p>
     */
    public ConcurrentScheduledMeasureRunner() {
        super();
        this.scheduledExecutorService = StalkerExecutors.newScheduledThreadPool(1, "stalker-concurrent-scheduled");
        super.executorService = StalkerExecutors.newFixedThreadExecutor(
                StalkerExecutors.MAX_POOL_SIZE, "stalker-concurrent-scheduled-measure");
    }

    /**
     * 持续并发的执行指定时间的 runnable 方法，并将执行成功与否、耗时结果等信息存入到 OverallResult 实体对象中.
     *
     * @param options 运行的配置选项实例
     * @param runnable 可运行实例
     * @return 测量统计结果
     */
    @Override
    public MeasureResult run(Options options, Runnable runnable) {
        int concurrens = options.getConcurrens();
        int runs = options.getRuns();
        boolean printErrorLog = options.isPrintErrorLog();

        // 初始化存储的集合、线程池、并发工具类中的对象实例等.
        final Semaphore semaphore = new Semaphore(concurrens);

        // 到指定的持续时间之后，就取消执行中的任务,并关闭线程池，
        // 注意，由于是定时任务，所以“是否取消”也设置为 false，用于区分是否是人为取消了任务，只有人为取消的才是 true.
        final RunDuration duration = options.getDuration();
        this.scheduledFuture = this.scheduledExecutorService.schedule(() -> {
            this.stop();
            super.canceled.compareAndSet(true, false);
        }, duration.getAmount(), duration.getTimeUnit());

        super.startNanoTime = System.nanoTime();
        long expectEndNanoTime = duration.getEndNanoTime(super.startNanoTime);

        while (true) {
            // 如果任务线程池已终止，或者当前时间大于了期望的结束时间，就跳出 while 循环.
            if (super.executorService.isShutdown() || (System.nanoTime() > expectEndNanoTime)) {
                break;
            }

            try {
                semaphore.acquire();
                final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    this.loopMeasure(runs, printErrorLog, runnable);
                    semaphore.release();
                }, super.executorService);

                // 将 future 添加到正在运行的 Future 信息集合中，并在 future 完成时,异步移除已经完成了的 future.
                runningFutures.add(future);
                future.whenCompleteAsync((a, b) -> runningFutures.remove(future));
            } catch (InterruptedException e) {
                log.error("【Stalker 错误提示】在多线程并发情况下测量任务执行的耗时信息的线程已被中断!", e);
                Thread.currentThread().interrupt();
            }
        }

        // 等待所有线程执行完毕，记录是否完成和完成时间，并关闭线程池等资源，最后将结果封装成实体信息返回.
        super.setEndNanoTimeIfEmpty(System.nanoTime());
        super.completed.compareAndSet(false, true);
        StalkerExecutors.shutdownNow(this.executorService, this.scheduledExecutorService);
        if (!this.scheduledFuture.isDone()) {
            this.scheduledFuture.cancel(true);
        }
        return super.getMeasureResult();
    }

    /**
     * 停止相关的运行测量任务.
     */
    @Override
    public void stop() {
        super.stop();

        // 关闭定时任务线程池和取消对应的定时任务.
        StalkerExecutors.shutdownNow(this.scheduledExecutorService);
        if (this.scheduledFuture != null && !this.scheduledFuture.isDone()) {
            this.scheduledFuture.cancel(true);
        }
    }

}
