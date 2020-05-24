package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.OverallResult;
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
        super.startNanoTime = System.nanoTime();

        // 单线程循环执行 runs 次.
        for (int i = 0; i < totalCount; ++i) {
            try {
                long eachStart = System.nanoTime();
                runnable.run();
                super.eachMeasures.add(System.nanoTime() - eachStart);
                super.success.incrementAndGet();
            } catch (RuntimeException e) {
                super.failure.incrementAndGet();
                if (printErrorLog) {
                    log.error("【stalker 错误】测量方法耗时信息出错!", e);
                }
            }
            super.total.incrementAndGet();
        }

        super.endNanoTime = System.nanoTime();
        super.complete.compareAndSet(false, true);
        return super.buildFinalMeasurement();
    }

}
