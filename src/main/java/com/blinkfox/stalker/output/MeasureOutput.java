package com.blinkfox.stalker.output;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.Measurement;

/**
 * 将最终的测量统计结果输出出来.
 *
 * @author blinkfox on 2019-01-11.
 * @since v1.0.0
 */
public interface MeasureOutput {

    /**
     * 将测量的相关参数和统计结果等信息输出出来.
     *
     * @param options 测量的选项参数
     * @param measurements 多种测量结果
     * @return 输出结果
     */
    Object output(Options options, Measurement... measurements);

}
