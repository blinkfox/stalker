package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.config.RunDuration;
import com.blinkfox.stalker.result.bean.OverallResult;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;

/**
 * 继承自 {@link SimpleMeasureRunner}，在单线程情况下的运行指定的持续时间的测量运行器.
 *
 * @author blinkfox on 2020-06-01.
 * @since v1.2.0
 */
@Slf4j
public class SimpleScheduledMeasureRunner extends SimpleMeasureRunner {

    /**
     * 用于异步定时调度任务的线程池.
     */
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * 执行中的测量任务的 {@link Future} 实例.
     *
     * @since v1.2.0
     */
    protected Future<?> scheduledFuture;

    /**
     * 构造方法.
     */
    public SimpleScheduledMeasureRunner() {
        super();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * 持续执行指定时间的 runnable 方法，并将执行成功与否、耗时结果等信息存入到 OverallResult 实体对象中.
     *
     * @param options 运行的配置选项实例
     * @param runnable 可运行实例
     * @return 测量结果
     */
    @Override
    public OverallResult run(Options options, Runnable runnable) {
        boolean printErrorLog = options.isPrintErrorLog();
        super.executorService = Executors.newSingleThreadExecutor();
        super.startNanoTime = System.nanoTime();

        // 将单线程中执行的任务放在 while 循环中，一直执行下去.
        super.measureTask = executorService.submit(() -> {
            while (true) {
                try {
                    // 开始执行测量任务，记录开始时间、执行次数等.
                    long eachStart = System.nanoTime();
                    runnable.run();
                    super.eachMeasures.add(System.nanoTime() - eachStart);
                    super.success.increment();
                } catch (Exception e) {
                    super.failure.increment();
                    if (printErrorLog) {
                        log.error("【stalker 错误】测量方法耗时信息出错!", e);
                    }
                }
            }
        });

        // 到指定的持续时间之后，就取消执行中的任务,并关闭线程池.
        final RunDuration duration = options.getDuration();
        this.scheduledFuture = this.scheduledExecutorService.schedule((Runnable) this::stop,
                duration.getAmount(), duration.getTimeUnit());

        // 阻塞调用要执行的测量任务，达到阻塞等待任务结束的目的.
        try {
            this.measureTask.get();
        } catch (Exception e) {
            log.error("【Stalker 错误】执行测量任务发生错误！", e);
        }
        return super.buildFinalMeasurement();
    }

    /**
     * 停止相关的运行测量任务.
     *
     * @return 是否成功的布尔值
     * @author blinkfox on 2020-05-25.
     * @since v1.2.0
     */
    public boolean stop() {
        super.stop();

        // 关闭定时任务线程池和取消对应的定时任务.
        this.scheduledExecutorService.shutdown();
        if (this.scheduledFuture != null && !this.scheduledFuture.isDone()) {
            return this.scheduledFuture.cancel(true);
        }
        return true;
    }

}
