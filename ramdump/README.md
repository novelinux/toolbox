# ramdump:

crash_ARM64 vmlinux DDRCS0_0.BIN@0x0000000080000000,DDRCS1_0.BIN@0x0000000100000000,DDRCS1_1.BIN@0x0000000180000000 --kaslr 0x199e600000

##【第一步】

首先要做debugpolicy，如果机器没有开secure boot可以不用这一步直接跳到第二步。
具体查看方法，##64663## 版本信息里面SecureBoot 那里 是yes or no
或者：
```
$ fastboot oem device-info
(bootloader)     Device unlocked: true
```

如果是打开了secure boot，则到源码的根目录执行
python vendor/xiaomi/securebootsigner/Qualcomm/tools/debugpolicy.py
然后会自动重启

##【第二步】

重启之后需要有root
```
adb root
adb shell "echo 1 > /sys/module/msm_poweroff/parameters/download_mode"
```
如何确认是否打开 download mode：
```
adb shell "cat /sys/module/msm_poweroff/parameters/download_mode"
```

返回值是1 就可以了。
如果重启手机了，需要重新执行第二步

##【第三步】

如果是定屏问题，则需要手动触发RAMDUMP：
```
adb shell "echo c > /proc/sysrq-trigger"
```
如果是panic，复现之后如果是底层重启，手机会进入黑屏状态，连上linux lsusb 查看 会有一个 900e 或者9091的设备
此时用高通qpst configuration 抓dump 就行了。
装好qpst 打开 qpst configuration， 手机连接电脑，如果是900e的话，会自动抓 dump的。
如果是虚拟机，注意如下事项：

* 1、需要重启时点击F8关闭设备签名校验
* 2、链接手机后勾选【设备】->【USB】→【Qualcomm  CDMA Technologies MSM  QUSB_BULK】

##【第四步】
用crash工具加载对应的vmlinux和dump出来的image来分析问题。
一般dump目录如下：

```
$ ls -l
-rw-rw-r-- 1 mi mi     163840  7月  6 11:32 CODERAM.BIN
-rw-rw-r-- 1 mi mi      81920  7月  6 11:32 DATARAM.BIN
-rw-rw-r-- 1 mi mi 2147483648  7月  6 11:33 DDRCS0_0.BIN
-rw-rw-r-- 1 mi mi 2147483648  7月  6 11:35 DDRCS1_0.BIN
-rw-rw-r-- 1 mi mi 2147483648  7月  6 11:36 DDRCS1_1.BIN
-rw-rw-r-- 1 mi mi       8192  7月  6 11:32 DDR_DATA.BIN
-rw-rw-r-- 1 mi mi       2223  7月  6 11:36 dump_info.txt
-rw-rw-r-- 1 mi mi     196511  7月 12 17:35 fx
-rw-rw-r-- 1 mi mi       4096  7月  6 11:32 IPA_DICT.BIN
-rw-rw-r-- 1 mi mi      16128  7月  6 11:32 IPA_DRAM.BIN
-rw-rw-r-- 1 mi mi      12032  7月  6 11:32 IPA_HRAM.BIN
-rw-rw-r-- 1 mi mi      16384  7月  6 11:32 IPA_IRAM.BIN
-rw-rw-r-- 1 mi mi        256  7月  6 11:32 IPA_MBOX.BIN
-rw-rw-r-- 1 mi mi       8192  7月  6 11:32 IPA_SRAM.BIN
-rw-rw-r-- 1 mi mi    2097140  7月  6 11:32 lastkmsg.txt
-rw-rw-r-- 1 mi mi       1644  7月  6 11:36 load.cmm
-rw-rw-r-- 1 mi mi      28672  7月  6 11:32 MSGRAM.BIN
-rw-rw-r-- 1 mi mi     262144  7月  6 11:32 OCIMEM.BIN
-rw-rw-r-- 1 mi mi          8  7月  6 11:32 PART_BIN.BIN
-rw-rw-r-- 1 mi mi    2097152  7月  6 11:32 PIMEM.BIN
-rw-rw-r-- 1 mi mi          8  7月  6 11:32 PMIC_PON.BIN
-rw-rw-r-- 1 mi mi        172  7月  6 11:32 PMON_HIS.BIN
-rw-r--r-- 1 mi mi       1002  7月 11 17:34 q
-rw-rw-r-- 1 mi mi          4  7月  6 11:32 RST_STAT.BIN
-rw-rw-r-- 1 mi mi      11192  7月  6 11:32 tz_log.txt
其中需要加载到crash工具里的文件有：
-rw-rw-r-- 1 mi mi 2147483648  7月  6 11:33 DDRCS0_0.BIN
-rw-rw-r-- 1 mi mi 2147483648  7月  6 11:35 DDRCS1_0.BIN
-rw-rw-r-- 1 mi mi 2147483648  7月  6 11:36 DDRCS1_1.BIN
```

加载命令如下：

```
$ ./crash_ARM64 vmlinux DDRCS0_0.BIN@0x0000000080000000,DDRCS1_0.BIN@0x0000000100000000,DDRCS1_1.BIN@0x0000000180000000 --kaslr 0x199e60000
```

其中每个bin的加载地址0x0000000080000000、0x0000000100000000、0x0000000180000000三个值可以从dump_info.txt文件中获取：

```
$ cat dump_info.txt |grep DDRCS
   1 0x0000000080000000 2147483648               (null)   DDR CS0 part0 Memo DDRCS0_0.BIN
   1 0x0000000100000000 2147483648               (null)   DDR CS1 part0 Memo DDRCS1_0.BIN
   1 0x0000000180000000 2147483648               (null)   DDR CS1 part1 Memo DDRCS1_1.BIN
```
kaslr后面的数值表示的是kernel的随机加载地址，可以通过如下方式获取：

```
$ hexdump  -e '16/4 "%08x " "\n"' -s 0x03f6d4 -n 8 OCIMEM.BIN
9e600000 00000019
```

或

```
$ strings  DDRCS0_0.BIN | grep "Kernel Offset"
0Kernel Offset: 0x%llx from 0x%lx
0Kernel Offset: disabled
Kernel Offset: 0x199e600000 from 0xffffff8008000000
[192145.589105] Kernel Offset: 0x199e600000 from 0xffffff8008000000
```

vmlinux可以在手机版本对应的symbols里去获取，具体路径如下：
out/target/product/jason/obj/KERNEL_OBJ/vmlinux

注：某些机器直接会dump到手机中的分区/dev/block/bootdevice/by-name/ramdump，
但这个分区不是每台机器都有,内部工程机某一个批次之前的都会有，量产的都不会有；
有该功能的手机界面会进入橘黄色的抓取ramdump的界面：
1.如果有提示save 到ramdump分区，  这种情况等自动重启，然后连上电脑拿文件出来即可
2.如果提示没找到ramdump分区，需要链接电脑用qpst抓，这时候需要在黄屏界面，连上电脑 使用qpst抓ramdump
