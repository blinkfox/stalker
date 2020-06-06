package com.blinkfox.stalker.result;

import com.blinkfox.stalker.kit.StrKit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 对测量的耗时时间等信息做统计分析后的测量统计结果实体类.
 *
 * @author blinkfox on 2019-01-05.
 * @see MeasureStatistician
 * @since v1.2.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class MeasureResult {

    /**
     * 测量代码在执行过程中所消耗的总耗时，单位为纳秒(ns).
     */
    protected long costs;

    /**
     * 总次数.
     */
    protected long total;

    /**
     * 测量过程中执行成功的次数.
     */
    protected long success;

    /**
     * 测量过程中执行失败的次数.
     */
    protected long failure;

    /**
     * 吞吐率，指单位时间内（每秒）的执行总次数，即：{@code throughput = total / (costs / 10^9)}.
     *
     * @since v1.1.1
     */
    protected double throughput;

    /**
     * 总耗时.
     */
    protected long sum;

    /**
     * 平均耗时.
     */
    protected long avg;

    /**
     * 最小耗时.
     */
    protected long min;

    /**
     * 最大耗时.
     */
    protected long max;

    /**
     * 标准差.
     */
    protected double stdDev;

    /**
     * 95%置信区间下限.
     */
    protected double lowerConfidence;

    /**
     * 95%置信区间上限.
     */
    protected double upperConfidence;

    /**
     * 获取易于人阅读的实际任务运行总时间字符串.
     *
     * @return 实际任务运行总时间字符串
     */
    public String getEasyReadCosts() {
        return StrKit.convertTime(this.costs);
    }

    /**
     * 获取易于人阅读的吞吐量字符串.
     *
     * @return 吞吐量字符串
     */
    public String getEasyReadThroughput() {
        return StrKit.roundToString(this.throughput);
    }

    /**
     * 获取易于人阅读的每次测量所花费时间总和的字符串.
     *
     * @return 花费时间总和的字符串
     */
    public String getEasyReadSum() {
        return StrKit.convertTime(this.sum);
    }

    /**
     * 获取易于人阅读的每次测量所花费的平均时间总和的字符串.
     *
     * @return 花费的平均时间的字符串
     */
    public String getEasyReadAvg() {
        return StrKit.convertTime(this.avg);
    }

    /**
     * 获取易于人阅读的测量所花费的最小时间的字符串.
     *
     * @return 所花费的最小时间的字符串
     */
    public String getEasyReadMin() {
        return StrKit.convertTime(this.min);
    }

    /**
     * 获取易于人阅读的测量所花费的最大时间的字符串.
     *
     * @return 所花费的最大时间的字符串
     */
    public String getEasyReadMax() {
        return StrKit.convertTime(this.max);
    }

    /**
     * 获取易于人阅读的测量所花费的时间标准差的字符串.
     *
     * @return 所花费的时间标准差的字符串
     */
    public String getEasyReadStdDev() {
        return StrKit.convertTime(this.stdDev);
    }

    /**
     * 获取易于人阅读的测量所花费时间的 95% 置信区间下界的时间字符串.
     *
     * @return 95% 置信区间下界的时间字符串
     */
    public String getEasyReadLowerConfidence() {
        return StrKit.convertTime(this.lowerConfidence);
    }

    /**
     * 获取易于人阅读的测量所花费时间的 95% 置信区间上界的字符串.
     *
     * @return 95% 置信区间上界的时间字符串
     */
    public String getEasyReadUpperConfidence() {
        return StrKit.convertTime(this.upperConfidence);
    }

    /**
     * 将对象转换为字符串.
     *
     * @return 字符串
     */
    @Override
    public String toString() {
        return StrKit.join("MeasureResult = {",
                ", costs = ", this.getEasyReadCosts(),
                ", total = ", this.getTotal(),
                ", success = ", this.getSuccess(),
                ", failure = ", this.getFailure(),
                ", throughput = ", this.getEasyReadThroughput(),
                ", sum = ", this.getEasyReadSum(),
                ", avg = ", this.getEasyReadAvg(),
                ", min = ", this.getEasyReadMin(),
                ", max = ", this.getEasyReadMax(),
                ", stdDev = ", this.getEasyReadStdDev(),
                ", lowerConfidence = ", this.getEasyReadLowerConfidence(),
                ", upperConfidence = ", this.getEasyReadUpperConfidence(),
                "}.");
    }

}
