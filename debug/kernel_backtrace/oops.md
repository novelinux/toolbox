# Count adress

oops错误日志信息：

```
Unable to handle kernel NULL pointer dereference at virtual address 00000020
pgd = 80004000
[00000020] *pgd=00000000
Internal error: Oops: 17 [#1] PREEMPT
last sysfs file: /sys/devices/platform/mxsdhci.2/mmc_host/mmc0/mmc0:0001/boot_bus_config
CPU: 0 Not tainted (2.6.35.3 #10)
PC is at fsg_main_thread+0x144/0x730
LR is at schedule+0x2ac/0x328
pc : [<8025b0b4>] lr : [<802ac778>] psr: 60000013
sp : cfcd3f88 ip : cfcd3f38 fp : cfcd3fc4
r10: cfcd2000 r9 : cf081640 r8 : 00000200
r7 : 80356ac8 r6 : cfcd2000 r5 : cf081678 r4 : cf081600
r3 : 00000000 r2 : 00000000 r1 : 00000000 r0 : 00000000
Flags: nZCv IRQs on FIQs on Mode SVC_32 ISA ARM Segment kernel
Control: 10c5387d Table: bd2a0019 DAC: 00000017
Process file-storage-ga (pid: 871, stack limit = 0xcfcd22e8)
Stack: (0xcfcd3f88 to 0xcfcd4000)
3f80: cf08167c cfcd3f98 802ac648 cf0816b0 00000000 cf029ed8
3fa0: cfcd3fcc 8025af70 cf081600 00000000 00000000 00000000 cfcd3ff4 cfcd3fc8
3fc0: 8006565c 8025af7c 00000000 00000000 cfcd3fd0 cfcd3fd0 cf029ed8 800655d8
3fe0: 80051d14 00000013 00000000 cfcd3ff8 80051d14 800655e4 eda8ff35 f7efad76
Backtrace:
[<8025af70>] (fsg_main_thread+0x0/0x730) from [<8006565c>] (kthread+0x84/0x8c)
[<800655d8>] (kthread+0x0/0x8c) from [<80051d14>] (do_exit+0x0/0x65c)
r7:00000013 r6:80051d14 r5:800655d8 r4:cf029ed8
Code: e5953004 e3530001 1afffff8 e5953018 (e5932020)
---[ end trace 38aa9563884a33ec ]---
```

遇 到了空指针错误，PC指针指向fsg_main_thread+0x144 处，fsg_main_thread()函数位于driver/usb/gadgate/file_storage.c这个文件内，但是0x144的 offset是哪一行呢？由于发生这个oops的kernel缺省没有包含debug信息，所以需要重新生成一个带debug info的vmlinux，步骤如下：
运行make menuconfig之后选中，

kernel hacking->Kernel debugging->Compile the kernel with debug info
这样编译出来的vmlinux就带调试符号了。

打开编译好的kernel vmlinux所在目录的符号表文件System.map，搜索fsg_main_thread，找到所在的行，最左边的就是fsg_main_thread的地址了，即8025af70，偏移0x144，最终出错的地址是：

```
0x8025af70+0x144=0x8025b0b4
```

此时用编译kernel的toolchain中的gdb工具打开带调式符号的vmlinux，

toolchain/arm-eabi-4.4.3/bin/arm-eabi-gdb kernel/vmlinux
在gdb中使用b命令设置断点

(gdb) b * 0x8025b0b4
最终得到了出错的代码行号

Breakpoint 1 at  0x8025b0b4 : file drivers/usb/gadget/file_storage.c, line 2750.
拿到了行号就可以继续深入debug了，在该行前后加一些BUG_ON（）宏对变量进行测试，最终找到出错的语句。
