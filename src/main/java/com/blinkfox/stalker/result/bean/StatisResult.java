package com.blinkfox.stalker.result.bean;

import com.blinkfox.stalker.kit.StrKit;
import lombok.Getter;
import lombok.Setter;

/**
 * 对测量的耗时时间等信息做统计分析后的统计结果实体类.
 *
 * @author blinkfox on 2019-01-05.
 * @since v1.0.0
 */
@Getter
@Setter
public class StatisResult {

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
     *
     * @since v1.1.1
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

    @Override
    public String toString() {
        return StrKit.join("MeasureStatisResult = {",
                ", sum = ", StrKit.convertTime(this.sum),
                ", avg = ", StrKit.convertTime(this.avg),
                ", min = ", StrKit.convertTime(this.min),
                ", max = ", StrKit.convertTime(this.max),
                ", stdDev = ", StrKit.convertTime(this.stdDev),
                ", lowerConfidence = ", StrKit.convertTime(this.lowerConfidence),
                ", upperConfidence = ", StrKit.convertTime(this.upperConfidence),
                "}.");
    }

}
