package com.blinkfox.stalker.test.prepare;

/**
 * 测试业务的自定义运行时异常.
 *
 * @author blinkfox on 2019-02-03.
 * @since v1.0.0
 */
public class MyServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 附带日志消息参数的构造方法.
     *
     * @param msg 日志消息
     */
    public MyServiceException(String msg) {
        super(msg);
    }

}
