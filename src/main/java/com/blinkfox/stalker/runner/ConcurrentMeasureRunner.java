package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.OverallResult;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;

/**
 * 当待执行的实例方法是在多线程并发情况时的测量运行实现类.
 *
 * @author blinkfox on 2019-01-08.
 * @since v1.0.0
 */
@Slf4j
public class ConcurrentMeasureRunner extends AbstractMeasureRunner {

    private static final int N_1024 = 1024;

    /**
     * 构造方法.
     *
     * <p>这个类中的属性，需要支持高并发写入.</p>
     */
    public ConcurrentMeasureRunner() {
        super();
    }

    /**
     * 执行 runnable 方法，并将执行结果的耗时纳秒(ns)值存入结果对象中.
     *
     * @param options 运行的配置选项实例
     * @param runnable 可运行实例
     * @return 测量结果
     */
    @Override
    public OverallResult run(Options options, Runnable runnable) {
        int threads = options.getThreads();
        int concurrens = options.getConcurrens();
        int runs = options.getRuns();
        boolean printErrorLog = options.isPrintErrorLog();

        // 初始化存储的集合、线程池、并发工具类中的对象实例等.
        Semaphore semaphore = new Semaphore(Math.min(concurrens, threads));
        super.countLatch = new CountDownLatch(threads);
        super.executorService = Executors.newFixedThreadPool(Math.min(threads, N_1024));
        super.startNanoTime = System.nanoTime();

        // 在多线程下控制线程并发量，与循环搭配来一起执行和测量.
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                try {
                    semaphore.acquire();
                    this.loopMeasure(runs, printErrorLog, runnable);
                    semaphore.release();
                } catch (InterruptedException e) {
                    log.error("测量方法耗时信息在多线程下出错!", e);
                    Thread.currentThread().interrupt();
                } finally {
                    super.countLatch.countDown();
                }
            });
        }

        // 等待所有线程执行完毕，并关闭线程池，最后将结果封装成实体信息.
        super.awaitAndShutdown();
        super.endNanoTime = System.nanoTime();
        super.complete.compareAndSet(false, true);
        return super.buildFinalMeasurement();
    }

    /**
     * 单个线程的循环批量执行测量.
     *
     * @param runs 批量运行次数
     * @param printErrorLog 是否打印输出错误日志
     * @param runnable 可执行实例
     */
    private void loopMeasure(int runs, boolean printErrorLog, final Runnable runnable) {
        for (int j = 0; j < runs; j++) {
            try {
                long eachStart = System.nanoTime();
                runnable.run();
                super.eachMeasures.add(System.nanoTime() - eachStart);
                super.success.increment();
            } catch (Exception e) {
                // 如果待测量的方法，执行错误则失败数 +1,且根据选项参数来判断是否打印异常错误日志.
                super.failure.increment();
                if (printErrorLog) {
                    log.error("测量方法耗时信息在多线程下出错!", e);
                }
            }
            super.total.increment();
        }
    }

    /**
     * 停止相关的运行测量任务.
     *
     * @return 是否成功的布尔值
     * @author blinkfox on 2020-05-25.
     * @since v1.2.0
     */
    public boolean stop() {
        // TODO
        return false;
    }

}
