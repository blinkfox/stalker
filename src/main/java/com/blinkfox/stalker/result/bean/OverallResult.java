package com.blinkfox.stalker.result.bean;

import com.blinkfox.stalker.kit.StrKit;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 待测量方法的耗时测量的整体结果实体类.
 *
 * @author blinkfox on 2019-01-09.
 * @since v1.0.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class OverallResult {

    /**
     * 每次成功测量出的待测量方法的测量时间的集合，单位为纳秒(ns).
     */
    private long[] eachMeasures;

    /**
     * 测量代码在执行过程中所消耗的总耗时，单位为纳秒(ns).
     */
    private long costs;

    /**
     * 测量过程中执行的总次数.
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
     * 将该对象转换字符串.
     *
     * @return 字符串
     */
    @Override
    public String toString() {
        return StrKit.join("Measurement = {",
                "costs = ", StrKit.convertTime(this.costs),
                ", total = ", this.total,
                ", success = ", this.success,
                ", failure = ", this.failure,
                ", throughput = ", this.throughput,
                "}");
    }

}
