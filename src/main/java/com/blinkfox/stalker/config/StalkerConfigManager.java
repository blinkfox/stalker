package com.blinkfox.stalker.config;

import java.util.ArrayList;

/**
 * Stalker 单例的全局配置管理类.
 *
 * @author blinkfox on 2019-01-30.
 */
public class StalkerConfigManager {

    /** 全局唯一的 StalkerConfigManager 实例. */
    private static final StalkerConfigManager stalkerConfigManager = new StalkerConfigManager();

    /** 全局默认的选项参数. */
    private Options defaultOptions;

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
                .outputs(new ArrayList<>());
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
     * 获取默认的选项参数.
     *
     * @return 选项参数
     */
    public Options getDefaultOptions() {
        return this.defaultOptions;
    }

    /**
     * 重新加载指定的选项参数 options 对象作为默认的 options 对象.
     * <p>重载传入的options前，对options的参数合法性做校验.</p>
     *
     * @param options 选项参数
     */
    public void reLoadOptions(Options options) {
        options.valid();
        this.defaultOptions = options;
    }

}
