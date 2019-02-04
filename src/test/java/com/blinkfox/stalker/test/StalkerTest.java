package com.blinkfox.stalker.test;

import com.blinkfox.stalker.Stalker;
import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.test.prepare.MyTestService;

import org.junit.Test;

/**
 * StalkerTest.
 *
 * @author blinkfox on 2019-02-03.
 */
public class StalkerTest {

    /**
     * 测试没有Options选项参数时的执行情况.
     */
    @Test
    public void runWithoutOptions() {
        Stalker.run(() -> new MyTestService().hello());
    }

    /**
     * 测试有options选项参数时的执行情况.
     */
    @Test
    public void run() {
        Stalker.run(Options.of(100, 20), () -> new MyTestService().hello());
    }

    /**
     * 测试 Options 为 null 时的执行情况.
     */
    @Test(expected = IllegalArgumentException.class)
    public void runWithNullOptions() {
        Options options = null;
        Stalker.run(options, () -> new MyTestService().hello());
    }

    /**
     * 测试 runnalbs 为 null 时的执行情况.
     */
    @Test(expected = IllegalArgumentException.class)
    public void runWithNullRunnalbs() {
        Stalker.run(Options.of(), null);
    }

    /**
     * 测试 runnalbs 为 null 时的执行情况.
     */
    @Test(expected = IllegalArgumentException.class)
    public void runWithEmptyRunnalbs() {
        Runnable[] runnables = new Runnable[]{};
        Stalker.run(Options.of(), runnables);
    }

    /**
     * 测试运行异常时的执行情况.
     */
    @Test
    public void runWithException() {
        Stalker.run(Options.of().runs(1).printErrorLog(true),
                () -> new MyTestService().helloException());
    }

    /**
     * 测试并发运行异常时的执行情况.
     */
    @Test
    public void runWithConcurrentException() {
        Stalker.run(Options.of(2, 2).runs(1).printErrorLog(true),
                () -> new MyTestService().helloException());
    }

}
