package com.blinkfox.stalker.result.bean;

import com.blinkfox.stalker.kit.StrKit;

/**
 * 待测量方法的耗时测量的整体结果实体类.
 *
 * @author blinkfox on 2019-01-09.
 */
public class OverallResult {

    /** 每次成功测量出的待测量方法的测量时间的集合，单位为纳秒(ns). */
    private long[] eachMeasures;

    /** 测量代码在执行过程中所消耗的总耗时，单位为纳秒(ns). */
    private long costs;

    /** 测量过程中执行的总次数. */
    private int total;

    /** 测量过程中执行成功的次数. */
    private int success;

    /** 测量过程中执行失败的次数. */
    private int failure;

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
                "}");
    }

    /* getter and setter methods. */

    public long[] getEachMeasures() {
        return eachMeasures;
    }

    public OverallResult setEachMeasures(long[] eachMeasures) {
        this.eachMeasures = eachMeasures;
        return this;
    }

    public long getCosts() {
        return costs;
    }

    public OverallResult setCosts(long costs) {
        this.costs = costs;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public OverallResult setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getSuccess() {
        return success;
    }

    public OverallResult setSuccess(int success) {
        this.success = success;
        return this;
    }

    public int getFailure() {
        return failure;
    }

    public OverallResult setFailure(int failure) {
        this.failure = failure;
        return this;
    }

}
