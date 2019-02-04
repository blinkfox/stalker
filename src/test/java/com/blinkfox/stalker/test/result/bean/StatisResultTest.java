package com.blinkfox.stalker.test.result.bean;

import com.blinkfox.stalker.result.bean.StatisResult;

import org.junit.Assert;
import org.junit.Test;

/**
 * StatisResultTest.
 *
 * @author blinkfox on 2019-02-04.
 */
public class StatisResultTest {

    @Test
    public void testToString() {
        StatisResult statisResult = new StatisResult();
        statisResult.setMax(30);
        Assert.assertNotNull(statisResult.toString());
    }

}
