package com.blinkfox.stalker.result.bean;

/**
 * 对测量的整体结果和统计结果等信息的时间单位做了易读性处理后的结果实体类.
 *
 * @author blinkfox on 2019-01-21.
 */
public class EasyReadResult {

    /** 正式测量期间消耗的总时间. */
    private String costs;

    /** 正式测量执行的总次数. */
    private int total;

    /** 正式测量执行成功的次数. */
    private int success;

    /** 正式测量执行失败的次数. */
    private int failure;

    /** 正式执行的所有结果的总耗时. */
    private String sum;

    /** 正式执行的所有结果的平均耗时. */
    private String avg;

    /** 最小耗时. */
    private String min;

    /** 最大耗时. */
    private String max;

    /** 标准差. */
    private String stdDev;

    /** 95%置信区间下限. */
    private String lowerConfidence;

    /** 95%置信区间上限. */
    private String upperConfidence;

    /* -------- getter and setter methods. -------- */

    public String getCosts() {
        return costs;
    }

    public void setCosts(String costs) {
        this.costs = costs;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getStdDev() {
        return stdDev;
    }

    public void setStdDev(String stdDev) {
        this.stdDev = stdDev;
    }

    public String getLowerConfidence() {
        return lowerConfidence;
    }

    public void setLowerConfidence(String lowerConfidence) {
        this.lowerConfidence = lowerConfidence;
    }

    public String getUpperConfidence() {
        return upperConfidence;
    }

    public void setUpperConfidence(String upperConfidence) {
        this.upperConfidence = upperConfidence;
    }

}
