package com.blinkfox.stalker.test.config;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.config.ScheduledUpdater;
import com.blinkfox.stalker.config.StalkerConfigManager;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

/**
 * StalkerConfigManagerTest.
 *
 * @author blinkfox on 2019-02-04.
 * @since v1.0.0
 */
public class StalkerConfigManagerTest {

    @Test
    public void testConfigOptions() {
        StalkerConfigManager stalkerConfigManager = StalkerConfigManager.getInstance();
        Assert.assertNotNull(stalkerConfigManager);

        Options options = stalkerConfigManager.getDefaultOptions();
        Assert.assertEquals(1, options.getRuns());

        options.named("test");
        stalkerConfigManager.reLoadOptions(options);
        Assert.assertEquals("test", options.getName());
    }

    /**
     * 测试加载获取 {@link ScheduledUpdater} 的配置参数信息.
     *
     * @author blinkfox on 2020-06-06.
     * @since v1.2.0
     */
    @Test
    public void testConfigOptions2() {
        StalkerConfigManager stalkerConfigManager = StalkerConfigManager.getInstance();

        Options options = stalkerConfigManager.getDefaultOptions();
        options.runs(1);
        stalkerConfigManager.reLoadOptions(options, ScheduledUpdater.ofMinutes(2));
        Assert.assertEquals(1, options.getRuns());

        ScheduledUpdater defaultScheduledUpdater = stalkerConfigManager.getDefaultScheduledUpdater();
        Assert.assertEquals(2, defaultScheduledUpdater.getDelay());
        Assert.assertEquals(TimeUnit.MINUTES, defaultScheduledUpdater.getTimeUnit());
    }

}