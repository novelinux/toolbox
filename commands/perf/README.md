Perf
========================================

Perf Event 是一款随 Linux 内核代码一同发布和维护的性能诊断工具，
由内核社区维护和发展。Perf 不仅可以用于应用程序的性能统计分析，
也可以应用于内核代码的性能统计和分析。得益于其优秀的体系结构设计，
越来越多的新功能被加入 Perf，使其已经成为一个多功能的性能统计工具集 。

Perf 是用来进行软件性能分析的工具。通过它，应用程序可以利用 PMU，
tracepoint 和内核中的特殊计数器来进行性能统计。它不但可以分析
指定应用程序的性能问题 (per thread)，也可以用来分析内核的性能问题，
当然也可以同时分析应用代码和内核，从而全面理解应用程序中的性能瓶颈。
最初的时候，它叫做 Performance counter，在 2.6.31 中第一次亮相。
此后他成为内核开发最为活跃的一个领域。在 2.6.32 中它正式改名为
Performance Event，因为 perf 已不再仅仅作为 PMU 的抽象，而是
能够处理所有的性能相关的事件。使用 perf，您可以分析程序运行期间
发生的硬件事件，比如 instructions retired ，processor clock cycles 等；
您也可以分析软件事件，比如 Page Fault 和进程切换。这使得 Perf 拥有了众多的
性能分析能力，举例来说，使用 Perf 可以计算每个时钟周期内的指令数，称为 IPC，
IPC 偏低表明代码没有很好地利用 CPU。Perf 还可以对程序进行函数级别的采样，
从而了解程序的性能瓶颈究竟在哪里等等。Perf 还可以替代 strace，可以添加
动态内核 probe 点，还可以做 benchmark 衡量调度器的好坏...

背景知识
----------------------------------------

有些背景知识是分析性能问题时需要了解的。比如硬件 cache；
再比如操作系统内核。应用程序的行为细节往往是和这些东西互相牵扯的，
这些底层的东西会以意想不到的方式影响应用程序的性能，比如某些程序
无法充分利用 cache，从而导致性能下降。比如不必要地调用过多的系统调用，
造成频繁的内核/用户切换。等等。方方面面，这里只是为本文的后续内容做一些
铺垫，关于调优还有很多东西，我所不知道的比知道的要多的多。

### PMU 简介

当算法已经优化，代码不断精简，人们调到最后，便需要斤斤计较了。
cache 啊，流水线啊一类平时不大注意的东西也必须精打细算了。

### 硬件特性之 cache

内存读写是很快的，但还是无法和处理器的指令执行速度相比。
为了从内存中读取指令和数据，处理器需要等待，用处理器的时间来衡量，
这种等待非常漫长。Cache 是一种 SRAM，它的读写速率非常快，能和
处理器处理速度相匹配。因此将常用的数据保存在 cache 中，处理器便
无须等待，从而提高性能。Cache 的尺寸一般都很小，充分利用 cache
是软件调优非常重要的部分。

### 硬件特性之流水线，超标量体系结构，乱序执行

提高性能最有效的方式之一就是并行。处理器在硬件设计时也尽可能地并行，
比如流水线，超标量体系结构以及乱序执行。处理器处理一条指令需要分多个步骤完成，
比如先取指令，然后完成运算，最后将计算结果输出到总线上。在处理器内部，
这可以看作一个三级流水线，一个时钟周期内可以同时处理三条指令，分别被流水线的不同部分处理。
超标量（superscalar）指一个时钟周期发射多条指令的流水线机器架构，
比如 Intel 的 Pentium 处理器，内部有两个执行单元，在一个时钟周期内允许执行两条指令。
此外，在处理器内部，不同指令所需要的处理步骤和时钟周期是不同的，
如果严格按照程序的执行顺序执行，那么就无法充分利用处理器的流水线。
因此指令有可能被乱序执行。上述三种并行技术对所执行的指令有一个基本要求，
即相邻的指令相互没有依赖关系。假如某条指令需要依赖前面一条指令的执行结果数据，
那么 pipeline 便失去作用，因为第二条指令必须等待第一条指令完成。
因此好的软件必须尽量避免这种代码的生成。

### 硬件特性之分支预测

分支指令对软件性能有比较大的影响。尤其是当处理器采用流水线设计之后，
假设流水线有三级，当前进入流水的第一条指令为分支指令。假设处理器顺序
读取指令，那么如果分支的结果是跳转到其他指令，那么被处理器流水线预取
的后续两条指令都将被放弃，从而影响性能。为此，很多处理器都提供了分支
预测功能，根据同一条指令的历史执行记录进行预测，读取最可能的下一条指令，
而并非顺序读取指令。
分支预测对软件结构有一些要求，对于重复性的分支指令序列，分支预测硬件能
得到较好的预测结果，而对于类似 switch case 一类的程序结构，则往往无法
得到理想的预测结果。上面介绍的几种处理器特性对软件的性能有很大的影响，
然而依赖时钟进行定期采样的 profiler 模式无法揭示程序对这些处理器硬件
特性的使用情况。处理器厂商针对这种情况，在硬件中加入了 PMU 单元，
即 performance monitor unit。PMU 允许软件针对某种硬件事件设置 counter，
此后处理器便开始统计该事件的发生次数，当发生的次数超过 counter 内设置的值后，
便产生中断。比如 cache miss 达到某个值后，PMU 便能产生相应的中断。
捕获这些中断，便可以考察程序对这些硬件特性的利用效率了。

