package com.blinkfox.stalker.output;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.Measurement;

import java.util.List;

/**
 * 用于输出测量结果的上下文.
 *
 * @author blinkfox on 2019-01-22.
 */
public class MeasureOutputContext {

    /**
     * 将测量的相关参数和统计结果等信息输出出来.
     *
     * @param options 测量的选项参数
     * @param measurements 多种测量结果
     */
    public void output(Options options, Measurement... measurements) {
        // 如果没有指定任何输出形式，则默认将结果输出到控制台中.
        List<MeasureOutput> outputs = options.getOutputs();
        if (outputs == null || outputs.isEmpty()) {
            new OutputConsole().output(options, measurements);
            return;
        }

        // 如果有多种输出形式，就遍历输出.
        outputs.forEach(measureOutput -> measureOutput.output(options, measurements));
    }

}
