package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.kit.StrKit;
import com.blinkfox.stalker.result.bean.OverallResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 待执行的实例方法在各种线程情形中运行实现类的上下文.
 *
 * @author blinkfox on 2019-01-08.
 */
public final class MeasureRunnerContext {

    private static final Logger log = LoggerFactory.getLogger(MeasureRunnerContext.class);

    /** 运行测量的性能参数配置选项. */
    private Options options;

    /**
     * 基于Options的构造方法，其中需要对options参数的合法性做校验.
     *
     * @param options 参数选项
     */
    public MeasureRunnerContext(Options options) {
        options.valid();
        this.options = options;
    }

    /**
     * 正式测量前所需要进行预热的方法.
     *
     * @param options 参数选项
     * @param runnable runnable
     */
    private void warmup(Options options, Runnable runnable) {
        final boolean printErrorLog = options.isPrintErrorLog();
        log.debug("预热开始...");
        long start = System.nanoTime();

        // 循环执行预热次数的方法.
        for (int i = 0, len = options.getWarmups(); i < len; i++) {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                // 预热时的异常日志，根据配置选项的参数值来看是否输出错误日志.
                if (printErrorLog) {
                    log.error("测量方法前进行预热时出错!", e);
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("预热完毕，预热期间耗时: {}.", StrKit.convertTime(System.nanoTime() - start));
        }
    }

    /**
     * 检查Options参数是否合法，并进行预热准备，然后执行 runnable 方法，并将执行结果的耗时纳秒(ns)值存入到集合中.
     *
     * @param runnable 可运行实例
     * @return 运行的测量结果
     */
    public OverallResult run(Runnable runnable) {
        this.warmup(options, runnable);
        return options.getThreads() > 1
                ? new ConcurrentMeasureRunner().run(options, runnable)
                : new SimpleMeasureRunner().run(options, runnable);
    }

}
