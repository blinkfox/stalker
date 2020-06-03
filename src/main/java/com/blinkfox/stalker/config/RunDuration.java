package com.blinkfox.stalker.config;

import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * 程序运行的持续时间实体类.
 *
 * @author blinkfox on 2020-05-31.
 * @since v1.2.0
 */
public class RunDuration {

    /**
     * 运行的持续时间量.
     */
    @Getter
    private final long amount;

    /**
     * 运行的持续时间单位.
     */
    @Getter
    private final TimeUnit timeUnit;

    /**
     * 构造方法.
     *
     * @param amount 持续时间的量
     * @param timeUnit 持续时间的单位
     */
    private RunDuration(long amount, TimeUnit timeUnit) {
        this.amount = amount;
        this.timeUnit = timeUnit;
    }

    /**
     * 构造运行持续时间的 {@link RunDuration} 实例.
     *
     * @param amount 持续时间的量
     * @param timeUnit 持续时间的单位
     * @return {@link RunDuration} 实例
     */
    public static RunDuration of(long amount, TimeUnit timeUnit) {
        checkParams(amount, timeUnit);
        return new RunDuration(amount, timeUnit);
    }

    /**
     * 构造运行持续指定【秒数】的 {@link RunDuration} 实例.
     *
     * @param amount 持续时间的量
     * @return {@link RunDuration} 实例
     */
    public static RunDuration ofSeconds(long amount) {
        checkAmount(amount);
        return new RunDuration(amount, TimeUnit.SECONDS);
    }

    /**
     * 构造运行持续指定【分钟数】的 {@link RunDuration} 实例.
     *
     * @param amount 持续时间的量
     * @return {@link RunDuration} 实例
     */
    public static RunDuration ofMinutes(long amount) {
        checkAmount(amount);
        return new RunDuration(amount, TimeUnit.MINUTES);
    }

    /**
     * 构造运行持续指定【小时数】的 {@link RunDuration} 实例.
     *
     * @param amount 持续时间的量
     * @return {@link RunDuration} 实例
     */
    public static RunDuration ofHours(long amount) {
        checkAmount(amount);
        return new RunDuration(amount, TimeUnit.HOURS);
    }

    /**
     * 构造运行持续指定【天数】的 {@link RunDuration} 实例.
     *
     * @param amount 持续时间的量
     * @return {@link RunDuration} 实例
     */
    public static RunDuration ofDays(long amount) {
        checkAmount(amount);
        return new RunDuration(amount, TimeUnit.DAYS);
    }

    /**
     * 根据开始纳秒时间和持续时间计算出期望的结束纳秒时间.
     *
     * @param startNanoTime 开始纳秒时间
     * @return 结束纳秒时间
     */
    public long getEndNanoTime(long startNanoTime) {
        switch (this.timeUnit) {
            case NANOSECONDS :
                return startNanoTime + amount;
            case MICROSECONDS :
                return startNanoTime + amount * 1000L;
            case MILLISECONDS :
                return startNanoTime + amount * 1000_000L;
            case SECONDS:
                return startNanoTime + amount * 1000_000_000L;
            case MINUTES:
                return startNanoTime + amount * 60_000_000_000L;
            case HOURS:
                return startNanoTime + amount * 3600_000_000_000L;
            case DAYS:
                return startNanoTime + amount * 86400_000_000_000L;
            default :
                return startNanoTime;
        }
    }

    private static void checkParams(long amount, TimeUnit timeUnit) {
        checkAmount(amount);
        if (timeUnit == null) {
            throw new IllegalArgumentException("【Stalker 无效参数异常】运行的最小持续时间单位不能为空【null】.");
        }
        if (timeUnit == TimeUnit.NANOSECONDS
                || timeUnit == TimeUnit.MICROSECONDS
                || timeUnit == TimeUnit.MILLISECONDS) {
            throw new IllegalArgumentException("【Stalker 无效参数异常】运行的最小持续时间单位至少是【秒】，"
                    + "不能是【纳秒】、【微秒】或者【毫秒】，获取到的值是：【" + timeUnit.name() + "】.");
        }
    }

    private static void checkAmount(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("【Stalker 无效参数异常】运行的续时间必须是正整数，获取到的值是：【" + amount + "】.");
        }
    }

}
