package com.blinkfox.stalker.test.result.statis;

import com.blinkfox.stalker.result.bean.OverallResult;
import com.blinkfox.stalker.result.statis.DefaultMeasureStatis;
import org.junit.Assert;
import org.junit.Test;

/**
 * DefaultMeasureStatisTest.
 *
 * @author blinkfox on 2019-02-04.
 */
public class DefaultMeasureStatisTest {

    @Test
    public void statis() {
        OverallResult overallResult = new OverallResult();
        Assert.assertEquals(0L, new DefaultMeasureStatis().statis(overallResult).getSum());

        overallResult.setEachMeasures(new long[]{});
        Assert.assertEquals(0L, new DefaultMeasureStatis().statis(overallResult).getSum());
    }

}
