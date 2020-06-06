package com.blinkfox.stalker.test.result.bean;

import com.blinkfox.stalker.result.StatisResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * StatisResultTest.
 *
 * @author blinkfox on 2019-02-04.
 * @since v1.0.0
 */
public class StatisResultTest {

    @Test
    public void testToString() {
        StatisResult statisResult = new StatisResult();
        statisResult.setMax(30);
        Assert.assertNotNull(statisResult.toString());
    }

}
