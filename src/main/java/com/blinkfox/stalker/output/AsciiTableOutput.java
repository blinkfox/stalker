package com.blinkfox.stalker.output;

import com.blinkfox.minitable.MiniTable;
import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.kit.StrKit;
import com.blinkfox.stalker.result.MeasureResult;
import java.util.Arrays;
import java.util.List;

/**
 * 一个生成 ASCII 表格结果的输出类.
 *
 * <p>返回的结果示例如下：</p>
 * +----------------------------------------------------------------------------------------------------------------+
 * |                    duration: 15 s, concurrens: 5, warmups:5, runs: 1, printErrorLog: false                     |
 * +--------+-------+---------+---------+------------+----------+------+-----------+----------+----------+----------+
 * | Costs  | Total | Success | Failure | Throughput |   Avg    | Min  |    Max    |  StdDev  |  95% LC  |  95% UC  |
 * +--------+-------+---------+---------+------------+----------+------+-----------+----------+----------+----------+
 * | 15.0 s | 1210  |  1172   |   38    |   80.66    | 61.45 ms | 0 ns | 103.33 ms | 22.68 ms | 60.17 ms | 62.72 ms |
 * +--------+-------+---------+---------+------------+----------+------+-----------+----------+----------+----------+
 *
 * @author blinkfox on 2020-06-14.
 * @since v1.2.1
 */
public class AsciiTableOutput implements MeasureOutput {

    /**
     * 表头数据的数组.
     */
    private static final List<String> HEADERS = Arrays.asList("Costs", "Total", "Success", "Failure", "Throughput",
            "Avg", "Min", "Max", "StdDev", "95% LC", "95% UC");

    /**
     * 将测量的相关参数和统计结果等信息输出出来.
     *
     * @param options 测量的选项参数
     * @param measureResults 多个测量统计结果的不定集合
     */
    @Override
    public Object output(Options options, MeasureResult... measureResults) {
        if (options == null || measureResults == null || measureResults.length == 0) {
            throw new IllegalArgumentException("options or measureResult is empty.");
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
            MeasureResult result = measureResults[i];
            table.addDatas(result.getEasyReadCosts(),
                    result.getTotal(), result.getSuccess(), result.getFailure(), result.getEasyReadThroughput(),
                    result.getEasyReadAvg(), result.getEasyReadMin(), result.getEasyReadMax(),
                    result.getEasyReadStdDev(), result.getEasyReadLowerConfidence(),
                    result.getEasyReadUpperConfidence());
        }
        return table.render();
    }

}
