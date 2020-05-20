# stalker

[![HitCount](http://hits.dwyl.io/blinkfox/stalker.svg)](http://hits.dwyl.io/blinkfox/stalker) [![Build Status](https://secure.travis-ci.org/blinkfox/stalker.svg)](https://travis-ci.org/blinkfox/stalker) [![GitHub license](https://img.shields.io/github/license/blinkfox/stalker.svg)](https://github.com/blinkfox/stalker/blob/master/LICENSE) [![codecov](https://codecov.io/gh/blinkfox/stalker/branch/master/graph/badge.svg)](https://codecov.io/gh/blinkfox/stalker) ![Java Version](https://img.shields.io/badge/Java-%3E%3D%208-blue.svg)

[中文介绍](https://github.com/blinkfox/stalker/blob/master/README_CN.md)

> A small library for performance evaluation of Java code.

## Features

- Lightweight (jar package is only '28kb')
- API are simple and easy to use. 
- Easy integration or expansion

## Maven integration

```xml
<dependency>
    <groupId>com.blinkfox</groupId>
    <artifactId>stalker</artifactId>
    <version>1.1.1</version>
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
@Slf4j
public class MyTestService {

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
+------------------------------------------------------------------------------------------------------------------------------------------------------+
|                                         threads: 1, concurrens: 1, warmups:5, runs: 10, printErrorLog: false                                         |
+---+----------+-------+---------+---------+------------+----------+---------+---------+---------+---------+---------------------+---------------------+
|   |  Costs   | Total | Success | Failure | Throughput |   Sum    |   Avg   |   Min   |   Max   | StdDev  | 95% LowerConfidence | 95% UpperConfidence |
+---+----------+-------+---------+---------+------------+----------+---------+---------+---------+---------+---------------------+---------------------+
| 1 | 40.52 ms |  10   |   10    |    0    |   246.76   | 40.43 ms | 4.04 ms | 2.24 ms | 7.74 ms | 1.56 ms |       3.07 ms       |       5.01 ms       |
+---+----------+-------+---------+---------+------------+----------+---------+---------+---------+---------+---------------------+---------------------+
```

You can also get statistical results:

```java
// Get running statistics.
Measurement[] measurements = mStalker.runStatis(() -> new MyTestService().hello());

// Get the MeasureOutput result specified in the running Options. 
// The default is the string content of the ASCII table output in the console log. 
// It can be multiple results, so return the collection.
List<Object> measurements = mStalker.run(() -> new MyTestService().hello());
```

#### 2. More complete example

The following code indicates that the two methods `hello()` and `fastHello()` will preheat `10` times, in the `1000` threads `200` concurrent, each time executing `5` times:

```java
Stalker.run(Options.of(1000, 200).warmups(10).runs(5),
        () -> new MyTestService().hello(),
        () -> new MyTestService().fastHello());
```

The above results will default to the console output:

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

Explanation of results:

- `Costs`: Total time spent on actual official runs
- `Total`: total number of official runs
- `Success`: number of successful runs
- `Failure`: number of failed runs
- `Throughput`: Throughput of official runs
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

## Changelog

- v1.1.1 New statistical index of throughput (2020-05-20)
  - New statistical index of throughput；
- v1.1.0 fixes the limitation when creating too many threads (2020-05-14)
  - Added the ability to output results in `MeasureOutput`, and the default run method will also return its results;
  - Added `runStatis` method, you can get the original statistical result data;
- v1.0.1 Fix the limitation problem when too many threads created (2019-09-14)
  - Fixed where thread creation failed after a certain number of thread pools failed;
- v1.0.0 Milestone version (2019-02-08)
  - Completed the basic functionality required for benchmark performance testing;
