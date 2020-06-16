package com.blinkfox.stalker.kit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import org.slf4j.helpers.MessageFormatter;

/**
 * 字符串操作工具类.
 *
 * @author blinkfox on 2019-01-10.
 * @since v1.0.0
 */
@UtilityClass
public class StrKit {

    /**
     * 判断给定字符串是否是空字符串.
     *
     * @param s 字符串
     * @return 布尔值
     */
    public boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 作为字符串连接各个对象.
     *
     * @param objects 不定参数对象
     * @return 字符串
     * @author blinkfox on 2020-05-23.
     * @since v1.2.0
     */
    public String join(Object... objects) {
        if (objects != null && objects.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object o : objects) {
                sb.append(o == null ? "" : o.toString());
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 使用 Slf4j 中的字符串格式化方式来格式化字符串.
     *
     * @param pattern 待格式化的字符串
     * @param args 参数
     * @return 格式化后的字符串
     */
    public String format(String pattern, Object... args) {
        return pattern == null ? "" : MessageFormatter.arrayFormat(pattern, args).getMessage();
    }

    /**
     * 将纳秒的时间转为与其最贴近的时间单位.
     *
     * @param n 纳秒
     * @return 其他单位的时间字符串
     */
    public String convertTime(Number n) {
        double d = n.doubleValue();
        if (d < 1e5) {
            return Long.toString(n.longValue()).concat(" ns");
        } else if (d >= 1e4 && d < 1e9) {
            return roundToString(d / 1e6, "ms");
        } else if (d >= 1e9 && d < 6e10) {
            return roundToString(d / 1e9, "s");
        } else if (d >= 6e10 && d < 36e11) {
            return roundToString(d / 6e10, "min");
        } else if (d >= 36e11 && d < 864e11) {
            return roundToString(d / 36e11, "h");
        } else {
            return roundToString(d / 864e11, "d");
        }
    }

    /**
     * 将纳秒的时间转为与其最贴近的时间单位.
     *
     * @param amount 数量
     * @param timeUnit 时间单位
     * @return 其他单位的时间字符串
     */
    public String convertTimeUnit(long amount, TimeUnit timeUnit) {
        switch (timeUnit) {
            case NANOSECONDS:
                return amount + " ns";
            case MICROSECONDS:
                return amount + " μs";
            case MILLISECONDS:
                return amount + " ms";
            case SECONDS:
                return amount + " s";
            case MINUTES:
                return amount + " m";
            case HOURS:
                return amount + " h";
            case DAYS:
                return amount + " d";
            default:
                return String.valueOf(amount);
        }
    }

    /**
     * 根据double的值和单位，拼接其对应的四舍五入的字符串值.
     *
     * @param d double值
     * @param unit 时间单位字符串
     * @return 四舍五入后的时间字符串
     */
    private String roundToString(double d, String unit) {
        return join(BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_UP).toString(), " ", unit);
    }

    /**
     * 对 double 类型的值进行四舍五入，并返回四舍五入的字符串值.
     *
     * @param d double值
     * @return 四舍五入后的字符串
     */
    public String roundToString(double d) {
        return BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_UP).toString();
    }

}