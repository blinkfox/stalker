package com.blinkfox.stalker.kit;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 字符串操作工具类.
 *
 * @author blinkfox on 2019-1-10.
 */
public final class StrKit {

    /** 空字符串. */
    private static final String EMPTY = "";

    /**
     * 私有构造方法.
     */
    private StrKit() {}

    /**
     * 判断给定字符串是否是空字符串.
     *
     * @param s 字符串
     * @return 布尔值
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 作为字符串连接各个对象.
     *
     * @param objects 不定参数对象
     * @return 字符串
     */
    public static String join(Object... objects) {
        if (objects != null && objects.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object o : objects) {
                sb.append(o == null ? EMPTY : o.toString());
            }
            return sb.toString();
        }

        return "";
    }

    /**
     * 将纳秒的时间转为与其最贴近的时间单位.
     *
     * @param n 纳秒
     * @return 其他单位的时间字符串
     */
    public static String convertTime(Number n) {
        double d = n.doubleValue();
        if (d < 1e5) {
            return Long.toString(n.longValue()).concat(" ns");
        } else if (d >= 1e4 && d < 1e9) {
            return getRoundString(d / 1e6, "ms");
        } else if (d >= 1e9 && d < 6e10) {
            return getRoundString(d / 1e9, "s");
        } else if (d >= 6e10 && d < 36e11) {
            return getRoundString(d / 6e10, "min");
        } else if (d >= 36e11 && d < 864e11) {
            return getRoundString(d / 36e11, "h");
        } else {
            return getRoundString(d / 864e11, "d");
        }
    }

    /**
     * 根据double的值和单位，拼接其对应的四舍五入的字符串值.
     *
     * @param d double值
     * @param unit 时间单位字符串
     * @return 四舍五入后的时间字符串
     */
    private static String getRoundString(double d, String unit) {
        return join(BigDecimal.valueOf(d)
                .setScale(2, RoundingMode.HALF_UP).doubleValue(), " ", unit);
    }

}