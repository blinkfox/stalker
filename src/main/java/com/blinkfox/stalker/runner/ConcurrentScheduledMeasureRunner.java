package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.OverallResult;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * ConcurrentScheduledMeasureRunner.
 *
 * @author blinkfox on 2020-06-01.
 * @since v1.0.0
 */
public class ConcurrentScheduledMeasureRunner extends ConcurrentMeasureRunner {

    /**
     * 用于异步定时调度任务的线程池.
     */
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * 构造方法.
     *
     * <p>这个类中的属性，需要支持高并发写入.</p>
     */
    public ConcurrentScheduledMeasureRunner() {
        super();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * 持续并发的执行指定时间的 runnable 方法，并将执行成功与否、耗时结果等信息存入到 OverallResult 实体对象中.
     *
     * @param options 运行的配置选项实例
     * @param runnable 可运行实例
     * @return 测量结果
     */
    @Override
    public OverallResult run(Options options, Runnable runnable) {
        return null;
    }

}
