package com.blinkfox.stalker;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.runner.MeasureRunnerContext;

/**
 * 关于性能测量和评估的断言.
 *
 * @author blinkfox on 2019-01-23.
 */
public final class Assert {

    /**
     * 私有构造方法，防止new.
     */
    private Assert() {}

    /**
     * 测试失败.
     */
    public static void fail() {
        throw new AssertionError();
    }

    /**
     * 断言 runnable1 比 runnable2 的实例程序执行的平均时间更快.
     *
     * @param options 两个Runnable需要测量运行的参数选项
     * @param runnable1 可运行实例1
     * @param runnable2 可运行实例2
     */
    public static void assertFaster(Options options, Runnable runnable1, Runnable runnable2) {
        MeasureRunnerContext runnerContext = new MeasureRunnerContext(options);
        if (calcSum(runnerContext.run(runnable1).getEachMeasures())
                >= calcSum(runnerContext.run(runnable2).getEachMeasures())) {
            fail();
        }
    }

    /**
     * 计算long数组中的总和.
     *
     * @param arr long数组
     * @return 总和
     */
    private static long calcSum(long[] arr) {
        int sum = 0;
        if (arr != null && arr.length > 0) {
            for (long n : arr) {
                sum += n;
            }
        }
        return sum;
    }

}
