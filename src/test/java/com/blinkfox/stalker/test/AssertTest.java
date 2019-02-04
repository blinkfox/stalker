package com.blinkfox.stalker.test;

import com.blinkfox.stalker.Assert;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.test.prepare.MyTestService;
import org.junit.Test;

/**
 * AssertTest.
 *
 * @author blinkfox on 2019-02-04.
 */
public class AssertTest {

    @Test(expected = AssertionError.class)
    public void fail() {
        Assert.fail();
    }

    @Test
    public void assertFaster() {
        Assert.assertFaster(Options.of(),
                () -> new MyTestService().fastHello(),
                () -> new MyTestService().hello());
    }

    @Test(expected = AssertionError.class)
    public void assertFasterWithFail() {
        Assert.assertFaster(Options.of(),
                () -> new MyTestService().hello(),
                () -> new MyTestService().fastHello());
    }

}
