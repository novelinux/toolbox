GPIO
========================================

简介
----------------------------------------

相信大家在进行嵌入式linux设备开发时，会多或少都会涉及到对gpio的控制。
以前通用的方式是在内核中增加一个gpio驱动，然后再在上端条用它从而实现对gpio的控制。
今天我给大家介绍一个简单的方式（不用写代码）用以控制gpio。该方式主要基于内核提供的gpio控制接口文件。
也就是通过读写/sys/class/gpio目录下的文件来控制对应的gpio接口。

使用该方法去控制某个gpio接口主要分为三个步骤：
* 1.导出相应的gpio接口;
* 2.设置相应gpio接口的方向;(in or out)
* 3.设置相应gpio的值。

导出GPIO
----------------------------------------

```
$ echo $gpio_num > /sys/class/gpio/export

```

eg: echo 1 > /sys/class/gpio/export
执行完以上命令后，如果该gpio接口存在且未被占用则会出现如下目录：/sys/class/gpio/gpio1

设置方向
----------------------------------------

gpio的方向分为两类：in和out

in:表示该gpio用于输入。（如该gpio连接一个按钮）
out:表示该gpio用于输出。（如该gpio连接一个led灯）

* 指定为in模式的命令：

```
$ echo in > /sys/class/gpio/gpio1/direction
```

* 指定为out模式的命令如下：

```
$ echo out > /sys/class/gpio/gpio1/direction
$ echo low > /sys/class/gpio/gpio1/direction
$ echo high > /sys/class/gpio/gpio1/direction
```
设置高低
----------------------------------------

只用当方向为out模式时才能指定gpio接口的电压的高低。这个很容易理解，
因为如果是in模式的话，它的电平高低取决于所连接外设的电平高低，我们只能读取它的值，不能更改它的值。

```
$ echo 1 > /sys/class/gpio/gpio1/value //指定gpio1为高电平。
$ echo 0 > /sys/class/gpio/gpio1/value //指定gpio1为低电平。
```

获取当前值
----------------------------------------

```
$ cat /sys/class/gpio/gpio1/value //用以获取gpio1的当前值。
$ cat /sys/kernel/debug/gpio //用以获取系统中所有正在使用的gpio的值。
```
