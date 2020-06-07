package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.MeasureResult;

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
     * @return 测量的统计结果
     */
    MeasureResult run(Options options, Runnable runnable);

    /**
     * 判断当前任务是否已经执行完成.
     *
     * @return 是否执行完成的布尔值
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    boolean isCompleted();

    /**
     * 判断当前任务是否已经被取消.
     *
     * @return 是否被取消的布尔值
     * @author blinkfox on 2020-06-02.
     * @since v1.2.0
     */
    boolean isCancelled();

    /**
     * 获取当前已经运行的总次数.
     *
     * @return 运行总次数
     * @author blinkfox on 2020-06-06.
     * @since v1.2.0
     */
    long getTotal();

    /**
     * 获取当前测量任务已经运行的总花费时间.
     *
     * @return 运行总花费时间
     * @author blinkfox on 2020-06-06.
     * @since v1.2.0
     */
    long getCosts();

    /**
     * 获取到当前时的运行成功的次数.
     *
     * @return 运行成功的次数
     * @author blinkfox on 2020-06-06.
     * @since v1.2.0
     */
    long getSuccess();

    /**
     * 获取当前运行失败的次数.
     *
     * @return 运行失败的次数
     * @author blinkfox on 2020-06-06.
     * @since v1.2.0
     */
    long getFailure();

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
     * 停止相关的运行测量任务.
     *
     * @author blinkfox on 2020-06-03.
     * @since v1.2.0
     */
    void stop();

    /**
     * 获取运行中的任务的测量统计结果信息.
     *
     * @return 统计结果信息
     * @author blinkfox on 2020-06-05.
     * @since v1.2.0
     */
    MeasureResult getMeasureResult();

}
