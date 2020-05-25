package com.blinkfox.stalker;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.output.MeasureOutputContext;
import com.blinkfox.stalker.result.bean.Measurement;
import com.blinkfox.stalker.runner.MeasureRunnerContext;
import java.util.List;
import java.util.Set;
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
     * 使用默认选项参数来提交可运行的测量任务，并立即返回此次会话的 ID.
     *
     * @param task 任务
     * @return 会话 ID
     * @author blinkfox on 2020-05-23
     * @since v1.2.0
     */
    public String submit(Runnable task) {
        return submit(Options.of(), task);
    }

    /**
     * 提交可运行的测量任务，并立即返回此次会话的 ID.
     *
     * @param options 选项参数
     * @param task 任务
     * @return 会话 ID
     * @author blinkfox on 2020-05-23
     * @since v1.2.0
     */
    public String submit(Options options, Runnable task) {
        if (options == null || task == null) {
            throw new IllegalArgumentException("options or runnables is null (or empty)!");
        }
        options.valid();
        return MeasureRunnerContext.submit(options, task);
    }

    /**
     * 根据会话的 ID 查询其对应的运行任务的测量结果数据，本结果是在 {@link Options#getOutputs()} 的属性中
     * {@link com.blinkfox.stalker.output.MeasureOutput} 接口定义的输出结果.
     *
     * @param sessionId 会话 ID
     * @return 运行的最终输出结果
     * @author blinkfox on 2020-05-23
     * @since v1.2.0
     */
    public List<Object> query(String sessionId) {
        return MeasureRunnerContext.query(sessionId);
    }

    /**
     * 根据运行的测量会话 ID，判断任务是否在运行中，即时该任务也许不存在，查找不到时，也会认为是 {@code true}.
     *
     * @param sessionId 会话 ID
     * @return 布尔值
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    public boolean isRunning(String sessionId) {
        return MeasureRunnerContext.isRunning(sessionId);
    }

    /**
     * 根据运行的测量会话 ID，移除相关的运行任务记录.目前不会停止任务，只是从缓存中移除任务记录.
     *
     * @param sessionId 会话 ID
     * @author blinkfox on 2020-05-23
     * @since v1.2.0
     */
    public void remove(String sessionId) {
        MeasureRunnerContext.remove(sessionId);
    }

    /**
     * 根据运行的测量会话 ID，停止相关的测量任务.
     *
     * @param sessionId 会话 ID
     * @author blinkfox on 2020-05-23
     * @since v1.2.0
     */
    public void stop(String sessionId) {
        MeasureRunnerContext.stop(sessionId);
    }

    /**
     * 根据会话的 ID 查询其对应的运行任务的测量结果数据.
     *
     * @param sessionId 会话 ID
     * @return 运行的测量结果
     * @author blinkfox on 2020-05-23
     * @since v1.2.0
     */
    public Measurement queryMeasurement(String sessionId) {
        return MeasureRunnerContext.queryMeasurement(sessionId);
    }

    /**
     * 清除所有测量任务记录.
     *
     * @author blinkfox on 2020-05-26.
     * @since v1.2.0
     */
    public void clear() {
        MeasureRunnerContext.clear();
    }

    /**
     * 获取所有测量任务记录的 Session ID 集合.
     *
     * @return Session ID 集合
     * @author blinkfox on 2020-05-26.
     * @since v1.2.0
     */
    public Set<String> getAllSessions() {
        return MeasureRunnerContext.getAllSessions();
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
     * @param runnables runnable
     * @return 各个运行结果统计数据的数组
     * @author blinkfox on 2020-05-14
     * @since v1.1.0
     */
    public Measurement[] runStatis(Options options, Runnable... runnables) {
        int len;
        if (options == null || runnables == null || (len = runnables.length) == 0) {
            throw new IllegalArgumentException("options or runnables is null (or empty)!");
        }

        // 循环遍历测量各个 Runnable 实例的性能结果，然后将各个结果存放到数组中，最后统一输出出来.
        Measurement[] measurements = new Measurement[len];
        for (int i = 0; i < len; i++) {
            measurements[i] = new MeasureRunnerContext(options).runAndCollect(runnables[i]);
        }
        return measurements;
    }

}
