package com.blinkfox.stalker;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.output.MeasureOutputContext;
import com.blinkfox.stalker.result.MeasurementCollector;
import com.blinkfox.stalker.result.bean.Measurement;
import com.blinkfox.stalker.runner.MeasureRunnerContext;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * Stalker 的主 API 使用类.
 *
 * @author blinkfox on 2019-01-02.
 * @since v1.0.0
 */
@UtilityClass
public class Stalker {

    /**
     * 测量要执行的代码的性能评估.
     *
     * @param runnables runnable
     * @return 运行的输出结果集合
     */
    public List<Object> run(Runnable... runnables) {
        return run(Options.of(), runnables);
    }

    /**
     * 测量要执行的各个代码的性能并输出结果.
     *
     * @param options 参数选项
     * @param runnables runnable
     * @return 运行的输出结果集合
     */
    public List<Object> run(Options options, Runnable... runnables) {
        return new MeasureOutputContext().output(options, measure(options, runnables));
    }

    /**
     * 测量要执行的各个代码的性能并输出统计数据的结果数组.
     *
     * @param options 参数选项
     * @param runnables runnable
     * @return 各个运行结果统计数据的数组
     * @author blinkfox on 2020-05-14
     * @since v1.1.0
     */
    public Measurement[] measure(Options options, Runnable... runnables) {
        int len;
        if (options == null || runnables == null || (len = runnables.length) == 0) {
            throw new IllegalArgumentException("options or runnables is null (or empty)!");
        }

        // 循环遍历测量各个 Runnable 实例的性能结果，然后将各个结果存放到数组中，最后统一输出出来.
        Measurement[] measurements = new Measurement[len];
        for (int i = 0; i < len; i++) {
            measurements[i] = new MeasurementCollector().collect(new MeasureRunnerContext(options).run(runnables[i]));
        }
        return measurements;
    }

}
