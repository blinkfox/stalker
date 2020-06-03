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
     * 总耗时.
     */
    private long sum;

    /**
     * 总次数.
     *
     * @since v1.2.0
     */
    private long total;

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
