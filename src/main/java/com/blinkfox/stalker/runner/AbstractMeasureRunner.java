package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.result.MeasureStatistician;
import com.blinkfox.stalker.result.bean.StatisResult;
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
     * 线程池.
     */
    protected ExecutorService executorService;

    /**
     * 测量统计器的实例对象.
     */
    private final MeasureStatistician measureStatistician;

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
     * 用于记录以前总共读取了 eachMeasures 中的数据总量，即总的偏移量.
     */
    private long beforeCount;

    /**
     * 公共的抽象父构造方法.
     */
    public AbstractMeasureRunner() {
        this.measureStatistician = new MeasureStatistician();
        this.eachMeasures = new ConcurrentLinkedQueue<>();
        this.success = new LongAdder();
        this.failure = new LongAdder();
        this.completed = new AtomicBoolean(false);
        this.canceled = new AtomicBoolean(false);
    }

    public long getTotal() {
        return this.success.longValue() + this.failure.longValue();
    }

    public long getSuccess() {
        return this.success.longValue();
    }

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
     * 更新并获取统计结果信息数据，由于可能会有两个或多个线程去更新和获取统计数据，这里使用 {@code synchronized} 来同步.
     *
     * @return 统计结果信息
     */
    @Override
    public synchronized StatisResult getStatisResult() {
        // 如果任务已经完成，就直接返回最终的测试结果数据即可.
        if (this.completed.get()) {
            return measureStatistician.get();
        }

        // 获取到截至到当前时间的正确运行次数数、错误运行次数，消耗的时间和每次的运行时间等数据.
        long currFailure = this.getFailure();
        long currSuccess = this.getSuccess();
        long currCosts = this.startNanoTime == 0 ? 0 : System.nanoTime() - this.startNanoTime;
        int len = (int) (currSuccess + currFailure - beforeCount);

        // 截取复制出最新的
        Long[] currMeasures = new Long[len];
        for (int i = 0; i < len; ++i) {
            Long cost = this.eachMeasures.poll();
            if (cost != null) {
                currMeasures[i] = cost;
            }
        }
        beforeCount = currSuccess + currFailure;

        // 更新并获取最新的数据.
        return measureStatistician.updateAndGet(currSuccess, currFailure, currCosts, currMeasures);
    }

}
