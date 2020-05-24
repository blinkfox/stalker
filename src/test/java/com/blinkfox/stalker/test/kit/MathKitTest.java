package com.blinkfox.stalker.test.kit;

import com.blinkfox.stalker.kit.MathKit;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link MathKit} 的单元测试类.
 *
 * @author blinkfox on 2020-05-15.
 * @since v1.1.1
 */
public class MathKitTest {

    @Test
    public void calcThroughput() {
        Assert.assertEquals(200, MathKit.calcThroughput(20, 100_000_000), 1e-6);
        Assert.assertEquals(2000, MathKit.calcThroughput(150, 75_000_000), 1e-6);
    }

}