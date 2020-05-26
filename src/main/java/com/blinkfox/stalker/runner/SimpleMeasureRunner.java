package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.OverallResult;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

/**
 * 当待执行的实例方法是简单(单线程)情况时的测量运行实现类.
 *
 * @author blinkfox on 2019-01-08.
 * @since v1.0.0
 */
@Slf4j
public class SimpleMeasureRunner extends AbstractMeasureRunner {

    /**
     * 执行中的测量任务的 {@link Future} 实例.
     *
     * @since v1.2.0
     */
    private Future<?> measureTask;

    /**
     * 构造方法.
     */
    public SimpleMeasureRunner() {
        super();
    }

    /**
     * 执行 runnable 方法，并将执行成功与否、耗时结果等信息存入到 OverallResult 实体对象中.
     * <p>这里由于是单线程的重写方法，不再需要`new Thread`了，直接调用`runnable.run()`即可.</p>
     *
     * @param options 运行的配置选项实例
     * @param runnable 可运行实例
     * @return 测量结果
     */
    @Override
    public OverallResult run(Options options, Runnable runnable) {
        boolean printErrorLog = options.isPrintErrorLog();
        int totalCount = options.getThreads() * options.getRuns();
        super.executorService = Executors.newSingleThreadExecutor();
        super.startNanoTime = System.nanoTime();

        // 由于并发数是 1，直接单线程循环执行 (runs * threads) 次即可，
        // 将执行的相关任务以 Future 的形式来执行，便于程序动态取消任务或判断任务执行情况等.
        this.measureTask = executorService.submit(() -> {
            for (int i = 0; i < totalCount; ++i) {
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

        // 阻塞调用要执行的测量任务，达到等待任务结束的目的.
        try {
            this.measureTask.get();
        } catch (Exception e) {
            log.error("【Stalker 错误】执行测量任务发生错误！", e);
        }

        // 等待所有线程执行完毕，并关闭线程池，最后将结果封装成实体信息.
        super.endNanoTime = System.nanoTime();
        super.complete.compareAndSet(false, true);
        super.shutdown();
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
        if (isComplete()) {
            log.info("【Stalker 提示】任务已完成，将不再暂停测量任务.");
            return true;
        }

        super.endNanoTime = System.nanoTime();
        super.complete.compareAndSet(false, true);

        // 立即关闭线程池.
        super.shutdownNowQuietly();
        // 取消正在执行中的任务.
        if (this.measureTask != null && !this.measureTask.isDone()) {
            return this.measureTask.cancel(true);
        }
        return true;
    }

}
