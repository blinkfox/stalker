package com.blinkfox.stalker.test.kit;

import com.blinkfox.stalker.kit.StrKit;
import org.junit.Assert;
import org.junit.Test;

/**
 * StrKitTest.
 *
 * @author blinkfox on 2019-02-04.
 * @since v1.0.0
 */
public class StrKitTest {

    /**
     * 测试 isEmpty 方法的边界情况.
     */
    @Test
    public void isEmpty() {
        Assert.assertTrue(StrKit.isEmpty(null));
        Assert.assertTrue(StrKit.isEmpty(""));
        Assert.assertFalse(StrKit.isEmpty(" "));
    }

    /**
     * 测试 join 方法的边界情况.
     */
    @Test
    public void join() {
        Assert.assertEquals("", StrKit.join(null));
        Assert.assertEquals("", StrKit.join(""));
        Assert.assertEquals("", StrKit.join(null, ""));
        Assert.assertEquals("ab", StrKit.join("a", null, "b"));
    }

    /**
     * 测试 convertTime 方法.
     */
    @Test
    public void convertTime() {
        Assert.assertEquals("5623 ns", StrKit.convertTime(5_623));
        Assert.assertEquals("0.22 ms", StrKit.convertTime(215_623));
        Assert.assertEquals("32.53 ms", StrKit.convertTime(32_525_623));
        Assert.assertEquals("1.53 s", StrKit.convertTime(1_526_165_603));
        Assert.assertEquals("1.67 min", StrKit.convertTime(100_000_000_000L));
        Assert.assertEquals("5.0 h", StrKit.convertTime(18_000_000_000_000L));
        Assert.assertEquals("5.0 h", StrKit.convertTime(18_000_000_000_000L));
        Assert.assertEquals("3.0 d", StrKit.convertTime(259_200_000_000_000L));
    }

}