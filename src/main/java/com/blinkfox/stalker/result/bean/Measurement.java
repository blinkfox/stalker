package com.blinkfox.stalker.result.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 正式测量结果的实体类.
 *
 * @author blinkfox on 2019-01-22.
 * @since v1.0.0
 */
@Getter
@Setter
public class Measurement {

    /**
     * 正式测量的整体结果信息.
     */
    private OverallResult overallResult;

    /**
     * 正式测量的统计结果信息.
     */
    private StatisResult statisResult;

    /**
     * 正式测量结果和统计结果信息转换成的易读的结果信息.
     */
    private EasyReadResult easyReadResult;

    /**
     * 构造方法.
     *
     * @param overallResult 整体测量结果
     */
    public Measurement(OverallResult overallResult) {
        this.overallResult = overallResult;
    }

}
