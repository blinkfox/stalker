package com.blinkfox.stalker.config;

import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * 用于定时更新统计数据的配置类.
 *
 * @author blinkfox on 2020-06-06.
 * @since v1.2.0
 */
@Getter
public class ScheduledUpdater {

    /**
     * 是否启用定时更新统计数据的任务，默认为 {@code false}.
     */
    private boolean enabled;

    /**
     * 初始化延迟执行的时间间隔.
     */
    private final long initialDelay;

    /**
     * 每次执行的时间间隔.
     */
    private final long delay;

    /**
     * 时间间隔的单位.
     */
    private final TimeUnit timeUnit;

    /**
     * 私有默认构造方法.
     *
     * @param enabled 是否启用
     * @param initialDelay 初始延迟执行时间
     * @param delay 定时执行的时间间隔
     * @param timeUnit 时间单位
     */
    private ScheduledUpdater(boolean enabled, long initialDelay, long delay, TimeUnit timeUnit) {
        this.enabled = enabled;
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    /**
     * 通过 4 个参数构造 {@link ScheduledUpdater} 实例的构造方法.
     *
     * @param enabled 是否启用
     * @param initialDelay 初始延迟执行时间
     * @param delay 定时执行的时间间隔
     * @param timeUnit 时间单位
     */
    public static ScheduledUpdater of(boolean enabled, long initialDelay, long delay, TimeUnit timeUnit) {
        checkDelay(initialDelay);
        checkParams(delay, timeUnit);
        return new ScheduledUpdater(enabled, initialDelay, delay, timeUnit);
    }

    /**
     * 构造启用和其他默认参数 {@link ScheduledUpdater} 实例的构造方法.
     */
    public static ScheduledUpdater ofEnable() {
        return new ScheduledUpdater(true, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * 构造禁用和其他默认参数 {@link ScheduledUpdater} 实例的构造方法.
     */
    public static ScheduledUpdater ofDisable() {
        return new ScheduledUpdater(false, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * 构造 {@link ScheduledUpdater} 实例的构造方法.
     *
     * @param delay 时间间隔数据，延迟时间的值也跟这个值默认保持一致
     * @param timeUnit 时间单位
     * @return {@link ScheduledUpdater} 实例
     */
    public static ScheduledUpdater of(long delay, TimeUnit timeUnit) {
        checkParams(delay, timeUnit);
        return new ScheduledUpdater(true, delay, delay, timeUnit);
    }

    /**
     * 根据时间间隔参数构和默认"秒"作为参数来构造 {@link ScheduledUpdater} 实例的构造方法.
     *
     * @param delay 时间间隔数据，延迟时间的值也跟这个值默认保持一致
     * @return {@link ScheduledUpdater} 实例
     */
    public static ScheduledUpdater ofSeconds(long delay) {
        checkDelay(delay);
        return new ScheduledUpdater(true, delay, delay, TimeUnit.SECONDS);
    }

    /**
     * 根据时间间隔参数构和默认"分钟"作为参数来构造 {@link ScheduledUpdater} 实例的构造方法.
     *
     * @param delay 时间间隔数据，延迟时间的值也跟这个值默认保持一致
     * @return {@link ScheduledUpdater} 实例
     */
    public static ScheduledUpdater ofMinutes(long delay) {
        checkDelay(delay);
        return new ScheduledUpdater(true, delay, delay, TimeUnit.MINUTES);
    }

    /**
     * 启用定时任务.
     *
     * @return 当前 {@link ScheduledUpdater} 实例对象
     */
    public ScheduledUpdater enable() {
        this.enabled = true;
        return this;
    }

    /**
     * 禁用定时任务.
     *
     * 当前 {@link ScheduledUpdater} 实例对象
     */
    public ScheduledUpdater disable() {
        this.enabled = false;
        return this;
    }

    public void valid() {
        checkDelay(initialDelay);
        checkParams(delay, timeUnit);
    }

    /**
     * 检查延迟时间或时间间隔参数是否合法.
     *
     * @param delay 时间间隔
     */
    private static void checkDelay(long delay) {
        if (delay <= 0) {
            throw new IllegalArgumentException("【Stalker 无效参数异常】延迟更新统计数据的时间必须大于 0，获取到的值是：【" + delay + "】.");
        }
    }

    /**
     * 检查时间间隔或时间单位参数是否合法.
     *
     * @param delay 时间间隔
     * @param timeUnit 时间单位
     */
    private static void checkParams(long delay, TimeUnit timeUnit) {
        checkDelay(delay);
        if (timeUnit == null) {
            throw new IllegalArgumentException("【Stalker 无效参数异常】运行的时间间隔的单位不能为空【null】.");
        }

        if (timeUnit == TimeUnit.NANOSECONDS
                || timeUnit == TimeUnit.MICROSECONDS
                || timeUnit == TimeUnit.MILLISECONDS) {
            throw new IllegalArgumentException("【Stalker 无效参数异常】运行的时间间隔的单位至少是【秒】，"
                    + "不能是【纳秒】、【微秒】或者【毫秒】，获取到的值是：【" + timeUnit.name() + "】.");
        }
    }

}
