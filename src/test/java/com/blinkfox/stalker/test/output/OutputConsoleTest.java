package com.blinkfox.stalker.test.output;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.output.OutputConsole;
import com.blinkfox.stalker.result.bean.Measurement;
import com.blinkfox.stalker.result.bean.OverallResult;
import org.junit.Test;

/**
 * OutputConsoleTest.
 *
 * @author blinkfox on 2019-02-04.
 * @since v1.0.0
 */
public class OutputConsoleTest {

    /**
     * 测试 Options 为 null 的情况.
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputWithNullOptions() {
        new OutputConsole().output(null, new Measurement(new OverallResult()));
    }

    /**
     * 测试 measurements 为 null 的情况.
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputWithNullMeasurements() {
        new OutputConsole().output(Options.of(), null);
    }

}
