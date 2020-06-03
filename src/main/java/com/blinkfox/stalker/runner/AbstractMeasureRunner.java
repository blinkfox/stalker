package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.kit.MathKit;
import com.blinkfox.stalker.result.bean.OverallResult;
import com.blinkfox.stalker.result.bean.StatisResult;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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
 * @see SimpleScheduledMeasureRunner
 * @see ConcurrentMeasureRunner
 * @see ConcurrentScheduledMeasureRunner
 * @since v1.2.0
 */
@Slf4j
public abstract class AbstractMeasureRunner implements MeasureRunner {

    /**
     * 每隔 10 万，累计一次统计计数，并清空 {@code eachMeasures} 队列中的数据，防止内存溢出.
     */
    protected static final int MAX_PERIOD_COUNT = 100000;

    /**
     * 线程池.
     */
    protected ExecutorService executorService;

    /**
     * 直到最后一次累计构建的统计结果信息.
     */
    @Getter
    protected StatisResult lastStatisResult;

    /**
     * 每次'成功'测量出的待测量方法的耗时时间，单位为纳秒({@code ns}).
     */
    protected Queue<Long> eachMeasures;

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
    protected AtomicBoolean completed;

    /**
     * 是否已经被取消.
     */
    protected AtomicBoolean canceled;

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
        this.success = new LongAdder();
        this.failure = new LongAdder();
        this.completed = new AtomicBoolean(false);
        this.canceled = new AtomicBoolean(false);
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
        return this.success.longValue() + this.failure.longValue();
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
    public boolean isCompleted() {
        return this.completed.get();
    }

    @Override
    public boolean isCancelled() {
        return this.canceled.get();
    }

    @Override
    public long getCosts() {
        return this.endNanoTime - this.startNanoTime;
    }

    /**
     * 如果结束时间的值是 0，那么就设置结束时的纳秒时间.
     *
     * @param endNanoTime 结束纳秒时间.
     */
    public void setEndNanoTimeIfEmpty(long endNanoTime) {
        if (this.endNanoTime == 0) {
            this.endNanoTime = endNanoTime;
        }
    }

    /**
     * 构造正在运行中的任务的测量结果信息的 {@link OverallResult} 对象.
     *
     * @return 总体测量结果信息
     */
    @Override
    public OverallResult buildRunningMeasurement() {
        // 如果任务已经完成，就直接返回最终的测试结果数据即可.
        if (this.completed.get()) {
            return this.buildFinalMeasurement();
        }

        // 如果任务仍然在运行中，由于各个计数器是独立的，对于整体上的各个统计数据的结果来说，并不能保证"线程安全".
        // 为了减小仍在运行中时的任务，获取各个统计数据时线程安全所导致的误差.
        // 这里只获取 eachMeasures 和 failure 两个值，基于这两个值来计算其他值，消耗的时间使用当前时间来计算.
        final long currFailure = this.getFailure();
        final long[] currEachCosts = this.getEachMeasures();
        long currTotalCount = currEachCosts.length;
        long currCosts = this.startNanoTime == 0 ? 0 : System.nanoTime() - this.startNanoTime;
        return new OverallResult()
                .setEachMeasures(currEachCosts)
                .setCosts(currCosts)
                .setTotal(currTotalCount)
                .setSuccess(currTotalCount - currFailure)
                .setFailure(currFailure)
                .setThroughput(MathKit.calcThroughput(currTotalCount, currCosts));
    }

    /**
     * 构造最终运行完时的测量的结果信息的 {@link OverallResult} 对象.
     *
     * @return 总体测量结果信息
     */
    @Override
    public OverallResult buildFinalMeasurement() {
        long successCount = this.success.longValue();
        long failureCount = this.failure.longValue();
        long totalCount = successCount + failureCount;
        return new OverallResult()
                .setEachMeasures(this.getEachMeasures())
                .setCosts(this.getCosts())
                .setTotal(totalCount)
                .setSuccess(successCount)
                .setFailure(failureCount)
                .setThroughput(MathKit.calcThroughput(totalCount, this.getCosts()));
    }

}
