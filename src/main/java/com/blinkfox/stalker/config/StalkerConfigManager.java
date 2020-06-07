package com.blinkfox.stalker.config;

import com.blinkfox.stalker.output.OutputConsole;
import lombok.Getter;

/**
 * Stalker 单例的全局配置管理类.
 *
 * @author blinkfox on 2019-01-30.
 * @since v1.0.0
 */
public final class StalkerConfigManager {

    /**
     * 全局唯一的 StalkerConfigManager 实例.
     */
    private static final StalkerConfigManager stalkerConfigManager = new StalkerConfigManager();

    /**
     * 全局默认的选项参数.
     */
    @Getter
    private Options defaultOptions;

    /**
     * 全局默认的定时更新统计数据的相关参数.
     */
    @Getter
    private ScheduledUpdater defaultScheduledUpdater;

    /**
     * 私有构造方法，构造默认的选项参数数据.
     */
    private StalkerConfigManager() {
        this.defaultOptions = new Options()
                .threads(1)
                .concurrens(1)
                .warmups(5)
                .runs(10)
                .printErrorLog(false)
                .outputs(new OutputConsole());

        this.defaultScheduledUpdater = ScheduledUpdater.ofSeconds(10).disable();
    }

    /**
     * 获取到唯一的 StalkerConfigManager 实例.
     *
     * @return StalkerConfigManager实例
     */
    public static StalkerConfigManager getInstance() {
        return stalkerConfigManager;
    }

    /**
     * 重新加载指定的选项参数 options 对象作为默认的 options 对象.
     * <p>重载传入的options前，对options的参数合法性做校验.</p>
     *
     * @param options 选项参数
     */
    public synchronized void reLoadOptions(Options options) {
        options.valid();
        this.defaultOptions = options;
    }

    /**
     * 重新加载指定的选项参数 options 对象作为默认的 options 对象.
     * <p>重载传入的options前，对options的参数合法性做校验.</p>
     *
     * @param options 选项参数
     * @param scheduledUpdater 用于定时更新测量的统计数据更新器配置参数
     */
    public synchronized void reLoadOptions(Options options, ScheduledUpdater scheduledUpdater) {
        options.valid();
        scheduledUpdater.valid();
        this.defaultOptions = options;
        this.defaultScheduledUpdater = scheduledUpdater;
    }

}
