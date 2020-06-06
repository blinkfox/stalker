package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.kit.StrKit;
import com.blinkfox.stalker.result.StalkerFuture;
import com.blinkfox.stalker.result.StatisResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 待执行的实例方法在各种线程情形中运行实现类的上下文.
 *
 * @author blinkfox on 2019-01-08.
 * @since v1.0.0
 */
@Slf4j
public final class MeasureRunnerContext {

    /**
     * 运行测量的性能参数配置选项.
     */
    private final Options options;

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
    private static void warmup(Options options, Runnable runnable) {
        final boolean printErrorLog = options.isPrintErrorLog();
        log.debug("【stalker 提示】预热开始...");
        long start = System.nanoTime();

        // 循环执行预热次数的方法.
        for (int i = 0, len = options.getWarmups(); i < len; i++) {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                // 预热时的异常日志，根据配置选项的参数值来看是否输出错误日志.
                if (printErrorLog) {
                    log.error("【stalker 错误】测量方法前进行预热时出错!", e);
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("【stalker 提示】预热完毕，预热期间耗时: {}.", StrKit.convertTime(System.nanoTime() - start));
        }
    }

    /**
     * 检查Options参数是否合法，并进行预热准备，然后执行 runnable 方法，并将执行结果的耗时纳秒(ns)值存入到集合中.
     *
     * @param runnable 可运行实例
     * @return 运行的测量统计结果信息
     */
    public StatisResult run(Runnable runnable) {
        warmup(options, runnable);
        if (options.getDuration() != null) {
            return options.getConcurrens() > 1
                    ? new ConcurrentScheduledMeasureRunner().run(options, runnable)
                    : new SimpleScheduledMeasureRunner().run(options, runnable);
        } else {
            return options.getConcurrens() > 1
                    ? new ConcurrentMeasureRunner().run(options, runnable)
                    : new SimpleMeasureRunner().run(options, runnable);
        }
    }

    /**
     * 检查Options参数是否合法，并进行预热准备，然后执行 runnable 方法，并将执行结果的耗时纳秒(ns)值存入到集合中.
     *
     * @param options 运行的选项参数
     * @param runnable 可运行实例
     * @return 此次运行的会话 ID
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    public static StalkerFuture submit(final Options options, final Runnable runnable) {
        // 预热运行.
        warmup(options, runnable);

        // 获取对应的 measureRunner，并将 measureRunner 存储到 map 中，并异步执行任务.
        MeasureRunner measureRunner;
        if (options.getDuration() != null) {
            measureRunner = options.getConcurrens() > 1
                    ? new ConcurrentScheduledMeasureRunner()
                    : new SimpleScheduledMeasureRunner();
        } else {
            measureRunner = options.getConcurrens() > 1
                    ? new ConcurrentMeasureRunner()
                    : new SimpleMeasureRunner();
        }

        // 构造 StalkerFuture 对象，并开始运行任务.
        StalkerFuture stalkerFuture = new StalkerFuture(options, runnable, measureRunner);
        stalkerFuture.run();
        return stalkerFuture;
    }

}
