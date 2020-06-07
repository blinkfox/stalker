package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.result.MeasureResult;
import com.blinkfox.stalker.result.MeasureStatistician;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

    private final Lock statisLock;

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
    private long beforeTotalCount;

    /**
     * 公共的抽象父构造方法.
     */
    public AbstractMeasureRunner() {
        this.measureStatistician = new MeasureStatistician();
        this.statisLock = new ReentrantLock();
        this.eachMeasures = new ConcurrentLinkedQueue<>();
        this.success = new LongAdder();
        this.failure = new LongAdder();
        this.completed = new AtomicBoolean(false);
        this.canceled = new AtomicBoolean(false);
    }

    /**
     * 获取当前测量任务已经运行的总花费时间.
     *
     * @return 运行总花费时间
     */
    public long getCosts() {
        return this.completed.get() && this.getTotal() <= this.beforeTotalCount && eachMeasures.isEmpty()
                ? this.endNanoTime - this.startNanoTime
                : this.startNanoTime == 0 ? 0 : System.nanoTime() - this.startNanoTime;
    }

    /**
     * 获取任务运行成功的数量.
     *
     * @return 成功数量
     */
    @Override
    public long getTotal() {
        return this.success.longValue() + this.failure.longValue();
    }

    /**
     * 获取任务运行成功的数量.
     *
     * @return 成功数量
     */
    public long getSuccess() {
        return this.success.longValue();
    }

    /**
     * 获取任务运行失败的数量.
     *
     * @return 失败数量
     */
    public long getFailure() {
        return this.failure.longValue();
    }

    /**
     * 判断当前任务是否已经执行完成.
     *
     * @return 是否执行完成的布尔值
     */
    @Override
    public boolean isCompleted() {
        return this.completed.get();
    }

    /**
     * 判断当前任务是否已经被取消.
     *
     * @return 是否被取消的布尔值
     */
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
     * 更新并获取统计结果信息数据.
     *
     * <p>如果任务已经完成，就直接返回最终的测试结果数据即可，否则加锁获取正在运行中的任务的统计数据.</p>
     *
     * @return 统计结果信息
     */
    @Override
    public MeasureResult getMeasureResult() {
        return this.completed.get() && this.getTotal() <= this.beforeTotalCount && eachMeasures.isEmpty()
                ? this.measureStatistician.get()
                : this.getRunningMeasureResult();
    }

    /**
     * 获取正在运行中的任务的数据的统计结果信息.
     *
     * <p>由于可能会有两个或多个线程去更新和获取统计数据，这里须要加锁来获取正在运行中的任务的统计数据.</p>
     *
     * @return 统计结果信息
     */
    private MeasureResult getRunningMeasureResult() {
        try {
            // 读取时加锁.
            statisLock.lockInterruptibly();

            // 计算出运行消耗的总时间，如果已经结束了，就直接使用结束时间戳减去开始时间戳.
            final long currCosts = this.completed.get()
                    ? this.endNanoTime - this.startNanoTime
                    : this.startNanoTime == 0 ? 0 : System.nanoTime() - this.startNanoTime;

            // 获取到截至到当前时间的正确运行次数数、错误运行次数，消耗的时间和每次的运行时间等数据.
            final long currFailure = this.getFailure();
            final long currSuccess = this.getSuccess();
            final long currTotal = currSuccess + currFailure;
            int newCount = (int) (currTotal - this.beforeTotalCount);
            if (newCount <= 0) {
                return this.measureStatistician.get();
            }

            // 截取复制出最新的测量耗时信息，这里从队列中"出队读取" len 个最新的测量耗时数据.
            final List<Long> currEachCosts = new ArrayList<>(newCount);
            for (int i = 0; i < newCount; ++i) {
                Long cost = this.eachMeasures.poll();
                if (cost != null) {
                    currEachCosts.add(cost);
                }
            }
            this.beforeTotalCount = currTotal;

            // 更新并获取最新的统计数据信息.
            return measureStatistician.updateAndGet(currSuccess, currFailure, currCosts, currEachCosts);
        } catch (InterruptedException e) {
            log.error("【Stalker 错误提示】获取运行中任务的统计结果数据线程被中断！", e);
            Thread.currentThread().interrupt();
            return this.measureStatistician.get();
        } catch (Exception e) {
            log.error("【Stalker 错误提示】获取运行中任务的统计结果数据时出错，将直接返回之前的数据.", e);
            return this.measureStatistician.get();
        } finally {
            statisLock.unlock();
        }
    }

}
