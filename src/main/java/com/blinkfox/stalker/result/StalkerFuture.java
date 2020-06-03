package com.blinkfox.stalker.result;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.Measurement;
import com.blinkfox.stalker.runner.MeasureRunner;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Stalker 中异步任务的 Future 结果对象.
 *
 * @author blinkfox on 2020-06-02.
 * @since v1.2.0
 */
@Slf4j
@AllArgsConstructor
public class StalkerFuture implements RunnableFuture<Measurement> {

    /**
     * 可运行任务的选项参数信息.
     */
    private final Options options;

    /**
     * 可运行的任务.
     */
    private final Runnable runnable;

    /**
     * 任务运行的 {@link MeasureRunner} 实例.
     */
    private final MeasureRunner measureRunner;

    /**
     * 执行此可运行的方法.
     */
    @Override
    public void run() {
        this.measureRunner.run(this.options, this.runnable);
    }

    /**
     * 立即取消正在执行的测量任务，并立即关闭运行中的任务线程池.
     *
     * @return 正常情况下返回 {@code true}，如果期间发生异常将返回 {@code false}
     */
    public boolean cancel() {
        return this.cancel(true);
    }

    /**
     * 立即取消正在执行的测量任务，并关闭运行中的任务线程池.
     *
     * @param mayInterruptIfRunning 该参数将始终是 {@code true}，即取消任务时不管是否执行完了相关的任务都会立即取消任务.
     * @return 正常情况下返回 {@code true}，如果期间发生异常将返回 {@code false}
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            this.measureRunner.stop();
            return true;
        } catch (Exception e) {
            log.error("【Stalker 错误提示】取消正在执行中的测量任务时发生异常！", e);
            return false;
        }
    }

    /**
     * 获取测量任务在完成之前是否已经被取消.
     * 如果任务是正常完成的，将返回 {@code false}，如果任务是在完成之前手动取消的，将返回 {@code true}.
     *
     * @return 布尔值
     */
    @Override
    public boolean isCancelled() {
        return this.measureRunner.isCancelled();
    }

    /**
     * 获取测量任务是否已经执行结束.
     * 不管任务是正常结束还是手动取消，只要任务结束了，都将返回 {@code true}，如果任务还在执行中，将返回 {@code false}.
     *
     * @return 布尔值
     */
    @Override
    public boolean isDone() {
        return this.measureRunner.isCompleted();
    }

    /**
     * 获取测量任务是否已经正常执行完毕.
     * 只有当任务正常执行完毕时才返回 {@code true}，如果任务还在执行中或者被手动取消将返回 {@code false}.
     *
     * @return 布尔值
     */
    public boolean isDoneSuccessfully() {
        return this.measureRunner.isCompleted() && !this.measureRunner.isCancelled();
    }

    /**
     * 获取任务的执行结果.
     * 请注意，该方法获取结果时是“非阻塞的”，每次都能获取到正在执行中的任务进度结果，即时任务被取消也能获取到取消时的最终结果信息。
     * 所以，你不应该调用此方法来阻塞等待执行结果.
     *
     * @return Measurement 实体对象
     */
    @Override
    public Measurement get() {
        return new MeasurementCollector().collect(this.measureRunner.buildRunningMeasurement());
    }

    /**
     * 获取任务的执行结果，本方法.
     * 请注意，该方法同 {@link #get()} 语义相同，获取结果时是“非阻塞的”，每次都能获取到正在执行中的任务进度结果，
     * 即时任务被取消也能获取到取消时的最终结果信息。
     *
     * <p>所以，你不应该调用此方法来阻塞等待执行结果.</p>
     *
     * @param timeout 超时时间
     * @param unit 超时时间单位
     * @return {@link Measurement} 结果
     */
    @Override
    public Measurement get(long timeout, TimeUnit unit) {
        return this.get();
    }

}
