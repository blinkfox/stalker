package com.blinkfox.stalker;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.output.MeasureOutputContext;
import com.blinkfox.stalker.result.MeasurementCollector;
import com.blinkfox.stalker.result.bean.Measurement;
import com.blinkfox.stalker.runner.MeasureRunnerContext;

/**
 * Stalker的主API使用类.
 *
 * @author blinkfox on 2019-1-2.
 */
public final class Stalker {

    /**
     * 私有构造方法.
     */
    private Stalker() {}

    /**
     * 测量要执行的代码的性能评估.
     *
     * @param runnables runnable
     */
    public static void run(Runnable... runnables) {
        run(Options.of(), runnables);
    }

    /**
     * 测量要执行的各个代码的性能并输出结果.
     *
     * @param options 参数选项
     * @param runnables runnable
     */
    public static void run(Options options, Runnable... runnables) {
        int len;
        if (options == null || runnables == null || (len = runnables.length) == 0) {
            throw new IllegalArgumentException("options or runnables is null (or empty)!");
        }

        // 循环遍历测量各个 Runnable 实例的性能结果，然后将各个结果存放到数组中，最后统一输出出来.
        Measurement[] measurements = new Measurement[len];
        for (int i = 0; i < len; i++) {
            measurements[i] = new MeasurementCollector().collect(new MeasureRunnerContext(options).run(runnables[i]));
        }

        new MeasureOutputContext().output(options, measurements);
    }

}
