package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.OverallResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 当待执行的实例方法是简单(单线程)情况时的测量运行实现类.
 *
 * @author blinkfox on 2019-01-08.
 */
public class SimpleMeasureRunner implements MeasureRunner {

    private static final Logger log = LoggerFactory.getLogger(SimpleMeasureRunner.class);

    /** 每次'成功'测量出的待测量方法的耗时时间，单位为纳秒(ns). */
    private long[] eachMeasures;

    /** 测量过程中执行的总次数. */
    private int total;

    /** 测量过程中执行成功的次数. */
    private int success;

    /** 测量过程中执行失败的次数. */
    private int failure;

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
        int runs = options.getRuns();
        boolean printErrorLog = options.isPrintErrorLog();
        this.eachMeasures = new long[runs];
        final long start = System.nanoTime();

        // 单线程循环执行 runs 次.
        for (int i = 0; i < runs; i++) {
            this.total++;
            try {
                long eachStart = System.nanoTime();
                runnable.run();
                this.eachMeasures[i] = System.nanoTime() - eachStart;
                this.success++;
            } catch (RuntimeException e) {
                this.failure++;
                if (printErrorLog) {
                    log.error("测量方法耗时信息出错!", e);
                }
            }
        }

        return this.buildMeasurement(System.nanoTime() - start);
    }

    /**
     * 构造测量的结果信息的 Measurement 对象.
     *
     * @param costs 消耗的总耗时，单位是纳秒
     * @return Measurement对象
     */
    private OverallResult buildMeasurement(long costs) {
        return new OverallResult()
                .setEachMeasures(this.eachMeasures)
                .setCosts(costs)
                .setTotal(this.total)
                .setSuccess(this.success)
                .setFailure(this.failure);
    }

}
