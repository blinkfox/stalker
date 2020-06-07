package com.blinkfox.stalker.exception;

/**
 * Stalker 运行过程中相关的运行时异常.
 *
 * @author blinkfox on 2020-05-23.
 * @since v1.2.0
 */
public class StalkerException extends RuntimeException {

    private static final long serialVersionUID = 8452522047738865868L;

    /**
     * 异常实例的构造方法.
     *
     * @param s 异常描述信息
     */
    public StalkerException(String s) {
        super(s);
    }

    /**
     * 附带异常实例的构造方法.
     *
     * @param s 异常描述信息
     * @param e 异常实例
     */
    public StalkerException(String s, Throwable e) {
        super(s, e);
    }

}
