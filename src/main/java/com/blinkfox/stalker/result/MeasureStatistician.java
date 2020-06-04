package com.blinkfox.stalker.result;

import com.blinkfox.stalker.kit.MathKit;
import com.blinkfox.stalker.result.bean.StatisResult;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.StampedLock;

/**
 * 针对测量出的消耗时间数据进行统计的统计器类.
 *
 * @author blinkfox on 2020-06-05.
 * @since v1.2.0
 */
public class MeasureStatistician {

    /**
     * 95% 置信区间的 Z 值.
     */
    private static final double Z = 1.96;

    /**
     * 用来表示存放每次花费时间的数组容量的阈值常量，默认 10 万.
     */
    private static final int THRESHOLD = 100_000;

    /**
     * 用来高效读写数据的锁.
     */
    private final StampedLock stampedLock = new StampedLock();

    /**
     * 用来记录每次测量出的待测量方法的消耗时间的集合，单位为纳秒(ns)，
     * 当集合中的数量超过设定的阈值时，就会清空本集合中的数据，防止内存移除.
     */
    private final List<Long> eachCosts = new ArrayList<>(3325);

    /**
     * 测量代码在执行过程中所消耗的总耗时，单位为纳秒(ns).
     */
    private long costs;

    /**
     * 总次数.
     */
    private long total;

    /**
     * 测量过程中执行成功的次数.
     */
    private long success;

    /**
     * 测量过程中执行失败的次数.
     */
    private long failure;

    /**
     * 吞吐率，指单位时间内（每秒）的执行总次数，即：{@code throughput = total / (costs / 10^9)}.
     */
    private double throughput;

    /**
     * 总耗时.
     */
    private long sum;

    /**
     * 平均耗时.
     */
    private long avg;

    /**
     * 最小耗时.
     */
    private long min;

    /**
     * 最大耗时.
     */
    private long max;

    /**
     * 方差和，该值表示各个值与平均值的差的平方和.
     *
     * @since v1.2.0
     */
    private double varSum;

    /**
     * 标准差.
     */
    private double stdDev;

    /**
     * 95%置信区间下限.
     */
    private double lowerConfidence;

    /**
     * 95%置信区间上限.
     */
    private double upperConfidence;

    /**
     * 更新最新的统计数据.
     *
     * <p>为了防止多个线程更新数据时的线程安全问题，这里加了写锁.</p>
     *
     * @param currSuccess 当前累计的成功运行次数
     * @param currFailure 当前累计的失败运行次数
     * @param currCosts 当前累计的总的运行时间
     * @param currEachCosts 从上次更新到本次更新期间的每次运行次数的花费时间
     */
    public StatisResult updateAndGet(long currSuccess, long currFailure, long currCosts, Long[] currEachCosts) {
        long stamp = stampedLock.writeLock();
        try {
            // 对基础统计数据进行赋值.
            this.success = currSuccess;
            this.failure = currFailure;
            this.total = this.success + this.failure;
            this.costs = currCosts;
            this.throughput = MathKit.calcThroughput(this.total, this.costs);

            // 遍历求得所有测量值的和，最大值，最小值和平均值.
            for (Long cost : currEachCosts) {
                eachCosts.add(cost);
                this.sum += cost;
                if (this.min > cost) {
                    this.min = cost;
                }
                if (this.max < cost) {
                    this.max = cost;
                }
            }
            this.avg = this.sum / this.total;

            // 计算方差所需的当前所有数据的平方差之和.
            double currVarSum = this.varSum;
            for (long measure : eachCosts) {
                currVarSum += Math.pow(measure - (double) avg, 2);
            }

            // 分别计算出标准差和95%的置信区间半径，
            // 由于数据超过阈值之后，之前的每次花费时间的值会清空，而新的花费时间的平均值发生了变化，
            // 因此，计算的总的平法差之和并不准确，从而导致标准差也不准确，但是在大数据情况时，这些误差可以容忍.
            this.stdDev = Math.sqrt(currVarSum / this.total);
            double radius = (Z * this.stdDev) / Math.sqrt(this.total);
            this.lowerConfidence = this.avg - radius;
            this.upperConfidence = this.avg + radius;

            // 当数据量超过阈值之后，为了防止后续程序继续运行时内存溢出，就清空 eachCosts 集合中的数据.
            // 并记录当前累计的平法差之和，便于以后再累计，计算方差时使用.
            if (this.eachCosts.size() > THRESHOLD) {
                this.eachCosts.clear();
                this.varSum = currVarSum;
            }
        } finally {
            stampedLock.unlockWrite(stamp);
        }
        return this.get();
    }

    /**
     * 读取最新的统计结果信息.
     *
     * <p>这里先使用乐观读的方式读取数据，如果读取期间数据发生了变化，就采用悲观读的方式来再次读取最新的数据到新的结果变量中.</p>
     *
     * @return 统计结果信息
     */
    public StatisResult get() {
        StatisResult statisResult = new StatisResult();
        long stamp = stampedLock.tryOptimisticRead();
        this.copyStatisResultData(statisResult);
        if (!stampedLock.validate(stamp)) {
            stamp = stampedLock.readLock();
            try {
                this.copyStatisResultData(statisResult);
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        return statisResult;
    }

    /**
     * 复制当前对象中的最新数据到新创建的 StatisResult 对象中.
     *
     * @param statisResult 新创建的 StatisResult 对象
     */
    private void copyStatisResultData(StatisResult statisResult) {
        statisResult.setCosts(this.costs);
        statisResult.setTotal(this.total);
        statisResult.setSuccess(this.success);
        statisResult.setFailure(this.failure);
        statisResult.setThroughput(this.throughput);
        statisResult.setSum(this.sum);
        statisResult.setAvg(this.avg);
        statisResult.setMin(this.min);
        statisResult.setMax(this.max);
        statisResult.setStdDev(this.stdDev);
        statisResult.setLowerConfidence(this.lowerConfidence);
        statisResult.setUpperConfidence(this.upperConfidence);
    }

}
