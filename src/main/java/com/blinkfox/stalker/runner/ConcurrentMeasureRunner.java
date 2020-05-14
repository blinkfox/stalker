package com.blinkfox.stalker.runner;

import com.blinkfox.stalker.config.Options;
import com.blinkfox.stalker.result.bean.OverallResult;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * 当待执行的实例方法是在多线程并发情况时的测量运行实现类.
 *
 * @author blinkfox on 2019-01-08.
 * @since v1.0.0
 */
@Slf4j
public class ConcurrentMeasureRunner implements MeasureRunner {

    private static final int N_1024 = 1024;

    /**
     * 每次'成功'测量出的待测量方法的耗时时间，单位为纳秒(ns).
     */
    private final Queue<Long> eachMeasures;

    /**
     * 测量过程中执行的总次数.
     */
    private final AtomicInteger total;

    /**
     * 测量过程中执行成功的次数.
     */
    private final AtomicInteger success;

    /**
     * 测量过程中执行失败的次数.
     */
    private final AtomicInteger failure;

    /**
     * 构造方法.
     *
     * <p>这个类中的属性，需要支持高并发写入.</p>
     */
    public ConcurrentMeasureRunner() {
        this.eachMeasures = new ConcurrentLinkedQueue<>();
        this.total = new AtomicInteger(0);
        this.success = new AtomicInteger(0);
        this.failure = new AtomicInteger(0);
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
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(threads, N_1024));
        final long start = System.nanoTime();

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
                    countDownLatch.countDown();
                }
            });
        }

        // 等待所有线程执行完毕，并关闭线程池，最后将结果封装成实体信息.
        this.awaitAndShutdown(countDownLatch, executorService);
        return this.buildMeasurement(System.nanoTime() - start);
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
            this.total.incrementAndGet();
            try {
                long eachStart = System.nanoTime();
                runnable.run();
                this.eachMeasures.add(System.nanoTime() - eachStart);
                this.success.incrementAndGet();
            } catch (Exception e) {
                // 如果待测量的方法，执行错误则失败数 +1,且根据选项参数来判断是否打印异常错误日志.
                this.failure.incrementAndGet();
                if (printErrorLog) {
                    log.error("测量方法耗时信息在多线程下出错!", e);
                }
            }
        }
    }

    /**
     * 等待所有线程执行完毕，并最终关闭线程池.
     *
     * @param countDownLatch countDownLatch实例
     * @param executorService 线程池
     */
    private void awaitAndShutdown(CountDownLatch countDownLatch, ExecutorService executorService) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("在多线程下等待测量结果结束时出错!", e);
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * 构造测量的结果信息的 Measurement 对象.
     *
     * @param costs 消耗的总耗时，单位是纳秒
     * @return Measurement对象
     */
    private OverallResult buildMeasurement(long costs) {
        // 将队列转数组.
        int len = this.eachMeasures.size();
        long[] measures = new long[len];
        for (int i = 0; i < len; i++) {
            measures[i] = eachMeasures.remove();
        }

        return new OverallResult()
                .setEachMeasures(measures)
                .setCosts(costs)
                .setTotal(this.total.get())
                .setSuccess(this.success.get())
                .setFailure(this.failure.get());
    }

}