### Tracepoints

Tracepoint 是散落在内核源代码中的一些 hook，一旦使能，它们便可以在特定的
代码被运行到时被触发，这一特性可以被各种 trace/debug 工具所使用。
Perf 就是该特性的用户之一。假如您想知道在应用程序运行期间，内核内存管理模块的行为，
便可以利用潜伏在 slab 分配器中的 tracepoint。当内核运行到这些 tracepoint 时，便会通知 perf。
Perf 将 tracepoint 产生的事件记录下来，生成报告，通过分析这些报告，
调优人员便可以了解程序运行时期内核的种种细节，对性能症状作出更准确的诊断。

perf list
----------------------------------------

```
$ ./perf list

List of pre-defined events (to be used in -e):
  cpu-cycles OR cycles                               [Hardware event]
  instructions                                       [Hardware event]
  cache-references                                   [Hardware event]
  cache-misses                                       [Hardware event]
  branch-misses                                      [Hardware event]
  ref-cycles                                         [Hardware event]

  cpu-clock                                          [Software event]
  task-clock                                         [Software event]
  page-faults OR faults                              [Software event]
  context-switches OR cs                             [Software event]
  cpu-migrations OR migrations                       [Software event]
  minor-faults                                       [Software event]
  major-faults                                       [Software event]
  alignment-faults                                   [Software event]
  emulation-faults                                   [Software event]

  L1-dcache-loads                                    [Hardware cache event]
  L1-dcache-load-misses                              [Hardware cache event]
  L1-dcache-stores                                   [Hardware cache event]
  L1-dcache-store-misses                             [Hardware cache event]
  branch-loads                                       [Hardware cache event]
  branch-load-misses                                 [Hardware cache event]

  rNNN                                               [Raw hardware event descriptor]
  cpu/t1=v1[,t2=v2,t3 ...]/modifier                  [Raw hardware event descriptor]
   (see 'man perf-list' on how to encode it)

  mem:<addr>[:access]                                [Hardware breakpoint]

  rmnet_data:rmnet_egress_handler                    [Tracepoint event]
  rmnet_data:rmnet_ingress_handler                   [Tracepoint event]
  rmnet_data:rmnet_vnd_start_xmit                    [Tracepoint event]
  rmnet_data:__rmnet_deliver_skb                     [Tracepoint event]
  rmnet_data:rmnet_fc_qmi                            [Tracepoint event]
```

其中通过perf list命令运行在不同的系统会列出不同的结果，
在 2.6.35 版本的内核中，该列表已经相当的长，但无论有多少，我们可以将它们划分为三类：

* Hardware Event:
是由 PMU 硬件产生的事件，比如 cache 命中，当需要了解程序对硬件特性的使用情况时，便需要对这些事件进行采样；

* Software Event:
是内核软件产生的事件，比如进程切换，tick 数等 ;

* Tracepoint event:
是内核中的静态 tracepoint 所触发的事件，这些 tracepoint
用来判断程序运行期间内核的行为细节，比如 slab 分配器的分配次数等。

在操作系统运行过程中，关于系统调用的调度优先级别，从高到低是这样的：

```
硬中断->软中断->实时进程->内核进程->用户进程
```

Samples
----------------------------------------

```
#include <stdio.h>
#include <stdlib.h>

static void longa(void)
{
    int i, j;

    for (i = 0; i < 1000000; ++i)
        j = i;
}

static void foo2(void)
{
    int i;
    for (i = 0; i < 100; ++i) {
        longa();
    }
}

static void foo1(void)
{
    int i;
    for (i = 0; i < 100; ++i) {
        longa();
    }
}

int main(int argc, char *argv[])
{
    foo1();
    foo2();
}
```

### build

```
$ gcc -g test.c
```

### output

```
$ perf stat ./a.out

 Performance counter stats for './a.out':

        378.491308      task-clock (msec)         #    0.999 CPUs utilized
                48      context-switches          #    0.127 K/sec
                 0      cpu-migrations            #    0.000 K/sec
               117      page-faults               #    0.309 K/sec
     1,410,308,862      cycles                    #    3.726 GHz                     [83.09%]
     1,003,839,179      stalled-cycles-frontend   #   71.18% frontend cycles idle    [83.10%]
        70,118,368      stalled-cycles-backend    #    4.97% backend  cycles idle    [67.02%]
       992,841,890      instructions              #    0.70  insns per cycle
                                                  #    1.01  stalled cycles per insn [84.04%]
       199,776,909      branches                  #  527.824 M/sec                   [84.16%]
             3,950      branch-misses             #    0.00% of all branches         [83.09%]

       0.378694982 seconds time elapsed
```

