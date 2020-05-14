package com.blinkfox.stalker.test.result.bean;

import com.blinkfox.stalker.result.bean.Measurement;
import com.blinkfox.stalker.result.bean.OverallResult;
import com.blinkfox.stalker.result.bean.StatisResult;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * MeasurementTest.
 *
 * @author blinkfox on 2019-02-04.
 * @since v1.0.0
 */
public class MeasurementTest {

    private static Measurement measurement;

    /**
     * 初始化.
     */
    @BeforeClass
    public static void init() {
        measurement = new Measurement(new OverallResult());
        measurement.setStatisResult(new StatisResult());
    }

    @Test
    public void getOverallResult() {
        Assert.assertNotNull(measurement.getOverallResult());
    }

    @Test
    public void getStatisResult() {
        Assert.assertNotNull(measurement.getStatisResult());
    }

}
