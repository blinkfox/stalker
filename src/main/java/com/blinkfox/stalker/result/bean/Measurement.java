package com.blinkfox.stalker.result.bean;

/**
 * 正式测量结果的实体类.
 *
 * @author blinkfox on 2019-01-22.
 */
public class Measurement {

    /** 正式测量的整体结果信息. */
    private OverallResult overallResult;

    /** 正式测量的统计结果信息. */
    private StatisResult statisResult;

    /** 正式测量结果和统计结果信息转换成的易读的结果信息. */
    private EasyReadResult easyReadResult;

    /**
     * 构造方法.
     *
     * @param overallResult 整体测量结果
     */
    public Measurement(OverallResult overallResult) {
        this.overallResult = overallResult;
    }

    /* -------- getter and setter methods. -------- */

    public OverallResult getOverallResult() {
        return overallResult;
    }

    public StatisResult getStatisResult() {
        return statisResult;
    }

    public void setStatisResult(StatisResult statisResult) {
        this.statisResult = statisResult;
    }

    public EasyReadResult getEasyReadResult() {
        return easyReadResult;
    }

    public void setEasyReadResult(EasyReadResult easyReadResult) {
        this.easyReadResult = easyReadResult;
    }

}
