package com.blinkfox.stalker.result.statis;

import com.blinkfox.stalker.result.bean.OverallResult;
import com.blinkfox.stalker.result.bean.StatisResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的测量结果统计实现.
 *
 * @author blinkfox on 2019-1-10.
 */
public class DefaultMeasureStatis implements MeasureStatis {

    /** 95%置信区间的 Z 值. */
    private static final double Z = 1.96;

    /**
     * 将测量结果数据做统计分析，得出性能结果数据.
     *
     * @param overallResult 测量结果数据
     * @return 性能结果数据
     */
    @Override
    public StatisResult statis(OverallResult overallResult) {
        StatisResult statisResult = new StatisResult();
        long[] eachMeasures = overallResult.getEachMeasures();
        if (eachMeasures == null || eachMeasures.length == 0) {
            return statisResult;
        }

        // 计算出测量结果的相关统计结果.
        return this.buildOtherResult(this.buildBaseResult(statisResult, eachMeasures), eachMeasures);
    }

    /**
     * 构建基础统计结果信息.
     *
     * @param statisResult 性能结果对象
     * @param eachMeasures 每次测量值的数组
     * @return 性能结果对象
     */
    private StatisResult buildBaseResult(StatisResult statisResult, long[] eachMeasures) {
        long sum = 0;
        long min = eachMeasures[0];
        long max = min;

        // 遍历求得所有测量值的和，最大值，最小值.
        for (long measure : eachMeasures) {
            sum += measure;
            if (min > measure) {
                min = measure;
            }
            if (max < measure) {
                max = measure;
            }
        }

        statisResult.setMin(min);
        statisResult.setMax(max);
        statisResult.setSum(sum);
        statisResult.setAvg(sum / eachMeasures.length);
        return statisResult;
    }

    /**
     * 构建其它统计结果信息.
     *
     * @param statisResult 性能结果对象
     * @param eachMeasures 每次测量值的数组
     * @return 性能结果对象
     */
    private StatisResult buildOtherResult(StatisResult statisResult, long[] eachMeasures) {
        // 计算方差所需的平方和.
        long s = 0;
        long avg = statisResult.getAvg();
        for (long measure : eachMeasures) {
            s += Math.pow(measure - avg, 2);
        }

        // 分别计算出标准差和95%的置信区间半径.
        long n = eachMeasures.length;
        double stdDev = Math.sqrt((double) s / n);
        double radius = (Z * stdDev) / Math.sqrt(n);

        statisResult.setStdDev(stdDev);
        statisResult.setLowerConfidence(avg - radius);
        statisResult.setUpperConfidence(avg + radius);
        return statisResult;
    }

}
