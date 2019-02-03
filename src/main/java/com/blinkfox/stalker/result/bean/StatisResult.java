package com.blinkfox.stalker.result.bean;

import com.blinkfox.stalker.kit.StrKit;

/**
 * 对测量的耗时时间等信息做统计分析后的统计结果实体类.
 *
 * @author blinkfox on 2019-01-05.
 */
public class StatisResult {

    /** 总耗时. */
    private long sum;

    /** 平均耗时. */
    private long avg;

    /** 最小耗时. */
    private long min;

    /** 最大耗时. */
    private long max;

    /** 标准差. */
    private double stdDev;

    /** 95%置信区间下限. */
    private double lowerConfidence;

    /** 95%置信区间上限. */
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

    /* getter and setter methods. */

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public long getAvg() {
        return avg;
    }

    public void setAvg(long avg) {
        this.avg = avg;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public double getStdDev() {
        return stdDev;
    }

    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    public double getLowerConfidence() {
        return lowerConfidence;
    }

    public void setLowerConfidence(double lowerConfidence) {
        this.lowerConfidence = lowerConfidence;
    }

    public double getUpperConfidence() {
        return upperConfidence;
    }

    public void setUpperConfidence(double upperConfidence) {
        this.upperConfidence = upperConfidence;
    }

}
