package com.blinkfox.stalker.test;

import com.blinkfox.stalker.Stalker;
import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.kit.StrKit;
import com.blinkfox.stalker.output.AsciiTableOutput;
import com.blinkfox.stalker.output.MeasureOutput;
import com.blinkfox.stalker.result.StalkerFuture;
import com.blinkfox.stalker.test.prepare.MyTestService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
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
     * 测试有 duration 选项参数时的执行情况.
     */
    @Test
    public void runWithDuration() {
        Stalker.run(Options.ofDuration(1, TimeUnit.SECONDS), () -> new MyTestService().hello());
    }

    /**
     * 测试有 duration 选项参数时的执行情况.
     */
    @Test
    public void runWithDurationConcurrent() {
        Stalker.run(Options.ofDurationSeconds(1, 3), () -> new MyTestService().hello());
    }

    /**
     * 测试有 duration 选项参数时的执行情况.
     */
    @Test(expected = IllegalArgumentException.class)
    public void runWithDurationException() {
        Stalker.run(Options.ofDuration(2, TimeUnit.MILLISECONDS), () -> new MyTestService().hello());
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
    public void submit() {
        StalkerFuture stalkerFuture = Stalker
                .submit(() -> new MyTestService().hello())
                .waitDone(future -> Assert.assertNotNull(future.getFirst()), 2L)
                .done(future -> {
                    log.info("任务已完成，获取最后的执行结果.");
                    future.get();
                });
        Assert.assertTrue(stalkerFuture.isDone());
    }

    /**
     * 测试慢方法的执行情况.
     */
    @Test
    public void submitWithSlowMethod() {
        StalkerFuture stalkerFuture = Stalker
                .submit(Options.of("SlowTest", 20, 5), () -> new MyTestService().slowHello())
                .waitDone(future -> {
                    Assert.assertNotNull(future.getFirst());
                }, 50L)
                .done(future -> {
                    log.info("任务已完成，获取最后的执行结果.");
                    future.get();
                });
        Assert.assertTrue(stalkerFuture.isDone());
    }

    /**
     * 测试慢方法的执行情况.
     */
    @Test
    public void submitWithSlowMethodDuration() {
        StalkerFuture stalkerFuture = Stalker
                .submit(Options.ofDurationSeconds(2, 4), () -> new MyTestService().slowHello());
        Assert.assertEquals(0, stalkerFuture.getEndNanoTime());

        // 执行过程中或执行完成后执行的方法.
        stalkerFuture
                .waitDone(future -> {
                    Assert.assertNotNull(future.getFirst());
                })
                .done(future -> {
                    log.info("任务已完成，获取最后的执行结果.");
                    future.get();
                });

        Assert.assertTrue(stalkerFuture.getStartNanoTime() > 0);
        Assert.assertTrue(stalkerFuture.isDoneSuccessfully());
        Assert.assertEquals(stalkerFuture.getTotal(), stalkerFuture.getSuccess() + stalkerFuture.getFailure());
        Assert.assertTrue(stalkerFuture.getCosts() > 0);
    }

    /**
     * 测试 queryMeasurement 方法.
     */
    @Test
    public void queryMeasureResult() {
        Stalker.submit(() -> new MyTestService().hello())
                .waitDone(future -> Assert.assertNotNull(future.getMeasureResult()), 5L)
                .done(future -> {
                    Assert.assertFalse(future.isCancelled());
                    Assert.assertNotNull(future.getMeasureResult());
                });
    }

    /**
     * 测试慢方法的执行情况.
     */
    @Test
    public void submitWithStop() throws InterruptedException {
        StalkerFuture stalkerFuture = Stalker.submit(Options.of("StopTest", 200, 5, 1),
                () -> new MyTestService().slowHello());
        Assert.assertNotNull(stalkerFuture);

        Thread.sleep(50L);
        Assert.assertNotNull(stalkerFuture.getFirst());

        // 取消任务.
        boolean isCancelled = stalkerFuture.cancel();
        if (isCancelled) {
            log.info("任务已停止，获取停止前的执行结果.");
        } else {
            log.info("任务停止失败.");
        }

        stalkerFuture.done(StalkerFuture::get);
        Thread.sleep(100L);

        log.info("任务已停止，再次获取最后的执行结果，判断内容是否一致.");
        stalkerFuture.get();
    }

    /**
     * 测试慢方法的执行情况.
     */
    @Test
    public void submitWithDuration() {
        // 构造新的输出结果.
        Options options = Options.ofDurationSeconds(15, 5)
                .outputs(MeasureOutput.ofList(new AsciiTableOutput()));

        Stalker.submit(options, () -> new MyTestService().slowHello())
                .waitDone(future -> log.info("\n{}\n当前进度: [{}%].", future.getFirst(),
                        StrKit.roundToString(Stalker.getProgress(options, future.getMeasureResult()))), 3000L)
                .done(future -> {
                    log.info("任务已完成，进度:【{}%】，获取最终的执行结果信息.",
                            StrKit.roundToString(Stalker.getProgress(options, future.getMeasureResult())));
                    log.info("\n{}.", future.getFirst());
                    Assert.assertTrue(future.getStartNanoTime() > 0);
                    Assert.assertTrue(future.isDone());
                    Assert.assertEquals(future.getTotal(), future.getSuccess() + future.getFailure());
                    Assert.assertTrue(future.getCosts() > 0);
                });
    }

}
