package com.blinkfox.stalker.test.config;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.output.OutputConsole;

import org.junit.Assert;
import org.junit.Test;

/**
 * OptionsTest.
 *
 * @author blinkfox on 2019-02-03.
 */
public class OptionsTest {

    /** 用来表示此次测量名称的常量. */
    private static final String NAME = "test";

    /**
     * 测试构造实例的of方法.
     */
    @Test
    public void of() {
        Assert.assertTrue(Options.of().getRuns() > 0);
        Assert.assertEquals(NAME, Options.of().named(NAME).getName());
        Assert.assertEquals(NAME, Options.of(NAME).getName());
        Assert.assertEquals(5, Options.of(5).getRuns());
        Assert.assertEquals(1, Options.of().outputs(new OutputConsole()).getOutputs().size());

        Options options1 = Options.of(NAME, 5);
        Assert.assertEquals(NAME, options1.getName());
        Assert.assertEquals(5, options1.getRuns());

        Options options2 = Options.of(NAME, 10, 3, 1);
        Assert.assertEquals(NAME, options2.getName());
        Assert.assertEquals(10, options2.getThreads());
        Assert.assertEquals(3, options2.getConcurrens());
        Assert.assertEquals(1, options2.getRuns());
    }

    /**
     * 测试线程数不对时的 valid 方法.
     */
    @Test(expected = IllegalArgumentException.class)
    public void validWithThreads() {
        Options.of().threads(0).valid();
    }

    /**
     * 测试并发数不对时的 valid 方法.
     */
    @Test(expected = IllegalArgumentException.class)
    public void validWithConcurrens() {
        Options.of().concurrens(0).valid();
    }

    /**
     * 测试预热次数数不对时的 valid 方法.
     */
    @Test(expected = IllegalArgumentException.class)
    public void validWithWarmups() {
        Options.of().warmups(-1).valid();
    }

    /**
     * 测试运行次数不对时的 valid 方法.
     */
    @Test(expected = IllegalArgumentException.class)
    public void validWithRuns() {
        Options.of().runs(0).valid();
    }

}
