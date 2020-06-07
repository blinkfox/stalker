package com.blinkfox.stalker.config;

import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ScheduledUpdater} 的单元测试类.
 *
 * @author blinkfox on 2020-06-06.
 * @since v1.2.0
 */
public class ScheduledUpdaterTest {

    @Test
    public void of() {
        ScheduledUpdater scheduledUpdater = ScheduledUpdater.of(true, 2, 3, TimeUnit.SECONDS);
        Assert.assertTrue(scheduledUpdater.isEnabled());
        Assert.assertEquals(2, scheduledUpdater.getInitialDelay());
        Assert.assertEquals(3, scheduledUpdater.getDelay());
        Assert.assertEquals(TimeUnit.SECONDS, scheduledUpdater.getTimeUnit());
    }

    @Test
    public void ofEnable() {
        ScheduledUpdater scheduledUpdater = ScheduledUpdater.ofEnable();
        Assert.assertTrue(scheduledUpdater.isEnabled());
        Assert.assertEquals(5, scheduledUpdater.getInitialDelay());
        Assert.assertEquals(5, scheduledUpdater.getDelay());
        Assert.assertEquals(TimeUnit.SECONDS, scheduledUpdater.getTimeUnit());
    }

    @Test
    public void ofDisable() {
        ScheduledUpdater scheduledUpdater = ScheduledUpdater.ofDisable();
        Assert.assertFalse(scheduledUpdater.isEnabled());
        Assert.assertEquals(5, scheduledUpdater.getInitialDelay());
        Assert.assertEquals(5, scheduledUpdater.getDelay());
        Assert.assertEquals(TimeUnit.SECONDS, scheduledUpdater.getTimeUnit());
    }

    @Test
    public void of2() {
        ScheduledUpdater scheduledUpdater = ScheduledUpdater.of(3, TimeUnit.MINUTES);
        Assert.assertTrue(scheduledUpdater.isEnabled());
        Assert.assertEquals(3, scheduledUpdater.getInitialDelay());
        Assert.assertEquals(3, scheduledUpdater.getDelay());
        Assert.assertEquals(TimeUnit.MINUTES, scheduledUpdater.getTimeUnit());
    }

    @Test
    public void of3() {
        ScheduledUpdater scheduledUpdater = ScheduledUpdater.ofSeconds(7);
        Assert.assertTrue(scheduledUpdater.isEnabled());
        Assert.assertEquals(7, scheduledUpdater.getInitialDelay());
        Assert.assertEquals(7, scheduledUpdater.getDelay());
        Assert.assertEquals(TimeUnit.SECONDS, scheduledUpdater.getTimeUnit());
    }

    @Test
    public void of4() {
        ScheduledUpdater scheduledUpdater = ScheduledUpdater.ofMinutes(2);
        Assert.assertTrue(scheduledUpdater.isEnabled());
        Assert.assertEquals(2, scheduledUpdater.getInitialDelay());
        Assert.assertEquals(2, scheduledUpdater.getDelay());
        Assert.assertEquals(TimeUnit.MINUTES, scheduledUpdater.getTimeUnit());

        scheduledUpdater.disable();
        Assert.assertFalse(scheduledUpdater.isEnabled());

        scheduledUpdater.enable();
        Assert.assertTrue(scheduledUpdater.isEnabled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkDelay() {
        ScheduledUpdater.ofMinutes(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkParams() {
        ScheduledUpdater.of(1, TimeUnit.MILLISECONDS);
    }

}