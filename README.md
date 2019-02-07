# stalker

[![HitCount](http://hits.dwyl.io/blinkfox/stalker.svg)](http://hits.dwyl.io/blinkfox/stalker) [![Build Status](https://secure.travis-ci.org/blinkfox/stalker.svg)](https://travis-ci.org/blinkfox/stalker) [![GitHub license](https://img.shields.io/github/license/blinkfox/stalker.svg)](https://github.com/blinkfox/stalker/blob/master/LICENSE) [![codecov](https://codecov.io/gh/blinkfox/stalker/branch/master/graph/badge.svg)](https://codecov.io/gh/blinkfox/stalker) ![Java Version](https://img.shields.io/badge/Java-%3E%3D%208-blue.svg)

[中文介绍](https://github.com/blinkfox/stalker/blob/master/README_CN.md)

> A small library for performance evaluation of Java code.

## Features

- Lightweight (jar package is only '26kb')
- API are simple and easy to use. 
- Easy integration or expansion

## Maven integration

```xml
<dependency>
    <groupId>com.blinkfox</groupId>
    <artifactId>stalker</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## API introduction and use

### Prepare

Before doing performance testing on Java methods, prepare the test service class and methods to be tested:

```java
/**
 * A class used to measure (test only) the time-consuming execution of methods in this class.
 *
 * @author blinkfox on 2019-02-03.
 */
public class MyTestService {

    private static final Logger log = LoggerFactory.getLogger(MyTestService.class);

    /**
     * Test Method 1, the simulation of the business code takes 2~5 ms, 
     * and there will be a 1% chance of executing the exception.
     */
    public void hello() {
        if (new Random().nextInt(100) == 5) {
            throw new MyServiceException("My Service Exception.");
        }

        this.sleep(2L + new Random().nextInt(3));
    }

    /**
     * Test Method 2, the simulation business code runs for about 2 ms.
     */
    public void fastHello() {
        this.sleep(2L);
    }

    /**
     * When this thread calls this method, 
     * it sleeps for the specified time and is used to simulate the time-consuming business.
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

### Stalker

#### 1. Simplest example

The following code will warm up to `5` times, then formally execute `10` times in a single thread, and then calculate the statistics of the running results and output them:

```java
public static void main(String[] args) {
    Stalker.run(() -> new MyTestService().hello());
}
```

The above results will default to the console output:

```bash
+-----------------------------------------------------------------------------------------------------------------------------------------+
|                                  threads: 1, concurrens: 1, warmups:5, runs: 10, printErrorLog: false                                   |
+---+----------+-------+---------+---------+----------+---------+---------+---------+---------+---------------------+---------------------+
|   |  Costs   | Total | Success | Failure |   Sum    |   Avg   |   Min   |   Max   | StdDev  | 95% LowerConfidence | 95% UpperConfidence |
+---+----------+-------+---------+---------+----------+---------+---------+---------+---------+---------------------+---------------------+
| 1 | 35.33 ms |  10   |   10    |    0    | 35.29 ms | 3.53 ms | 2.56 ms | 4.81 ms | 0.85 ms |       3.0 ms        |       4.06 ms       |
+---+----------+-------+---------+---------+----------+---------+---------+---------+---------+---------------------+---------------------+
```

#### 2. More complete example

The following code indicates that the two methods `hello()` and `fastHello()` will preheat `1000` times, in the `1000` threads `200` concurrent, each time executing `10` times:

```java
Stalker.run(Options.of(1000, 200).warmups(1000).runs(10),
        () -> new MyTestService().hello(),
        () -> new MyTestService().fastHello());
```

The above results will default to the console output:

```bash
+------------------------------------------------------------------------------------------------------------------------------------------+
|                               threads: 1000, concurrens: 200, warmups:1000, runs: 10, printErrorLog: false                               |
+---+-----------+-------+---------+---------+---------+---------+---------+----------+---------+---------------------+---------------------+
|   |   Costs   | Total | Success | Failure |   Sum   |   Avg   |   Min   |   Max    | StdDev  | 95% LowerConfidence | 95% UpperConfidence |
+---+-----------+-------+---------+---------+---------+---------+---------+----------+---------+---------------------+---------------------+
| 1 | 454.33 ms | 10000 |  9900   |   100   | 36.79 s | 3.72 ms | 2.01 ms | 11.89 ms | 1.31 ms |       3.69 ms       |       3.74 ms       |
| 2 | 159.94 ms | 10000 |  10000  |    0    | 21.72 s | 2.17 ms | 2.01 ms | 3.24 ms  | 0.15 ms |       2.17 ms       |       2.18 ms       |
+---+-----------+-------+---------+---------+---------+---------+---------+----------+---------+---------------------+---------------------+
```

Explanation of results:

- `Costs`: Total time spent on actual official runs
- `Total`: total number of official runs
- `Success`: number of successful runs
- `Failure`: number of failed runs
- `Sum`: the value after each time-consuming summation of the run
- `Avg`: arithmetic mean of all running time-consuming results
- `Min`: the minimum of all running time-consuming results
- `Max`: the maximum of all running time-consuming results
- `StdDev`: standard deviation of all running time-consuming results
- `95% LowerConfidence`: minimum boundary value for 95% confidence interval
- `95% LowerConfidence`: maximum boundary value for 95% confidence interval

#### 3. Main methods

- `void run(Runnable... runnables)`: Perform performance measurement evaluation on several code to be executed.
- `void run(Options options, Runnable... runnables)`: Performance measurement evaluation of several code to be executed by custom `Options`.

### Options

Options indicates option parameters when making performance measurements.

#### The main properties are as follows

- `name`: name.
- `threads`: The number of threads that are executed. The default is 1.
- `concurrens`: The number of concurrent executions under formal multithreading. The default is 1.
- `warmups`: The number of warm ups under single thread, the default is 5.
- `runs`: The number of times each thread is executed, the default is 10.
- `printErrorLog`: Whether to print the error log, the default is false.
- `outputs`: The measurement results are output in a variety of ways (collections). The default is output to the console, which can be customized to implement the `MeasureOutput` interface.

#### Main methods

Here are a few overloaded methods for constructing an `Options` instance:

- `Options of(String name)`
- `Options of(int runs)`
- `Options of(String name, int runs)`
- `Options of(int threads, int concurrens)`
- `Options of(String name, int threads, int concurrens)`
- `Options of(String name, int threads, int concurrens, int runs)`

Other methods:

- `boolean valid()`: Check whether the parameters related to Options are legal.
- `Options named(String name)`: Set the name property of the Options instance
- `Options threads(int threads)`: Set the threads property of the Options instance
- `Options concurrens(int concurrens)`: Set the concurrens property of the Options instance
- `Options warmups(int warmups)`: Set the warmups property of the Options instance
- `Options runs(int runs)`: Set the runs property of the Options instance
- `Options printErrorLog(boolean printErrorLog)`: Set the printErrorLog property of the Options instance
- `Options outputs(MeasureOutput... measureOutputs)`: Set the outputs property of the Options instance

### Assert

The Assert is mainly used for assertion use.

#### Demo

```java
Assert.assertFaster(Options.of(),
        () -> new MyTestService().fastHello(),
        () -> new MyTestService().hello());
```

## License

This [stalker](https://github.com/blinkfox/stalker) library is open sourced under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).