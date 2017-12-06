# qcrash

## 1、解压qcrash.zip

```
/home/mi/disk/tools/qcrash$ ls -l
总用量 30784
-rwxrwxr-x 1 mi mi 31505765 8月 23 15:55 crash64
drwxrwxr-x 2 mi mi 4096 8月 23 12:53 extensions
drwxrwxr-x 2 mi mi 4096 8月 23 12:53 plugin
-rwxrwxrwx 1 mi mi 1001 8月 28 11:54 qcom_crash.sh
drwxrwxr-x 2 mi mi 4096 8月 23 12:53 scripts
```


## 2、环境变量PATH里添加qcrash.zip的解压目录

``
export PATH=~/bin:/home/mi/disk/tools/qcrash:$PATH
```

## 3、进入ramdump目录

```
~/disk/logs/D5_HUNG/hang$ ls
CODERAM.BIN DDRCS0_0.BIN DDRCS1_1.BIN IPA_GSI1.BIN IPA_MBOX.BIN lastkmsg.txt OCIMEM.BIN PMIC_PON.BIN
DDRCS0_1.BIN IPA_DICT.BIN IPA_HRAM.BIN IPA_SRAM.BIN load.cmm PART_BIN.BIN PMON_HIS.BIN ramdump.cpl tz_log.txt
DATARAM.BIN DDRCS1_0.BIN IPA_DRAM.BIN IPA_IRAM.BIN IPA_UCS.BIN MSGRAM.BIN PIMEM.BIN qcom_crash.sh RST_STAT.BIN vmlinux
```

其中vmlinux是内核态的符号表，并非ramdump中的文件。

## 4、执行脚本qcom_crash.sh进入crash环境

```
$ qcom_crash.sh vmlinux load.cmm
/home/mi/disk/tools/qcrash/crash64 vmlinux DDRCS0_0.BIN@0x40000000,DDRCS0_1.BIN@0xc0000000,DDRCS1_0.BIN@0x100000000,DDRCS1_1.BIN@0x180000000
crash64 7.1.9
Copyright (C) 2002-2016 Red Hat, Inc.
Copyright (C) 2004, 2005, 2006, 2010 IBM Corporation
Copyright (C) 1999-2006 Hewlett-Packard Co
Copyright (C) 2005, 2006, 2011, 2012 Fujitsu Limited
Copyright (C) 2006, 2007 VA Linux Systems Japan K.K.
...
gcore: WARNING: page fault at 16e26000
gcore: WARNING: page fault at 16e27000
crash64>
```
## 5、crash环境下加载gcore插件

```
crash64> extend /home/mi/disk/tools/qcrash/extensions/gcore.so
/home/mi/disk/tools/qcrash/extensions/gcore.so: shared object loaded
```

## 6、找到想要dump的进程pid

```
crash64> ps |grep system_server
1651 818 4 ffffffc09b06d240 IN 8.5 3114120 621084 system_server
1734 818 4 ffffffc14ac9e9c0 IN 8.5 3114120 621084 system_server
2104 818 7 ffffffc089d74680 IN 8.5 3114120 621084 system_server
2105 818 6 ffffffc089d75240 IN 8.5 3114120 621084 system_serve
```

## 7、dump core

```
crash64> gcore f 31 1651
gcore: The specified task is a kernel thread.
gcore: The specified task is a kernel thread.
gcore: WARNING: page fault at 16a2d000
gcore: WARNING: page fault at 16a2e000
gcore: WARNING: page fault at 16a3c000
gcore: WARNING: page fault at 16a42000
gcore: WARNING: page fault at 16a65000
gcore: WARNING: page fault at 16a67000
...
gcore: WARNING: page fault at 14ba3000
gcore: WARNING: page fault at 14bb3000
gcore: WARNING: page fault at 14bc2000
gcore: WARNING: page fault at 14bc3000
gcore: WARNING: page fault at 14bca000
-- MORE -- forward: <SPACE>, <ENTER> or j backward: b or k quit: q
如遇到上述提示， 按q键。
之后耗时比较长（5~10分钟）需耐心等待，直到最后一行出现crash64，如：
gcore: WARNING: page fault at 16e0d000
gcore: WARNING: page fault at 16e13000
gcore: WARNING: page fault at 16e26000
gcore: WARNING: page fault at 16e27000
crash64>
这时，当前目录下会有生成的core文件（实际上，敲gcore命令后这个文件就已经生成，等待过程中这个文件慢慢会变大）。
crash64> ls
CODERAM.BIN DATARAM.BIN DDRCS1_0.BIN IPA_DRAM.BIN IPA_IRAM.BIN IPA_UCS.BIN MSGRAM.BIN PIMEM.BIN qcom_crash.sh RST_STAT.BIN vmlinux
core.1651.system_server DDRCS0_0.BIN DDRCS1_1.BIN IPA_GSI1.BIN IPA_MBOX.BIN lastkmsg.txt OCIMEM.BIN PMIC_PON.BIN qcrash symbols
DDRCS0_1.BIN IPA_DICT.BIN IPA_HRAM.BIN IPA_SRAM.BIN load.cmm PART_BIN.BIN PMON_HIS.BIN ramdump.cpl tz_log.txt
```

## 8、用gdb调试core文件