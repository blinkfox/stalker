package com.blinkfox.stalker.result;

import com.blinkfox.stalker.kit.MathKit;
import java.util.ArrayList;
import java.util.List;

/**
 * 针对测量出的消耗时间数据进行统计的统计器类，一些通用属性信息集成自 {@link MeasureResult}.
 *
 * @author blinkfox on 2020-06-05.
 * @see MeasureResult
 * @since v1.2.0
 */
public class MeasureStatistician extends MeasureResult {

    /**
     * 95% 置信区间的 Z 值.
     */
    private static final double Z = 1.96;

    /**
     * 用来表示存放每次花费时间的数组容量的阈值常量，默认 10 万.
     */
    private static final int THRESHOLD = 100_000;

    /**
     * 用来记录每次测量出的待测量方法的消耗时间的集合，单位为纳秒(ns)，
     * 当集合中的数量超过设定的阈值时，就会清空本集合中的数据，防止内存移除.
     */
    private final List<Long> eachCosts = new ArrayList<>(3325);

    /**
     * 方差和，该值表示各个值与平均值的差的平方和.
     *
     * @since v1.2.0
     */
    private double varSum;

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
    public void update(long currSuccess, long currFailure, long currCosts, List<Long> currEachCosts) {
        // 对基础统计数据进行赋值.
        super.success = currSuccess;
        super.failure = currFailure;
        super.total = super.success + super.failure;
        super.costs = currCosts;
        super.throughput = MathKit.calcThroughput(super.total, super.costs);

        // 遍历求得所有测量值的和，最大值，最小值和平均值.
        for (Long cost : currEachCosts) {
            eachCosts.add(cost);
            super.sum += cost;
            if (super.min > cost) {
                super.min = cost;
            }
            if (super.max < cost) {
                super.max = cost;
            }
        }
        super.avg = super.sum / super.total;

        // 计算方差所需的当前所有数据的平方差之和.
        double currVarSum = this.varSum;
        for (long measure : eachCosts) {
            currVarSum += Math.pow(measure - (double) super.avg, 2);
        }

        // 分别计算出标准差和95%的置信区间半径，
        // 由于数据超过阈值之后，之前的每次花费时间的值会清空，而新的花费时间的平均值发生了变化，
        // 因此，计算的总的平法差之和并不准确，从而导致标准差也不准确，但是在大数据情况时，这些误差可以容忍.
        super.stdDev = Math.sqrt(currVarSum / super.total);
        double radius = (Z * super.stdDev) / Math.sqrt(super.total);
        this.lowerConfidence = super.avg - radius;
        this.upperConfidence = super.avg + radius;

        // 当数据量超过阈值之后，为了防止后续程序继续运行时内存溢出，就清空 eachCosts 集合中的数据.
        // 并记录当前累计的平法差之和，便于以后再累计，计算方差时使用.
        if (this.eachCosts.size() > THRESHOLD) {
            this.eachCosts.clear();
            this.varSum = currVarSum;
        }
    }

    /**
     * 更新最新的统计数据.
     *
     * <p>为了防止多个线程更新数据时的线程安全问题，这里加了写锁.</p>
     *
     * @param currSuccess 当前累计的成功运行次数
     * @param currFailure 当前累计的失败运行次数
     * @param currCosts 当前累计的总的运行时间
     * @param currEachCosts 从上次更新到本次更新期间的每次运行次数的花费时间
     * @return {@link MeasureResult} 测量出的统计结果信息
     */
    public MeasureResult updateAndGet(long currSuccess, long currFailure, long currCosts, List<Long> currEachCosts) {
        this.update(currSuccess, currFailure, currCosts, currEachCosts);
        return this.get();
    }

    /**
     * 读取最新的统计结果信息.
     *
     * <p>这里先使用乐观读的方式读取数据，如果读取期间数据发生了变化，就采用悲观读的方式来再次读取最新的数据到新的结果变量中.</p>
     *
     * @return 统计结果信息
     */
    public MeasureResult get() {
        return new MeasureResult()
                .setCosts(super.costs)
                .setTotal(super.total)
                .setSuccess(super.success)
                .setFailure(super.failure)
                .setThroughput(super.throughput)
                .setSum(super.sum)
                .setAvg(super.avg)
                .setMin(super.min)
                .setMax(super.max)
                .setStdDev(super.stdDev)
                .setLowerConfidence(super.lowerConfidence)
                .setUpperConfidence(super.upperConfidence);
    }

}
