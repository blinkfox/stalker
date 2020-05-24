package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.OverallResult;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
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
        super.countLatch = new CountDownLatch(1);
        super.executorService = Executors.newSingleThreadExecutor();
        super.startNanoTime = System.nanoTime();

        // 由于并发数是 1，直接单线程循环执行 (runs * threads) 次即可.
        executorService.submit(() -> {
            for (int i = 0; i < totalCount; ++i) {
                try {
                    long eachStart = System.nanoTime();
                    runnable.run();
                    super.eachMeasures.add(System.nanoTime() - eachStart);
                    super.success.increment();
                } catch (Exception e) {
                    super.failure.increment();
                    if (printErrorLog) {
                        log.error("【stalker 错误】测量方法耗时信息出错!", e);
                    }
                } finally {
                    super.total.increment();
                }
            }
            super.countLatch.countDown();
        });

        // 等待所有线程执行完毕，并关闭线程池，最后将结果封装成实体信息.
        this.awaitAndShutdown();
        super.endNanoTime = System.nanoTime();
        super.complete.compareAndSet(false, true);
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
        // TODO
        return false;
    }

}
