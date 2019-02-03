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

}
