package com.blinkfox.stalker.test;

import com.blinkfox.stalker.Stalker;
import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.Measurement;
import com.blinkfox.stalker.test.prepare.MyTestService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * StalkerTest.
 *
 * @author blinkfox on 2019-02-03.
 * @since v1.0.0
 */
@Slf4j
public class StalkerTest {

    /**
     * 测试没有Options选项参数时的执行情况.
     */
    @Test
    public void runWithoutOptions() {
        Assert.assertTrue(Stalker.run(() -> new MyTestService().hello()).size() > 0);
    }

    /**
     * 测试有options选项参数时的执行情况.
     */
    @Test
    public void run() {
        Stalker.run(Options.of(100, 20), () -> new MyTestService().hello());
    }

    /**
     * 测试简单无并发的执行情况.
     */
    @Test
    public void runWithSimple() {
        Stalker.run(Options.of(100, 1).runs(3), () -> new MyTestService().hello());
    }

    /**
     * 测试有options选项参数时的执行情况.
     */
    @Test
    public void runMultiRunnables() {
        Stalker.run(Options.of(1000, 200).warmups(10).runs(5),
                () -> new MyTestService().hello(),
                () -> new MyTestService().fastHello());
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
        Runnable[] runnables = new Runnable[] {};
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

    /**
     * 测试慢方法的执行情况.
     */
    @Test
    public void runWithSlowMethod() {
        Stalker.run(Options.of("SlowTest", 20, 5, 1),
                () -> new MyTestService().slowHello());
    }

    /**
     * 测试没有Options选项参数时的执行情况.
     */
    @Test
    public void submit() throws InterruptedException {
        String sessionId = Stalker.submit(() -> new MyTestService().hello());
        Assert.assertNotNull(sessionId);

        while (Stalker.isRunning(sessionId)) {
            List<Object> results = Stalker.query(sessionId);
            Assert.assertNotNull(results.get(0));
            Thread.sleep(3L);
        }

        log.info("任务已完成，获取最后的执行结果，并移除任务记录.");
        Stalker.query(sessionId);
        // 执行完成之后移除 sessionId.
        Stalker.remove(sessionId);
    }

    /**
     * 测试慢方法的执行情况.
     */
    @Test
    public void submitWithSlowMethod() throws InterruptedException {
        String sessionId = Stalker.submit(Options.of("SlowTest", 20, 5, 1),
                () -> new MyTestService().slowHello());
        Assert.assertNotNull(sessionId);

        while (Stalker.isRunning(sessionId)) {
            List<Object> results = Stalker.query(sessionId);
            Assert.assertNotNull(results.get(0));
            Thread.sleep(50L);
        }

        log.info("任务已完成，获取最后的执行结果，并移除任务记录.");
        Stalker.query(sessionId);

        // 执行完成之后移除 sessionId.
        Stalker.remove(sessionId);
    }

    /**
     * 测试 queryMeasurement 方法.
     */
    @Test
    public void queryMeasurement() throws InterruptedException {
        String sessionId = Stalker.submit(() -> new MyTestService().hello());
        Assert.assertNotNull(sessionId);

        while (Stalker.isRunning(sessionId)) {
            Measurement measurement = Stalker.queryMeasurement(sessionId);
            Assert.assertNotNull(measurement);
            Thread.sleep(5L);
        }
        // 执行完成之后移除 sessionId.
        Stalker.remove(sessionId);
    }

    /**
     * 测试慢方法的执行情况.
     */
    @Test
    public void submitWithStop() throws InterruptedException {
        String sessionId = Stalker.submit(Options.of("StopTest", 20, 5, 1),
                () -> new MyTestService().slowHello());
        Assert.assertNotNull(sessionId);

        Thread.sleep(50L);
        List<Object> results = Stalker.query(sessionId);
        Assert.assertNotNull(results.get(0));
        Stalker.stop(sessionId);
        log.info("任务已停止，获取停止前的执行结果.");
        Stalker.query(sessionId);
        Thread.sleep(100L);

        log.info("任务已停止，获取最后的执行结果.");
        Stalker.query(sessionId);

        // 执行完成之后移除 sessionId.
        Stalker.remove(sessionId);
    }

    /**
     * 单测结束后执行的方法.
     */
    @AfterClass
    public static void destroy() {
        Assert.assertNotNull(Stalker.getAllSessions());
        Stalker.clear();
    }

}
