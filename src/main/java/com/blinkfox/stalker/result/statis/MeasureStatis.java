package com.blinkfox.stalker.result.statis;

import com.blinkfox.stalker.result.bean.OverallResult;
import com.blinkfox.stalker.result.bean.StatisResult;

/**
 * 测量结果统计接口.
 *
 * @author blinkfox on 2019-1-10.
 */
public interface MeasureStatis {

    /**
     * 将测量结果数据做统计分析，得出性能结果数据.
     *
     * @param overallResult 测量结果数据
     * @return 性能结果数据
     */
    StatisResult statis(OverallResult overallResult);

}