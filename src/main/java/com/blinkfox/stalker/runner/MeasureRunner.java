package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.OverallResult;

/**
 * 用于测量待执行方法耗时情况等信息的运行器接口.
 *
 * @author blinkfox on 2019-01-08.
 * @since v1.0.0
 */
public interface MeasureRunner {

    /**
     * 执行 runnable 方法，并将执行成功与否、耗时结果等信息存入到 OverallResult 实体对象中.
     *
     * @param options 运行的配置选项实例
     * @param runnable 可运行实例
     * @return 测量结果
     */
    OverallResult run(Options options, Runnable runnable);

    /**
     * 获取当前运行任务中，每次执行的耗时情况的长整形数组.
     *
     * @return 每次执行耗时情况的长整形数组
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    long[] getEachMeasures();

    /**
     * 获取当前总共的运行次数.
     *
     * @return 运行总次数
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    long getTotal();

    /**
     * 获取当前运行成功的次数.
     *
     * @return 运行成功的次数
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    long getSuccess();

    /**
     * 获取当前运行失败的次数.
     *
     * @return 运行失败的次数
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    long getFailure();

    /**
     * 判断当前任务是否已经执行完成.
     *
     * @return 是否执行完成的布尔值
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    boolean isComplete();

    /**
     * 获取任务开始运行时的纳秒时间戳.
     *
     * @return 开始运行时间
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    long getStartNanoTime();

    /**
     * 获取任务结束运行时的纳秒时间戳.
     *
     * @return 结束运行时间
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    long getEndNanoTime();

    /**
     * 获取任务最终完成时实际所消耗的总时间.
     *
     * @return 实际所消耗的总时间
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    long getCosts();

    /**
     * 停止相关的运行测量任务.
     *
     * @return 是否成功的布尔值
     */
    boolean stop();

    /**
     * 构建运行中的任务的总体测量结果信息.
     *
     * @return 总体测量结果信息
     */
    OverallResult buildRunningMeasurement();

    /**
     * 构建运行任务完成后的最终总体测量结果信息.
     *
     * @return 总体测量结果信息
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    OverallResult buildFinalMeasurement();

}
