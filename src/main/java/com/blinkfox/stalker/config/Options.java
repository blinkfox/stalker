package com.blinkfox.stalker.config;

import com.blinkfox.stalker.output.MeasureOutput;

import java.util.Arrays;
import java.util.List;

/**
 * 性能测试参数选项实体类.
 *
 * @author blinkfox on 2019-1-2.
 */
public class Options {

    /** 进行测量的名称，便于和其他执行的测量作区分. */
    private String name;

    /** 执行的线程数. */
    private int threads;

    /** 执行的并发数. */
    private int concurrens;

    /** 执行前的预热次数. */
    private int warmups;

    /** 执行的运行次数. */
    private int runs;

    /** 是否打印出执行错误(异常运行)的日志，默认是false. */
    private boolean printErrorLog;

    /** 输出 */
    private List<MeasureOutput> outputs;

    /** 校验失败时的提示消息. */
    private String message;

    /**
     * 根据'执行次数'来构建Options实例.
     *
     * @return Options实例
     */
    public static Options of() {
        // 从全局唯一的配置实例中获取到默认的选项参数实例，并将默认的属性值赋给新的 Options 实例.
        Options defaultOptions = StalkerConfigManager.getInstance().getDefaultOptions();
        return new Options()
                .threads(defaultOptions.getThreads())
                .concurrens(defaultOptions.getConcurrens())
                .warmups(defaultOptions.getWarmups())
                .runs(defaultOptions.getRuns())
                .printErrorLog(defaultOptions.isPrintErrorLog())
                .outputs(defaultOptions.getOutputs());
    }

    /**
     * 根据'执行次数'来构建Options实例.
     *
     * @param name 名称
     * @return Options实例
     */
    public static Options of(String name) {
        Options options = of();
        options.name = name;
        return options;
    }

    /**
     * 根据'执行次数'来构建Options实例.
     *
     * @param runs 执行次数
     * @return Options实例
     */
    public static Options of(int runs) {
        Options options = of();
        options.runs = runs;
        return options;
    }

    /**
     * 根据'执行次数'来构建Options实例.
     *
     * @param name 名称
     * @param runs 执行次数
     * @return Options实例
     */
    public static Options of(String name, int runs) {
        Options options = of(name);
        options.runs = runs;
        return options;
    }

    /**
     * 根据'线程数'、'并发数'来构建Options实例.
     *
     * @param threads 线程数
     * @param concurrens 并发数
     * @return Options实例
     */
    public static Options of(int threads, int concurrens) {
        Options options = of();
        options.threads = threads;
        options.concurrens = concurrens;
        return options;
    }

    /**
     * 根据'线程数'、'并发数'来构建Options实例.
     *
     * @param name 名称
     * @param threads 线程数
     * @param concurrens 并发数
     * @return Options实例
     */
    public static Options of(String name, int threads, int concurrens) {
        Options options = of(threads, concurrens);
        options.name = name;
        return options;
    }

    /**
     * 根据'线程数'、'并发数'、每个线程的'执行次数'来构建Options实例.
     *
     * @param name 名称
     * @param threads 线程数
     * @param concurrens 并发数
     * @param runs 每个线程的执行次数
     * @return Options实例
     */
    public static Options of(String name, int threads, int concurrens, int runs) {
        Options options = of(name, threads, concurrens);
        options.runs = runs;
        return options;
    }

    /**
     * 校验需要进行测量的 Options 选项参数是否合法，如果不合法，则抛出异常.
     */
    public void valid() {
        // 对各个选项参数进行检查、校验，失败时抛出异常.
        if (this.verify(this.getThreads() < 0, "Options 中的线程数 threads 的值必须大于0.")
                && this.verify(this.getConcurrens() < 0, "Options 中的线程数 concurrens 的值必须大于0.")
                && this.verify(this.getWarmups() < 0, "Options 中的线程数 warmups 的值必须大于0.")
                && this.verify(this.getRuns() < 0, "Options 中的线程数 runs 的值必须大于0.")) {
            throw new IllegalArgumentException(this.message);
        }
    }

    /**
     * 检查结果是否为false，如果为false，则记录message提示信息.
     *
     * @param condition 检查结果
     * @param message 提示信息
     * @return 布尔值
     */
    private boolean verify(boolean condition, String message) {
        if (condition) {
            this.message = message;
        }
        return condition;
    }

    /**
     * 设置测量名称 name 的属性值.
     *
     * @param name 线程数
     * @return Options实例
     */
    public Options named(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置线程数 threads 的属性值.
     *
     * @param threads 线程数
     * @return Options实例
     */
    public Options threads(int threads) {
        this.threads = threads;
        return this;
    }

    /**
     * 设置线程数 concurrens 的属性值.
     *
     * @param concurrens 并发数
     * @return Options实例
     */
    public Options concurrens(int concurrens) {
        this.concurrens = concurrens;
        return this;
    }

    /**
     * 设置预热次数的 warmups 的属性值.
     *
     * @param warmups 热加载次数
     * @return Options实例
     */
    public Options warmups(int warmups) {
        this.warmups = warmups;
        return this;
    }

    /**
     * 设置执行次数的 runs 的属性值.
     *
     * @param runs 运行次数
     * @return Options实例
     */
    public Options runs(int runs) {
        this.runs = runs;
        return this;
    }

    /**
     * 设置是否打印运行错误的日志的 printErrorLog 的属性值.
     *
     * @param isPrint 是否打印异常运行的日志
     * @return Options实例
     */
    public Options printErrorLog(boolean isPrint) {
        this.printErrorLog = isPrint;
        return this;
    }

    /**
     * 设置需要将结果输出的通道.
     *
     * @param measureOutputs 输出通道的集合.
     */
    public Options outputs(MeasureOutput... measureOutputs) {
        this.outputs.addAll(Arrays.asList(measureOutputs));
        return this;
    }

    /**
     * 设置需要将结果输出的通道.
     *
     * @param outputs 输出通道的集合.
     */
    public Options outputs(List<MeasureOutput> outputs) {
        this.outputs = outputs;
        return this;
    }

    /* getter 方法. */

    public String getName() {
        return name;
    }

    public int getThreads() {
        return threads;
    }

    public int getConcurrens() {
        return concurrens;
    }

    public int getWarmups() {
        return warmups;
    }

    public int getRuns() {
        return runs;
    }

    public boolean isPrintErrorLog() {
        return printErrorLog;
    }

    public List<MeasureOutput> getOutputs() {
        return outputs;
    }

}