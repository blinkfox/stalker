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

}
