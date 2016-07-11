systrace
========================================

systrace允许你监视和跟踪Android系统的行为(trace)。它会告诉你系统都在哪些工作上
花费时间、CPU周期都用在哪里，甚至你可以看到每个线程、进程在指定时间内都在干嘛。
它同时还会突出观测到的问题，从垃圾回收到渲染内容都可能是问题对象，甚至提供给你建议的解决方案。
Systrace分为三个部分：

* 1.内核部分：
     systrace以Linux Kernel的ftrace为基础。
* 2.数据采集：
    Android定义的Trace类，提供了把信息输出到ftrace的功能。
    同时，Android还有一个atrace程序，它可以从ftrace中读取统计信息然后交给数据分析工具来处理。
* 3.数据生成：
    SDK提供了systrace.py用来收集ftrace统计数据并生成一个结果网页文件供用户查看。

```
$ python ~/big/tool/android-sdk-linux/platform-tools/systrace/systrace.py --time=300 -o my.html bionic
```