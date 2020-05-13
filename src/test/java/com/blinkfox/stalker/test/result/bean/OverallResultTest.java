package com.blinkfox.stalker.test.result.bean;

import com.blinkfox.stalker.result.bean.OverallResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * OverallResultTest.
 *
 * @author blinkfox on 2019-02-04.
 * @since v1.0.0
 */
public class OverallResultTest {

    @Test
    public void testToString() {
        Assert.assertNotNull(new OverallResult()
                .setEachMeasures(new long[] {20, 10})
                .setCosts(40)
                .setTotal(5)
                .setSuccess(5)
                .setFailure(0).toString());
    }

}