对t1进行调优应该要找到热点 ( 即最耗时的代码片段 )，再看看是否能够提高热点代码的效率。
缺省情况下，除了 task-clock-msecs 之外，perf stat 还给出了其他几个最常用的统计信息：

* Task-clock-msecs：
CPU 利用率，该值高，说明程序的多数时间花费在 CPU 计算上而非 IO。

* Context-switches：
进程切换次数，记录了程序运行过程中发生了多少次进程切换，频繁的进程切换是应该避免的。

* Cache-misses：
程序运行过程中总体的 cache 利用情况，如果该值过高，说明程序的 cache 利用不好

* CPU-migrations：
表示进程 t1 运行过程中发生了多少次 CPU 迁移，即被调度器从一个 CPU 转移到另外一个 CPU 上运行。

* Cycles：
处理器时钟，一条机器指令可能需要多个 cycles，

* Instructions: 机器指令数目。

* IPC：
是 Instructions/Cycles 的比值，该值越大越好，说明程序充分利用了处理器的特性。

* Cache-references: cache 命中的次数

* Cache-misses: cache 失效的次数。
通过指定 -e 选项，您可以改变 perf stat 的缺省事件 ( 关于事件，可以通过 perf list 来查看 )。
假如您已经有很多的调优经验，可能会使用 -e 选项来查看您所感兴趣的特殊的事件。

### perf record & perf report

```
$ perf record -g –e cpu-clock ./a.out
$ perf report --symfs=out/target/product/hydrogen/symbols/system/bin --sort dso,symbol
```

使用 tracepoint
----------------------------------------

当 perf 根据 tick 时间点进行采样后，人们便能够得到内核代码中的 hot spot。
那什么时候需要使用 tracepoint 来采样呢？我想人们使用 tracepoint 的基本需求
是对内核的运行时行为的关心，如前所述，有些内核开发人员需要专注于特定的子系统，
比如内存管理模块。这便需要统计相关内核函数的运行情况。
另外，内核行为对应用程序性能的影响也是不容忽视的：

```
/data/local/tmp/perf stat -e raw_syscalls:sys_enter ls
...
 Performance counter stats for 'ls':

               533 raw_syscalls:sys_enter

       0.017243333 seconds time elapsed
```

perf bench
----------------------------------------

除了调度器之外，很多时候人们都需要衡量自己的工作对系统性能的影响。
benchmark 是衡量性能的标准方法，对于同一个目标，如果能够有一个
大家都承认的 benchmark，将非常有助于”提高内核性能”这项工作。
目前，就我所知，perf bench 提供了 3 个 benchmark:

### Sched message

```
# ./perf bench sched messaging
# Running sched/messaging benchmark...
# 20 sender and receiver processes per group
# 10 groups == 400 processes run

     Total time: 0.677 [sec]
```

是从经典的测试程序 hackbench 移植而来，用来衡量调度器的性能，
overhead 以及可扩展性。该 benchmark 启动 N 个 reader/sender
进程或线程对，通过 IPC(socket 或者 pipe) 进行并发的读写。一般
人们将 N 不断加大来衡量调度器的可扩展性。Sched message 的用法及用途和 hackbench 一样。

### Sched Pipe

```
# ./perf bench sched pipe
```

Extecuted 1000000 pipe operations between two tasks Total time:
20.888 [sec] 20.888017 usecs/op 47874 ops/secsched pipe
从 Ingo Molnar 的 pipe-test-1m.c 移植而来。当初 Ingo 的原始程序是为了
测试不同的调度器的性能和公平性的。其工作原理很简单，两个进程互相通过 pipe
拼命地发 1000000 个整数，进程 A 发给 B，同时 B 发给 A。。。因为 A 和 B
互相依赖，因此假如调度器不公平，对 A 比 B 好，那么 A 和 B 整体所需要的时间就会更长。

### Mem memcpy

```
# ./perf bench mem memcpy
# Running mem/memcpy benchmark...
# Copying 1MB Bytes ...

       2.104661 GB/Sec
       6.260016 GB/Sec (with prefault)
```

这个是 perf bench 的作者 Hitoshi Mitake 自己写的一个执行 memcpy 的 benchmark。
该测试衡量一个拷贝 1M 数据的 memcpy() 函数所花费的时间。我尚不明白该 benchmark
的使用场景。。。或许是一个例子，告诉人们如何利用 perf bench 框架开发更多的 benchmark吧。
这三个 benchmark 给我们展示了一个可能的未来：不同语言，不同肤色，来自不同背景的人们
将来会采用同样的 benchmark，只要有一份 Linux 内核代码即可。

perf probe
----------------------------------------

tracepoint 是静态检查点，意思是一旦它在哪里，便一直在那里了，
您想让它移动一步也是不可能的。但目前 tracepoint 有多少呢？
所以能够动态地在想查看的地方插入动态监测点的意义是不言而喻的。
Perf 并不是第一个提供这个功能的软件，systemTap 早就实现了。
但假若您不选择 RedHat 的发行版的话，安装 systemTap 并不是件轻松愉快的事情。
perf 是内核代码包的一部分，所以使用和维护都非常方便。
