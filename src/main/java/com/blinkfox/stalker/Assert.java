package com.blinkfox.stalker;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.runner.MeasureRunnerContext;
import lombok.experimental.UtilityClass;

/**
 * 关于性能测量和评估的断言.
 *
 * @author blinkfox on 2019-01-23.
 * @since v1.0.0
 */
@UtilityClass
public class Assert {

    /**
     * 测试失败.
     */
    public void fail() {
        throw new AssertionError();
    }

    /**
     * 断言 runnable1 比 runnable2 的实例程序执行的平均时间更快.
     *
     * @param options 两个Runnable需要测量运行的参数选项
     * @param runnable1 可运行实例1
     * @param runnable2 可运行实例2
     */
    public void assertFaster(Options options, Runnable runnable1, Runnable runnable2) {
        MeasureRunnerContext runnerContext = new MeasureRunnerContext(options);
        if (runnerContext.run(runnable1).getSum() >= runnerContext.run(runnable2).getSum()) {
            fail();
        }
    }

}
