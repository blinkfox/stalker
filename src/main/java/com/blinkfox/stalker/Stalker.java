package com.blinkfox.stalker;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.output.MeasureOutputContext;
import com.blinkfox.stalker.result.StalkerFuture;
import com.blinkfox.stalker.result.StatisResult;
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
     * 使用默认选项参数来提交可运行的异步测量任务，并立即返回此次异步任务的 {@link StalkerFuture} 实例.
     *
     * @param task 任务
     * @return {@link StalkerFuture} 对象实例
     * @author blinkfox on 2020-06-03
     * @since v1.2.0
     */
    public StalkerFuture submit(Runnable task) {
        return submit(Options.of(), task);
    }

    /**
     * 提交可运行的异步测量任务，并立即返回此次异步任务的 {@link StalkerFuture} 实例.
     *
     * <p>异步提交任务时，将默认额外开启定时更新统计数据的定时任务.</p>
     *
     * @param options 选项参数
     * @param task 任务
     * @return {@link StalkerFuture} 对象实例
     * @author blinkfox on 2020-06-03
     * @since v1.2.0
     */
    public StalkerFuture submit(Options options, Runnable task) {
        if (options == null || task == null) {
            throw new IllegalArgumentException("options or runnables is null (or empty)!");
        }
        options.valid();

        // 异步提交任务时，将默认额外开启定时更新统计数据的定时任务.
        options.enableScheduledUpdater();
        return MeasureRunnerContext.submit(options, task);
    }

    /**
     * 测量要执行的代码的性能评估.
     *
     * @param runnables runnable
     * @return 运行的输出结果集合，v1.1.0 新增的返回结果
     */
    public List<Object> run(Runnable... runnables) {
        return run(Options.of(), runnables);
    }

    /**
     * 测量要执行的各个代码的性能并输出结果.
     *
     * @param options 参数选项
     * @param runnables runnable
     * @return 运行的输出结果集合，v1.1.0 新增的返回结果
     */
    public List<Object> run(Options options, Runnable... runnables) {
        return new MeasureOutputContext().output(options, runStatis(options, runnables));
    }

    /**
     * 测量要执行的各个代码的性能并输出统计数据的结果数组.
     *
     * @param options 参数选项
     * @param runnables 可运行的任务
     * @return 各个运行结果统计数据的数组
     * @author blinkfox on 2020-05-14
     * @since v1.1.0
     */
    public StatisResult[] runStatis(Options options, Runnable... runnables) {
        int len;
        if (options == null || runnables == null || (len = runnables.length) == 0) {
            throw new IllegalArgumentException("【Stalker 参数异常】options or runnables is null (or empty)!");
        }

        // 循环遍历测量各个 Runnable 实例的性能结果，然后将各个结果存放到数组中，最后统一输出出来.
        StatisResult[] measurements = new StatisResult[len];
        for (int i = 0; i < len; i++) {
            measurements[i] = new MeasureRunnerContext(options).run(runnables[i]);
        }
        return measurements;
    }

}
