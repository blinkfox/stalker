package com.blinkfox.stalker.runner.executor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * Stalker 中线程池队列满了之后的拒绝执行策略.
 *
 * @author blinkfox on 2020-06-02.
 * @since v1.2.0
 */
@Slf4j
public class StalkerRejectedHandler implements RejectedExecutionHandler {

    /**
     * 当线程池队列满了之后，将拒绝接收新的任务，即放弃任务并打印出 {@code warn} 级别的日志基于警示.
     *
     * @param r 可运行任务
     * @param executor 线程池执行器
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.warn("【Stalker 警示】线程池队列任务已满，将拒绝接收新的执行任务，建议你调低运行的【并发数】。");
    }

}
