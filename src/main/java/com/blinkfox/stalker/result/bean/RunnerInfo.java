package com.blinkfox.stalker.result.bean;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.runner.MeasureRunner;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 可运行任务的相关的信息实体.
 *
 * @author blinkfox on 2020-05-24.
 * @since v1.2.0
 */
@Getter
@AllArgsConstructor
public class RunnerInfo {

    /**
     * 可运行任务的选项参数信息.
     */
    private final Options options;

    /**
     * 任务运行的 {@link MeasureRunner} 实例.
     */
    private final MeasureRunner measureRunner;

}
