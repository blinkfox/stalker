package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.kit.MathKit;
import com.blinkfox.stalker.result.bean.OverallResult;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于测量待执行方法耗时情况等信息的抽象运行器，实现了 {@link MeasureRunner} 接口.
 *
 * @author blinkfox on 2020-05-24.
 * @see MeasureRunner
 * @see SimpleMeasureRunner
 * @see ConcurrentMeasureRunner
 * @since v1.2.0
 */
@Slf4j
public abstract class AbstractMeasureRunner implements MeasureRunner {

    /**
     * 线程池.
     */
    protected ExecutorService executorService;

    /**
     * 线程执行计数器锁.
     */
    protected CountDownLatch countLatch;

    /**
     * 每次'成功'测量出的待测量方法的耗时时间，单位为纳秒({@code ns}).
     */
    protected Queue<Long> eachMeasures;

    /**
     * 测量过程中执行的总次数.
     */
    protected LongAdder total;

    /**
     * 测量过程中执行成功的次数.
     */
    protected LongAdder success;

    /**
     * 测量过程中执行失败的次数.
     */
    protected LongAdder failure;

    /**
     * 是否已经运行完成.
     */
    protected AtomicBoolean complete;

    /**
     * 运行开始时的纳秒时间戳，单位为纳秒({@code ns}).
     */
    @Getter
    protected long startNanoTime;

    /**
     * 运行结束时的纳秒时间戳，单位为纳秒({@code ns}).
     */
    @Getter
    protected long endNanoTime;

    /**
     * 公共的抽象父构造方法.
     */
    public AbstractMeasureRunner() {
        this.eachMeasures = new ConcurrentLinkedQueue<>();
        this.total = new LongAdder();
        this.success = new LongAdder();
        this.failure = new LongAdder();
        this.complete = new AtomicBoolean(false);
    }

    @Override
    public long[] getEachMeasures() {
        // 为了不影响正在运行中的数据及当前或以后数据统计的"准确性"，这里复制一份队列中的数据来单独计算和返回.
        Queue<Long> queue = new ArrayDeque<>(this.eachMeasures);
        int len = queue.size();
        long[] measures = new long[len];
        for (int i = 0; i < len; i++) {
            Long cost = queue.poll();
            if (cost != null) {
                measures[i] = cost;
            }
        }
        return measures;
    }

    @Override
    public long getTotal() {
        return this.total.longValue();
    }

    @Override
    public long getSuccess() {
        return this.success.longValue();
    }

    @Override
    public long getFailure() {
        return this.failure.longValue();
    }

    @Override
    public boolean isComplete() {
        return this.complete.get();
    }

    @Override
    public long getCosts() {
        return this.endNanoTime - this.startNanoTime;
    }

    /**
     * 构造正在运行中的任务的测量结果信息的 {@link OverallResult} 对象.
     *
     * @return 总体测量结果信息
     */
    @Override
    public OverallResult buildRunningMeasurement() {
        // 如果任务已经完成，就直接返回最终的测试结果数据即可.
        if (this.complete.get()) {
            return this.buildFinalMeasurement();
        }

        // 如果任务仍然在运行中，由于各个计数器是独立的，对于整体上的各个统计数据的结果来说，并不能保证"线程安全".
        // 为了减小仍在运行中时的任务，获取各个统计数据时线程安全所导致的误差.
        // 这里只获取 eachMeasures 和 failure 两个值，基于这两个值来计算其他值，消耗的时间使用当前时间来计算.
        long failure = this.getFailure();
        long[] eachCosts = this.getEachMeasures();
        long totalCount = eachCosts.length;
        long costs = this.startNanoTime == 0 ? 0 : System.nanoTime() - this.startNanoTime;
        return new OverallResult()
                .setEachMeasures(eachCosts)
                .setCosts(costs)
                .setTotal(totalCount)
                .setSuccess(totalCount - failure)
                .setFailure(failure)
                .setThroughput(MathKit.calcThroughput(totalCount, costs));
    }

    /**
     * 构造最终运行完时的测量的结果信息的 {@link OverallResult} 对象.
     *
     * @return 总体测量结果信息
     */
    @Override
    public OverallResult buildFinalMeasurement() {
        long totalCount = this.getTotal();
        return new OverallResult()
                .setEachMeasures(this.getEachMeasures())
                .setCosts(this.getCosts())
                .setTotal(totalCount)
                .setSuccess(this.getSuccess())
                .setFailure(this.getFailure())
                .setThroughput(MathKit.calcThroughput(totalCount, this.getCosts()));
    }

    /**
     * 等待所有线程执行完毕，并最终关闭线程池.
     */
    protected void awaitAndShutdown() {
        try {
            if (this.countLatch != null) {
                this.countLatch.await();
            }
        } catch (InterruptedException e) {
            log.error("在多线程下等待测量结果结束时出错!", e);
            Thread.currentThread().interrupt();
        } finally {
            if (this.executorService != null) {
                this.executorService.shutdown();
            }
        }
    }

    /**
     * 立即安静的关闭线程池.
     */
    protected void shutdownNowQuietly() {
        if (this.executorService != null) {
            try {
                this.executorService.shutdownNow();
            } catch (Exception e) {
                log.error("【Stalker 错误】关闭测量任务的线程池失败，失败原因：【{}】.", e.getMessage());
            }
        }
    }

}
