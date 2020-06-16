package com.blinkfox.stalker.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link RunDuration} 的单元测试类.
 *
 * @author blinkfox on 2020-06-16.
 * @since v1.2.2
 */
public class RunDurationTest {

    @Test
    public void parseToSeconds() {
        Assert.assertEquals(136, RunDuration.parseToSeconds("136s"));
        Assert.assertEquals(1513, RunDuration.parseToSeconds("25m13s"));
        Assert.assertEquals(267913, RunDuration.parseToSeconds("3d2h25m13s"));
    }

}
