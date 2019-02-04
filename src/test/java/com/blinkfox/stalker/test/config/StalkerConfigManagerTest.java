package com.blinkfox.stalker.test.config;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.config.StalkerConfigManager;

import org.junit.Assert;
import org.junit.Test;

/**
 * StalkerConfigManagerTest.
 *
 * @author blinkfox on 2019-02-04.
 */
public class StalkerConfigManagerTest {

    @Test
    public void testConfigOptions() {
        StalkerConfigManager stalkerConfigManager = StalkerConfigManager.getInstance();
        Assert.assertNotNull(stalkerConfigManager);

        Options options = stalkerConfigManager.getDefaultOptions();
        Assert.assertEquals(10, options.getRuns());

        options.named("test");
        stalkerConfigManager.reLoadOptions(options);
        Assert.assertEquals("test", options.getName());
    }

}