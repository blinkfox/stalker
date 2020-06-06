package com.blinkfox.stalker.result;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.config.ScheduledUpdater;
import com.blinkfox.stalker.kit.StrKit;
import com.blinkfox.stalker.output.MeasureOutputContext;
import com.blinkfox.stalker.runner.MeasureRunner;
import com.blinkfox.stalker.runner.executor.StalkerExecutors;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Stalker 中异步任务的 Future 结果对象.
 *
 * @author blinkfox on 2020-06-02.
 * @since v1.2.0
 */
@Slf4j
public class StalkerFuture implements RunnableFuture<List<Object>> {

    /**
     * 用于异步提交任务的线程池.
     */
    private static final ExecutorService executor =
            StalkerExecutors.newThreadExecutor(4, 16, "stalker-future-thread");

    /**
     * 用于异步定时更新统计数据的线程池.
     */
    private ScheduledExecutorService scheduledUpdateExecutor;

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
    @Getter
    private final MeasureRunner measureRunner;

    /**
     * 用于 StalkerFuture 内部识别和控制任务运行状态的 {@link CompletableFuture} 对象.
     */
    private CompletableFuture<Void> runFuture;

    /**
     * 定时更新同步数据的 {@link ScheduledFuture} 对象.
     */
    private ScheduledFuture<?> scheduledUpdateFuture;

    /**
     * 构造方法.
     *
     * @param options 运行任务的选项参数
     * @param runnable 可运行实例
     * @param measureRunner 运行测量器
     */
    public StalkerFuture(Options options, Runnable runnable, MeasureRunner measureRunner) {
        this.options = options;
        this.runnable = runnable;
        this.measureRunner = measureRunner;

        // 如果启用了定时更新统计数据的任务，就构造定时任务线程池，并开启异步定时获取统计数据的任务.
        ScheduledUpdater scheduledUpdater = options.getScheduledUpdater();
        if (scheduledUpdater != null && scheduledUpdater.isEnabled()) {
            this.scheduledUpdateExecutor = StalkerExecutors.newScheduledThreadPool(1, "scheduled-update-thread");

            final long delay = scheduledUpdater.getDelay();
            final TimeUnit timeUnit = scheduledUpdater.getTimeUnit();
            this.scheduledUpdateFuture = this.scheduledUpdateExecutor.scheduleWithFixedDelay(() -> {
                if (log.isDebugEnabled()) {
                    log.debug("【Stalker 提示】开始了每隔【{}】执行一次定时更新统计数据的定时任务.", StrKit.convertTimeUnit(delay, timeUnit));
                }
                this.measureRunner.getStatisResult();
            },
            scheduledUpdater.getInitialDelay(), delay, timeUnit);
        }
    }

    /**
     * 执行此可运行的方法.
     * <p>注意，此次使用双重检查锁机制，使得该对象的任务只会被运行一次.</p>
     */
    @Override
    public void run() {
        if (this.runFuture != null) {
            return;
        }

        synchronized (this) {
            if (this.runFuture == null) {
                // 开始异步运行测量任务.
                this.runFuture = CompletableFuture.runAsync(
                        () -> this.measureRunner.run(this.options, this.runnable), executor);

                // 当任务完成之后，如果有其他异步任务没完成或关闭，就关闭相关的异步任务.
                this.runFuture.whenComplete((a, e) -> stopFutures());
            }
        }
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
        // 使用布尔值记录，核心任务是否运行完成.
        boolean flag = true;
        try {
            this.measureRunner.stop();
        } catch (Exception e) {
            log.error("【Stalker 错误提示】取消正在执行中的测量任务时发生异常！", e);
            flag = false;
        }

        // 需要将本 Future 中的相关任务或线程也停止.
        this.stopFutures();
        return flag;
    }

    /**
     * 如果某些任务还没完成或者没关闭，就停止相关的任务信息.
     */
    private void stopFutures() {
        // 立即停止当前异步测量线程任务.
        if (this.runFuture != null && !this.runFuture.isDone()) {
            this.runFuture.cancel(true);
        }

        // 如果线程池未关闭，就关闭线程池，注意，这里不要理解关闭和立即终止正在运行中的任务，防止最后的统计数据更新异常.
        if (this.scheduledUpdateExecutor != null && !this.scheduledUpdateExecutor.isShutdown()) {
            this.scheduledUpdateExecutor.shutdown();
        }
        if (this.scheduledUpdateFuture != null && !this.scheduledUpdateFuture.isDone()) {
            this.scheduledUpdateFuture.cancel(false);
        }
        log.debug("【Stalker 提示】已关闭停止了相关的线程池或异步任务.");
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
     * 实时获取任务的执行结果.
     * 请注意，该方法获取结果时是“非阻塞的”，每次都能获取到正在执行中的任务进度结果，即时任务被取消也能获取到取消时的最终结果信息。
     * 所以，你不应该调用此方法来阻塞等待执行结果.
     *
     * @return {@link Options#getOutputs()}  中定义多种的输出通道结果
     */
    @Override
    public List<Object> get() {
        return new MeasureOutputContext().output(this.options, this.getMeasureResult());
    }

    /**
     * 实时获取任务的执行结果.
     * 请注意，该方法同 {@link #get()} 语义相同，获取结果时是“非阻塞的”，每次都能获取到正在执行中的任务进度结果，
     * 即时任务被取消也能获取到取消时的最终结果信息。
     *
     * <p>所以，你不应该调用此方法来阻塞等待执行结果.</p>
     *
     * @param timeout 超时时间
     * @param unit 超时时间单位
     * @return {@link Options#getOutputs()}  中定义多种的输出通道结果
     */
    @Override
    public List<Object> get(long timeout, TimeUnit unit) {
        return this.get();
    }

    /**
     * 实时获取任务的测量头统计结果.
     *
     * @return {@link StatisResult} 结果
     */
    public StatisResult getMeasureResult() {
        return this.measureRunner.getStatisResult();
    }

    /**
     * 获取任务最终完成时实际所消耗的总的纳秒时间数.
     *
     * @return 实际所消耗的总的纳秒时间数
     */
    public long getCosts() {
        return this.measureRunner.getCosts();
    }

    /**
     * 获取当前已经运行的总次数.
     *
     * @return 运行总次数
     */
    public long getTotal() {
        return this.measureRunner.getTotal();
    }

    /**
     * 获取到当前时的运行成功的次数.
     *
     * @return 运行成功的次数
     */
    public long getSuccess() {
        return this.measureRunner.getSuccess();
    }

    /**
     * 获取当前运行失败的次数.
     *
     * @return 运行失败的次数
     */
    public long getFailure() {
        return this.measureRunner.getFailure();
    }

    /**
     * 获取任务开始运行时的纳秒时间戳.
     *
     * @return 开始运行时的纳秒时间戳
     */
    public long getStartNanoTime() {
        return this.measureRunner.getStartNanoTime();
    }

    /**
     * 获取任务结束运行时的纳秒时间戳，如果任务还未结束，该值将是 {@code 0}.
     *
     * @return 结束时纳秒时间戳MeasureStatistician
     */
    public long getEndNanoTime() {
        return this.measureRunner.getEndNanoTime();
    }

}
