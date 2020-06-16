package com.blinkfox.stalker.config;

import com.blinkfox.stalker.kit.StrKit;
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
        return startNanoTime + this.getAmountNanoTime();
    }

    /**
     * 获取总持续时间的纳秒数值.
     *
     * @return 结束纳秒时间
     * @author blinkfox on 2020-06-16.
     * @since v1.2.2
     */
    public long getAmountNanoTime() {
        switch (this.timeUnit) {
            case NANOSECONDS:
                return amount;
            case MICROSECONDS:
                return amount * 1000L;
            case MILLISECONDS:
                return amount * 1000_000L;
            case SECONDS:
                return amount * 1000_000_000L;
            case MINUTES:
                return amount * 60_000_000_000L;
            case HOURS:
                return amount * 3600_000_000_000L;
            case DAYS:
                return amount * 86400_000_000_000L;
            default:
                return 0;
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

    /**
     * 解析传入的持续时间字符串为秒数，格式如：{@code 2d5h23m1s}.
     *
     * <p>注：时间单位只能是 {@code d, h, m, s} 四个，无视大小写，分别表示天、时、分、秒，
     * 如果没有任何单位，则单位默认是秒，如果有其他字母，则会忽略.</p>
     *
     * @param duration 持续时间字符串.
     * @return 持续时间的秒数数值
     * @author blinkfox on 2020-06-16.
     * @since v1.2.2
     */
    public static long parseToSeconds(String duration) {
        long totalSeconds = 0;
        // 如果是数字，就记录下来，如果是单位字符，就累计出所有的秒数，其他字符则直接跳过.
        StringBuilder sb = new StringBuilder();
        for (char c : duration.toCharArray()) {
            if (c >= '0' && c <= '9') {
                sb.append(c);
            } else if (c == 'd' || c == 'D') {
                String day = sb.toString();
                if (day.length() > 0) {
                    totalSeconds += Long.parseLong(day) * 86400;
                    sb.setLength(0);
                }
            } else if (c == 'h' || c == 'H') {
                String hour = sb.toString();
                if (hour.length() > 0) {
                    totalSeconds += Long.parseLong(hour) * 3600;
                    sb.setLength(0);
                }
            } else if (c == 'm' || c == 'M') {
                String minute = sb.toString();
                if (minute.length() > 0) {
                    totalSeconds += Long.parseLong(minute) * 60;
                    sb.setLength(0);
                }
            } else if (c == 's' || c == 'S') {
                String second = sb.toString();
                if (second.length() > 0) {
                    totalSeconds += Long.parseLong(second);
                    sb.setLength(0);
                }
            } else {
                sb.setLength(0);
            }
        }
        return totalSeconds;
    }

    /**
     * 将对象转换为字符串.
     *
     * @return 字符串
     */
    @Override
    public String toString() {
        return StrKit.convertTimeUnit(this.amount, this.timeUnit);
    }

}
