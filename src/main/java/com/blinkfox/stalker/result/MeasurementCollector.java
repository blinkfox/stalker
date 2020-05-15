package com.blinkfox.stalker.result;

import com.blinkfox.stalker.kit.StrKit;
import com.blinkfox.stalker.result.bean.EasyReadResult;
import com.blinkfox.stalker.result.bean.Measurement;
import com.blinkfox.stalker.result.bean.OverallResult;
import com.blinkfox.stalker.result.bean.StatisResult;
import com.blinkfox.stalker.result.statis.DefaultMeasureStatis;

/**
 * 测量结果信息的收集器类，即将测量结果、统计结果等收集、整合起来.
 *
 * @author blinkfox on 2019-01-22.
 * @since v1.0.0
 */
public final class MeasurementCollector {

    /**
     * 根据正式测量的总体结果信息，统计、收集整理出更全面的测量结果信息.
     *
     * @param overallResult 总体结果
     * @return 测量总结果
     */
    public Measurement collect(OverallResult overallResult) {
        Measurement measurement = new Measurement(overallResult);
        StatisResult statisResult = new DefaultMeasureStatis().statis(overallResult);
        measurement.setStatisResult(statisResult);
        measurement.setEasyReadResult(this.buildEasyReadResult(overallResult, statisResult));
        return measurement;
    }

    /**
     * 由于测量结果单位均为纳秒，这里需要构建使人易读的测量结果信息 EasyReadResult 对象.
     *
     * @param overallResult 总体结果
     * @param statisResult 统计结果
     * @return 易读的结果
     */
    private EasyReadResult buildEasyReadResult(OverallResult overallResult, StatisResult statisResult) {
        EasyReadResult easyReadResult = new EasyReadResult();
        easyReadResult.setCosts(StrKit.convertTime(overallResult.getCosts()));
        easyReadResult.setTotal(overallResult.getTotal());
        easyReadResult.setSuccess(overallResult.getSuccess());
        easyReadResult.setFailure(overallResult.getFailure());
        easyReadResult.setThroughput(StrKit.roundToString(overallResult.getThroughput()));

        easyReadResult.setSum(StrKit.convertTime(statisResult.getSum()));
        easyReadResult.setAvg(StrKit.convertTime(statisResult.getAvg()));
        easyReadResult.setMin(StrKit.convertTime(statisResult.getMin()));
        easyReadResult.setMax(StrKit.convertTime(statisResult.getMax()));
        easyReadResult.setStdDev(StrKit.convertTime(statisResult.getStdDev()));
        easyReadResult.setLowerConfidence(StrKit.convertTime(statisResult.getLowerConfidence()));
        easyReadResult.setUpperConfidence(StrKit.convertTime(statisResult.getUpperConfidence()));
        return easyReadResult;
    }

}
