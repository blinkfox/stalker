package com.blinkfox.stalker.output;

import com.blinkfox.minitable.MiniTable;
import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.kit.StrKit;
import com.blinkfox.stalker.result.StatisResult;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 在控制台输出结果的实现类.
 *
 * <p>控制台输出的结果示例如下：</p>
 * +----------------------------------------------------------------------------------------------------------------+
 * |                     threads: 1000, concurrens: 100, warmups:5, runs: 5, printErrorLog: false                   |
 * +-----------+-------+---------+---------+---------+---------+---------+----------+---------+---------------------+
 * |   Costs   | Total | Success | Failure |   Sum   |   Avg   |   Min   |   Max    | StdDev  | 95% LowerConfidence |
 * +-----------+-------+---------+---------+---------+---------+---------+----------+---------+---------------------+
 * | 397.15 ms | 5000  |  4968   |   32    | 23.44 s | 4.72 ms | 2.01 ms | 13.26 ms | 1.81 ms |       4.67 ms       |
 * +-----------+-------+---------+---------+---------+---------+---------+----------+---------+---------------------+
 *
 * @author blinkfox on 2019-01-11.
 * @since v1.0.0
 */
@Slf4j
public class OutputConsole implements MeasureOutput {

    /**
     * 表头数据的数组.
     */
    private static final List<String> HEADERS = Arrays.asList("", "Costs", "Total", "Success", "Failure", "Throughput",
            "Sum", "Avg", "Min", "Max", "StdDev", "95% LowerConfidence", "95% UpperConfidence");

    /**
     * 将测量的相关参数和统计结果等信息输出出来.
     *
     * @param options 测量的选项参数
     * @param measurements 多种测量结果
     */
    @Override
    public Object output(Options options, StatisResult... measurements) {
        // 渲染并打印结果.
        String result = this.getRenderResult(options, measurements);
        log.warn("\n{}", result);
        return result;
    }

    /**
     * 获取最终需要渲染的结果.
     *
     * @param options 测量运行的相关选项参数
     * @param measureResults 多个测量结果的不定集合
     * @return 结果字符串
     */
    private String getRenderResult(Options options, StatisResult... measureResults) {
        if (options == null || measureResults == null) {
            throw new IllegalArgumentException("options or measureStatisResult is null.");
        }

        // 根据options的值, 拼接title.
        String title;
        if (options.getDuration() != null) {
            title = StrKit.join("duration: ", options.getDuration().toString(),
                    ", concurrens: ", options.getConcurrens(), ", warmups:", options.getWarmups(),
                    ", runs: ", options.getRuns(), ", printErrorLog: ", options.isPrintErrorLog());
        } else {
            title = StrKit.join("threads: ", options.getThreads(),
                    ", concurrens: ", options.getConcurrens(), ", warmups:", options.getWarmups(),
                    ", runs: ", options.getRuns(), ", printErrorLog: ", options.isPrintErrorLog());
        }
        String name = options.getName();
        title = StrKit.isEmpty(name) ? title : StrKit.join("name: ", name, ", ", title);

        // 拼接各个测量结果的字符串表头和内容.
        MiniTable table = new MiniTable(title).addHeaders(HEADERS);
        for (int i = 0, len = measureResults.length; i < len; i++) {
            StatisResult result = measureResults[i];
            table.addDatas(i + 1, result.getEasyReadCosts(),
                    result.getTotal(), result.getSuccess(), result.getFailure(), result.getEasyReadThroughput(),
                    result.getEasyReadSum(), result.getEasyReadAvg(), result.getEasyReadMin(), result.getEasyReadMax(),
                    result.getEasyReadStdDev(), result.getEasyReadLowerConfidence(),
                    result.getEasyReadUpperConfidence());
        }
        return table.render();
    }

}
