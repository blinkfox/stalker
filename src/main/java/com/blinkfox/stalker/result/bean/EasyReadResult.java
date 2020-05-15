package com.blinkfox.stalker.result.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 对测量的整体结果和统计结果等信息的时间单位做了易读性处理后的结果实体类.
 *
 * @author blinkfox on 2019-01-21.
 * @since v1.0.0
 */
@Getter
@Setter
public class EasyReadResult {

    /**
     * 正式测量期间消耗的总时间.
     */
    private String costs;

    /**
     * 正式测量执行的总次数.
     */
    private long total;

    /**
     * 正式测量执行成功的次数.
     */
    private long success;

    /**
     * 正式测量执行失败的次数.
     */
    private long failure;

    /**
     * 吞吐率，指单位时间内（每秒）的执行总次数.
     *
     * @since v1.1.1
     */
    private String throughput;

    /**
     * 正式执行的所有结果的总耗时.
     */
    private String sum;

    /**
     * 正式执行的所有结果的平均耗时.
     */
    private String avg;

    /**
     * 最小耗时.
     */
    private String min;

    /**
     * 最大耗时.
     */
    private String max;

    /**
     * 标准差.
     */
    private String stdDev;

    /**
     * 95%置信区间下限.
     */
    private String lowerConfidence;

    /**
     * 95%置信区间上限.
     */
    private String upperConfidence;

}
