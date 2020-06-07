package com.blinkfox.stalker.test.result.bean;

import com.blinkfox.stalker.result.MeasureResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link MeasureResult} 的单元测试类.
 *
 * @author blinkfox on 2019-02-04.
 * @since v1.0.0
 */
public class MeasureResultTest {

    @Test
    public void testToString() {
        MeasureResult measureResult = new MeasureResult();
        measureResult.setMax(30);
        Assert.assertNotNull(measureResult.toString());
    }

}
