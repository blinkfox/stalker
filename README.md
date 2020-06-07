# stalker

[![HitCount](http://hits.dwyl.io/blinkfox/stalker.svg)](http://hits.dwyl.io/blinkfox/stalker) [![Build Status](https://secure.travis-ci.org/blinkfox/stalker.svg)](https://travis-ci.org/blinkfox/stalker) [![GitHub license](https://img.shields.io/github/license/blinkfox/stalker.svg)](https://github.com/blinkfox/stalker/blob/master/LICENSE) [![codecov](https://codecov.io/gh/blinkfox/stalker/branch/master/graph/badge.svg)](https://codecov.io/gh/blinkfox/stalker) ![Java Version](https://img.shields.io/badge/Java-%3E%3D%208-blue.svg)

> 这是一个简单的用来对 Java 代码做性能评估的工具库。

## 一、特性

- 轻量级（jar包仅`49kb`）
- 支持对性能的多种统计纬度
- API简单易用，易于集成或扩展

## 二、快速集成

### 1. Maven

```xml
<dependency>
    <groupId>com.blinkfox</groupId>
    <artifactId>stalker</artifactId>
    <version>1.2.0</version>
</dependency>
```

### 2. Gradle

```bash
compile 'com.blinkfox:stalker:1.2.0'
```

## 三、API 介绍和使用

### 预先准备

在对Java方法做性能测试之前，先准备好待测试的类和方法：

```java
/**
 * 用于测量（仅测试使用）该类中的方法的执行耗时的类.
 *
 * @author blinkfox on 2019-02-03.
 */
@Slf4j
public class MyTestService {

    /**
     * 测试方法1，模拟业务代码耗时 2~5 ms，且会有约 1% 的几率执行异常.
     */
    public void hello() {
        // 模拟运行时抛出异常.
        if (new Random().nextInt(100) == 5) {
            throw new MyServiceException("My Service Exception.");
        }

        // 模拟运行占用约 2~5 ms 的时间.
        this.sleep(2L + new Random().nextInt(3));
    }

    /**
     * 测试方法2，模拟业务代码运行占用约 2 ms 的时间.
     */
    public void fastHello() {
        this.sleep(2L);
    }

    /**
     * 本线程调用该方法时，睡眠指定时间，用来模拟业务耗时.
     *
     * @param time 时间
     */
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.info("InterruptedException", e);
            Thread.currentThread().interrupt();
        }
    }

}
```

### Stalker 类

#### 1. 最简示例

以下代码将会预热`5`次，然后在单线程下正式执行`10`次，从而将运行结果计算统计并输出出来：

```java
public static void main(String[] args) {
    Stalker.run(() -> new MyTestService().hello());
}
```

以上结果将默认在控制台输出：

```bash
+------------------------------------------------------------------------------------------------------------------------------------------------------+
|                                         threads: 1, concurrens: 1, warmups:5, runs: 10, printErrorLog: false                                         |
+---+----------+-------+---------+---------+------------+----------+---------+---------+---------+---------+---------------------+---------------------+
|   |  Costs   | Total | Success | Failure | Throughput |   Sum    |   Avg   |   Min   |   Max   | StdDev  | 95% LowerConfidence | 95% UpperConfidence |
+---+----------+-------+---------+---------+------------+----------+---------+---------+---------+---------+---------------------+---------------------+
| 1 | 40.52 ms |  10   |   10    |    0    |   246.76   | 40.43 ms | 4.04 ms | 2.24 ms | 7.74 ms | 1.56 ms |       3.07 ms       |       5.01 ms       |
+---+----------+-------+---------+---------+------------+----------+---------+---------+---------+---------+---------------------+---------------------+
```

也可以获取到统计结果:

```java
// 获取运行的统计结果.
MeasureResult[] measureResults = mStalker.runStatis(() -> new MyTestService().hello());

// 获取运行的 Options 中指定的 MeasureOutput 结果，默认是控制台中输出的 ASCII 表格的字符串内容，
// 你可以实现 MeasureOutput 接口，来实现自定义的结果返回，可以是多个结果，所以返回集合.
List<Object> measurements = mStalker.run(() -> new MyTestService().hello());
```

#### 2. 更全示例

以下代码表示，两个方法`hello()`和`fastHello()`将会预热`10`次，在`1000`个线程`200`个并发下，每次执行`5`次：

```java
Stalker.run(Options.of(1000, 200).warmups(10).runs(5),
        () -> new MyTestService().hello(),
        () -> new MyTestService().fastHello());
```

以上结果将默认在控制台输出：

```bash
+-------------------------------------------------------------------------------------------------------------------------------------------------------+
|                                       threads: 1000, concurrens: 200, warmups:10, runs: 5, printErrorLog: false                                       |
+---+-----------+-------+---------+---------+------------+---------+---------+---------+----------+---------+---------------------+---------------------+
|   |   Costs   | Total | Success | Failure | Throughput |   Sum   |   Avg   |   Min   |   Max    | StdDev  | 95% LowerConfidence | 95% UpperConfidence |
+---+-----------+-------+---------+---------+------------+---------+---------+---------+----------+---------+---------------------+---------------------+
| 1 | 668.93 ms | 5000  |  4955   |   45    |  7474.57   | 17.22 s | 3.48 ms | 2.01 ms | 11.66 ms | 1.14 ms |       3.44 ms       |       3.51 ms       |
| 2 | 348.85 ms | 5000  |  5000   |    0    |  14332.69  | 11.23 s | 2.25 ms | 2.01 ms | 3.32 ms  | 0.19 ms |       2.24 ms       |       2.25 ms       |
+---+-----------+-------+---------+---------+------------+---------+---------+---------+----------+---------+---------------------+---------------------+
```

结果说明：

- `Costs`: 实际正式运行所消耗的总时间
- `Total`: 正式运行的总次数
- `Success`: 正式运行的成功次数
- `Failure`: 正式运行的失败次数
- `Throughput`: 正式运行的吞吐量
- `Sum`: 每次运行的耗时结果求和之后的值
- `Avg`: 所有运行耗时结果的算术平均数
- `Min`: 所有运行耗时结果中最小值
- `Max`: 所有运行耗时结果中最大值
- `StdDev`: 所有运行耗时结果的标准方差
- `95% LowerConfidence`: 95%置信区间的最小边界值
- `95% LowerConfidence`: 95%置信区间的最大边界值

#### 3. submit 异步执行

Stalker 中的 `run` 方法默认是同步执行的，如果你的性能测试任务耗时比较久，可以直接调用 `submit` 来异步提交性能测试任务，`submit` 将返回 `StalkerFuture` 的实例，后续你就可以通过 `StalkerFuture` 实时获取任务执行情况或取消执行中的任务等。

下面是在 20 个线程、5 并发下的异步执行情况：

```java
@Test
public void submitWithSlowMethod() throws InterruptedException {
    StalkerFuture stalkerFuture = Stalker.submit(Options.of("SlowTest", 20, 5, 1),
            () -> new MyTestService().slowHello());
    Assert.assertNotNull(stalkerFuture);

    while (!stalkerFuture.isDone()) {
        List<Object> results = stalkerFuture.get();
        Assert.assertNotNull(results.get(0));
        Thread.sleep(50L);
    }

    log.info("任务已完成，获取最后的执行结果.");
    stalkerFuture.get();
}
```

执行将得到如下类似结果：

```bash
+-------------------------------------------------------------------------------------------------------------------------------------+
|                               duration: 2 s, concurrens: 4, warmups:5, runs: 1, printErrorLog: false                                |
+---+-------+-------+---------+---------+------------+------+------+------+------+--------+---------------------+---------------------+
|   | Costs | Total | Success | Failure | Throughput | Sum  | Avg  | Min  | Max  | StdDev | 95% LowerConfidence | 95% UpperConfidence |
+---+-------+-------+---------+---------+------------+------+------+------+------+--------+---------------------+---------------------+
| 1 | 0 ns  |   0   |    0    |    0    |    0.00    | 0 ns | 0 ns | 0 ns | 0 ns |  0 ns  |        0 ns         |        0 ns         |
+---+-------+-------+---------+---------+------------+------+------+------+------+--------+---------------------+---------------------+

+---------------------------------------------------------------------------------------------------------------------------------------------------+
|                                      duration: 2 s, concurrens: 4, warmups:5, runs: 1, printErrorLog: false                                       |
+---+--------+-------+---------+---------+------------+--------+----------+------+-----------+----------+---------------------+---------------------+
|   | Costs  | Total | Success | Failure | Throughput |  Sum   |   Avg    | Min  |    Max    |  StdDev  | 95% LowerConfidence | 95% UpperConfidence |
+---+--------+-------+---------+---------+------------+--------+----------+------+-----------+----------+---------------------+---------------------+
| 1 | 1.03 s |  69   |   66    |    3    |   66.86    | 3.92 s | 56.76 ms | 0 ns | 102.38 ms | 24.13 ms |      51.06 ms       |      62.45 ms       |
+---+--------+-------+---------+---------+------------+--------+----------+------+-----------+----------+---------------------+---------------------+

[main] INFO com.blinkfox.stalker.test.StalkerTest - 任务已完成，获取最后的执行结果，并移除任务记录.
+--------------------------------------------------------------------------------------------------------------------------------------------------+
|                                      duration: 2 s, concurrens: 4, warmups:5, runs: 1, printErrorLog: false                                      |
+---+--------+-------+---------+---------+------------+--------+---------+------+-----------+----------+---------------------+---------------------+
|   | Costs  | Total | Success | Failure | Throughput |  Sum   |   Avg   | Min  |    Max    |  StdDev  | 95% LowerConfidence | 95% UpperConfidence |
+---+--------+-------+---------+---------+------------+--------+---------+------+-----------+----------+---------------------+---------------------+
| 1 | 2.03 s |  138  |   132   |    6    |   68.03    | 7.89 s | 57.2 ms | 0 ns | 102.38 ms | 24.18 ms |      53.17 ms       |      61.24 ms       |
+---+--------+-------+---------+---------+------------+--------+---------+------+-----------+----------+---------------------+---------------------+
```

#### 4. 执行指定的时间

你也可以在 `run` 或者 `submit` 方法中通过 `options` 参数设置运行指定的时间，当达到指定的结束时间点时，将自动停止执行中的性能测试任务。

下面是运行 `15` 秒，5 个绝对并发的代码示例：

```java
@Test
public void submitWithDuration() throws InterruptedException {
    StalkerFuture stalkerFuture = Stalker.submit(Options.ofDurationSeconds(15, 5),
            () -> new MyTestService().slowHello());

    // 判断任务是否完成，没完成，则休眠 5 秒 直到完成为止.
    while (!stalkerFuture.isDone()) {
        Thread.sleep(5000L);
    }

    log.info("任务已完成，获取最终的执行结果信息.");
    stalkerFuture.get();
}
```

执行之后将获得如下类似结果：

```bash
+----------------------------------------------------------------------------------------------------------------------------------------------------+
|                                      duration: 15 s, concurrens: 5, warmups:5, runs: 1, printErrorLog: false                                       |
+---+--------+-------+---------+---------+------------+----------+----------+------+-----------+---------+---------------------+---------------------+
|   | Costs  | Total | Success | Failure | Throughput |   Sum    |   Avg    | Min  |    Max    | StdDev  | 95% LowerConfidence | 95% UpperConfidence |
+---+--------+-------+---------+---------+------------+----------+----------+------+-----------+---------+---------------------+---------------------+
| 1 | 15.0 s | 1241  |  1199   |   42    |   82.71    | 1.24 min | 59.95 ms | 0 ns | 103.94 ms | 22.5 ms |      58.69 ms       |       61.2 ms       |
+---+--------+-------+---------+---------+------------+----------+----------+------+-----------+---------+---------------------+---------------------+
```

#### 5. 停止正在运行中的任务

你也可以在获取到 `StalkerFuture` 对象后，停止正在运行中的任务。代码示例如下：

```java
@Test
public void submitWithStop() throws InterruptedException {
    StalkerFuture stalkerFuture = Stalker.submit(Options.of("StopTest", 20, 5, 1),
            () -> new MyTestService().slowHello());

    Thread.sleep(50L);
    List<Object> results = stalkerFuture.get();
    Assert.assertNotNull(results.get(0));
    // 调用 cancel 取消任务.
    stalkerFuture.cancel();

    stalkerFuture.get();
    log.info("任务已停止，获取最后的执行结果.");
}
```

#### 6. 主要方法

- `List<Object> run(Runnable... runnables)`: 对若干个要执行的代码做性能测量评估，并返回输出结果信息.
- `List<Object> run(Options options, Runnable... runnables)`: 通过自定义的`Options`对若干个要执行的代码做性能测量评估，并返回输出结果信息.
- `MeasureResult[] runStatis(Options options, Runnable... runnables)`: 对若干个要执行的代码做性能测量评估，并返回多个基础测量统计结果信息.
- `StalkerFuture submit(Runnable task)`: 对要执行的代码做性能测量评估，并返回异步获取结果信息的 `Future`.
- `StalkerFuture submit(Options options, Runnable task)`: 通过自定义的`Options`对若干个要执行的代码做性能测量评估，并返回异步获取结果信息的 `Future`.

### Options类

Options 表示做性能测量时的选项参数

#### 1. 主要属性如下

- `name`: 选项参数的名称
- `threads`: 正式执行的线程数，默认为 `1`。
- `concurrens`: 正式多线程下执行的并发数，默认为 `1`。
- `warmups`: 单线程下的预热次数，默认 `5`。
- `runs`: 每个线程正式执行的次数，默认 `10`。
- `printErrorLog`: 是否打印错误日志，默认 `false`。
- `outputs`: 将测量结果通过多种方式(集合)输出出来，默认为输出到控制台，可自定义实现 `MeasureOutput` 接口。
- `duration`: `v1.2.0` 版本新增，表示运行的持续时间。
- `scheduledUpdater`：`v1.2.0`版本新增，在调用 `submit` 方法时会默认开启，用于定时更新统计数据的定时更新器。

#### 2. 主要方法

以下是构造`Options`实例的若干重载方法：

- `Options of(String name)`
- `Options of(int runs)`
- `Options of(String name, int runs)`
- `Options of(int threads, int concurrens)`
- `Options of(String name, int threads, int concurrens)`
- `Options of(String name, int threads, int concurrens, int runs)`
- `Options ofDuration(long amount, TimeUnit timeUnit)`
- `Options ofDuration(long amount, TimeUnit timeUnit, int concurrens)`
- `Options ofDurationSeconds(long amount, int concurrens)`
- `Options ofDurationMinutes(long amount, int concurrens)`
- `Options ofDurationHours(long amount, int concurrens)`
- `Options ofDurationDays(long amount, int concurrens)`

其他方法：

- `boolean valid()`: 校验 Options 相关参数是否合法
- `Options named(String name)`: 设置 Options 实例的 name 属性
- `Options threads(int threads)`: 设置 Options 实例的 threads 属性
- `Options concurrens(int concurrens)`: 设置 Options 实例的 concurrens 属性
- `Options warmups(int warmups)`: 设置 Options 实例的 warmups 属性
- `Options runs(int runs)`: 设置 Options 实例的 runs 属性
- `Options printErrorLog(boolean printErrorLog)`: 设置 Options 实例的 printErrorLog 属性
- `Options outputs(MeasureOutput... measureOutputs)`: 自定义设置 Options 实例的 MeasureOutput 输出通道
- `Options duration(long amount, TimeUnit timeUnit)`: 设置任务持续运行的时间
- `Options enableScheduledUpdater()`: 默认的定时统计数据更新任务的配置选项，默认是 `10` 秒
- `Options enableScheduledUpdater(long delay, TimeUnit timeUnit)`: 设置默认的定时统计数据更新任务的配置选项
- `Options enableScheduledUpdater(long initialDelay, long delay, TimeUnit timeUnit)`: 设置默认的定时统计数据更新任务的配置选项

### StalkerFuture 类

- `void run()`: 执行可运行的方法，通常你不需要再去手动执行了。
- `boolean cancel()`: 取消正在运行中的任务.
- `boolean cancel(boolean mayInterruptIfRunning)` 取消正在运行中的任务.
- `boolean isCancelled()`: 是否已经取消了执行中的性能测试任务。
- `boolean isDone()`: 是否已经执行完成.
- `boolean isDoneSuccessfully()`: 是否是正常执行完成的.
- `List<Object> get()`: 实时获取任务的执行结果，该方法不会阻塞任务执行.
- `List<Object> get(long timeout, TimeUnit unit)`: 实时获取任务的执行结果，该方法不会阻塞任务执行.
- `MeasureResult getMeasureResult()`: 获取基础的测量统计结果信息.
- `long getCosts()`: 获取任务最终完成时实际所消耗的总的纳秒时间数.
- `long getTotal()`: 获取当前已经运行的总次数.
- `long getSuccess()`: 获取到当前时的运行成功的次数.
- `long getFailure()`: 获取当前运行失败的次数.
- `long getStartNanoTime()`: 获取任务开始运行时的纳秒时间戳.
- `long getEndNanoTime()`: 获取任务结束运行时的纳秒时间戳，如果任务还未结束，该值将是 `0`.

### Assert类

Assert类主要用来做断言使用。

#### 示例

```java
Assert.assertFaster(Options.of(),
        () -> new MyTestService().fastHello(),
        () -> new MyTestService().hello());
```

## 四、许可证

本 [stalker](https://github.com/blinkfox/stalker) 类库遵守 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) 许可证。

## 五、变更日志

- v1.2.0 新增了异步性能评估和大量的代码重构 (2020-06-07)
  - 新增了异步提交任务作性能评估；
  - 重构了大量代码，部分方法或类与之前的版本不兼容；
- v1.1.1 新增了吞吐量的统计指标 (2020-05-20)
  - 新增了吞吐量的统计指标；
- v1.1.0 新增了运行后可以获取返回结果的功能 (2020-05-14)
  - 新增了 `MeasureOutput` 中可输出结果的功能，且默认的 run 方法，也会返回其结果；
  - 新增了 `runStatis` 方法，可以拿到原始的统计结果数据；
- v1.0.1 修复线程创建过多时的限制问题 (2019-09-14)
  - 修复了线程池超过一定数量后的线程创建失败的问题；
- v1.0.0 里程碑正式版 (2019-02-08)
  - 完成了基准性能测试所需的基础功能；
