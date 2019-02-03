package com.blinkfox.stalker.test.prepare;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于测量（仅测试使用）该类中的方法的执行耗时的类.
 *
 * @author blinkfox on 2019-02-03.
 */
public class MyTestService {

    private static final Logger log = LoggerFactory.getLogger(MyTestService.class);

    /**
     * 测试方法1，模拟业务代码耗时 2~5 ms，且会有约 5% 的几率执行异常.
     */
    public void hello() {
        // 模拟运行抛出异常.
        if (new Random().nextInt(100) == 5) {
            throw new MyServiceException("My Service Exception.");
        }

        // 模拟运行占用约 2~5 ms 的时间.
        try {
            Thread.sleep(2L + new Random().nextInt(3));
        } catch (InterruptedException e) {
            log.info("InterruptedException", e);
            Thread.currentThread().interrupt();
        }
    }

}
