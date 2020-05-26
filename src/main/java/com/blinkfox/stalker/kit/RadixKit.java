package com.blinkfox.stalker.kit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 进制处理工具类.
 *
 * @author blinkfox on 2020-05-27.
 * @since v1.2.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RadixKit {

    /**
     * 62 进制数需要的 char 数组字符表，
     * 为了保证生成的各个进制数的字符串保证 ASCII 的大小顺序，Radix 类生成的进制顺序是先数字，大写字母，再小写字母的顺序.
     */
    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * 支持的最大进制数.
     */
    static final int RADIX_62 = DIGITS.length;

    /**
     * 默认的 10 进制常量.
     */
    private static final int RADIX_10 = 10;

    /**
     * 将长整型数值转换为指定的进制数（最大支持 62 进制，字母数字已经用尽）.
     *
     * @param i 待转换数字
     * @param radix 进制数
     * @return 转换后的字符串
     */
    public static String toString(long i, int radix) {
        if (radix < Character.MIN_RADIX || radix > RADIX_62) {
            radix = RADIX_10;
        }

        if (radix == RADIX_10) {
            return Long.toString(i);
        }

        boolean negative = (i < 0);
        if (!negative) {
            i = -i;
        }

        final int size = 65;
        int charPos = 64;
        char[] buf = new char[size];

        while (i <= -radix) {
            buf[charPos--] = DIGITS[(int) (-(i % radix))];
            i = i / radix;
        }
        buf[charPos] = DIGITS[(int) (-i)];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (size - charPos));
    }

    /**
     * 根据对应的整数和移位数求得对应的字符串值.
     *
     * @param val 整数
     * @param digits 移位数
     * @return 字符串值
     */
    public static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return toString(hi | (val & (hi - 1)), RADIX_62).substring(1);
    }

}
