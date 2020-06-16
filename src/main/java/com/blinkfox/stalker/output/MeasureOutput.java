package com.blinkfox.stalker.output;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.MeasureResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * @param measureResults 多个测量统计结果的不定集合
     * @return 输出结果
     */
    Object output(Options options, MeasureResult... measureResults);

    /**
     * 基于动态传入的不定个数的 {@link MeasureOutput} 实例构造新的集合.
     *
     * @param measureOutputs {@link MeasureOutput} 实例
     * @return {@link MeasureOutput} 实例集合
     * @author blinkfox on 2020-06-16.
     * @since v1.2.2
     */
    static List<MeasureOutput> ofList(MeasureOutput... measureOutputs) {
        return measureOutputs == null || measureOutputs.length == 0
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(measureOutputs));
    }

}
