package com.blinkfox.stalker.kit;

import lombok.experimental.UtilityClass;

/**
 * 数学计算的相关工具类.
 *
 * @author blinkfox on 2020-05-15.
 * @since v1.0.0
 */
@UtilityClass
public class MathKit {

    /**
     * 根据执行总数和消耗的总时间来计算吞吐率.
     *
     * <p>吞吐率，指单位时间内（每秒）的执行总次数，即：{@code throughput = count / (costs / 10^9)}.</p>
     *
     * @param count 执行总次数
     * @param costs 消耗的总时间
     * @return 吞吐率
     */
    public double calcThroughput(long count, long costs) {
        return costs == 0 ? 0.0d : count / ((double) costs / 1e9);
    }

}
