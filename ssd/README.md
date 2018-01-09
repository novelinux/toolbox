# Solid State Drives

## 概述

相比于机械硬盘的

### 优点

* 更快的读取速度 - 比普通桌面硬盘快 2-3 倍 (7,200 RPM 使用 SATA2 接口).
* 持续读取速度 - 在整个设备中读取速度不会下降。在普通硬盘中，当磁头从磁盘外缘移向中心时性能会相对下降。
* 极小访问时间 - 比普通硬盘快大约 100 倍。例如，0.1 ms (100 ns) vs. 12-20 ms (12,000-20,000 ns) (桌面硬盘)。
* 高度可靠性
* 没有运动部件
* 极小的产热
* 极小的能源消耗 - 大约1W(闲置)以及 1-2 W (读取) vs. 10-30 W 对于普通硬盘 HDD (与转速有关).
* 轻量级 - 笔记本电脑的理想选择。

### 局限

* 单位容量价格 (每GB数美元 vs. 普通硬盘每GB数美分)。
* 市售容量小于普通硬盘。
* 大的读写单元需要与传统存储介质不同的文件系统优化。闪存翻译层(flash translation layer)隐藏了允许现代操作系统用来优化访问性能的原始的闪存访问。
* 分区和文件系统需要一些针对性的调整。页面大小和擦除页面大小无法自动探测。
* 单元损坏。现代成熟的50nm消费级 MLC 单元可以进行 10000 次写入；35nm 通常可以进行 5000 次写入，25nm 可以进行3000次写入 (工艺越小，密度越高，价格越便宜)。如果写入被正确的分散开，写入不是太小，并且与单元正确对齐，这翻译成固态硬盘的终生写入量是上面的数字乘以容量。日常的写入量必须与期望寿命权衡。
* 复杂的固件和控制器。它们有时候会出现错误。现在它们会消耗和普通硬盘差不多的能量。它们实现了带有垃圾回收功能的日志文件系统，同时转换传统的为旋转媒体设计的 SATA 命令。同时一些固件和控制器会做在线的压缩。它们把重复的写入分散到整个闪存的不同部分，来避免过早的损坏。它们同时还把写入组合起来，这样小的写入就不会导致大的存储单元的重复擦除。最后它们还要移动存储数据的单元，这样这个单元就不会在长时间之后丢失数据。
* 当磁盘变满时性能下降。垃圾回收并不总是能很好的实现，这意味着剩余空间不会总是收集为整个空闲单元。


原生 TRIM 支持是一个很重要的功能，它既可以延长固态硬盘寿命，同时可以在长期减少写入性能下降。
购买正确容量的固态硬盘是关键。对于大多数文件系统，对所有的固态硬盘分区使用 <75 % 的容量可以确保被内核高效的使用。

## 最大化利用SSD的技巧

### 分区对齐

https://wiki.archlinux.org/index.php/GNU_Parted#Check_alignment

### TRIM

绝大多数SSD支持 ATA_TRIM 命令 以保持长期性能和损耗水平。一些测试前后的有关内容，见这个 教程。
自Linux内核版本3.7起，以下文件系统支持 TRIM: Ext4, Btrfs, JFS, VFAT, XFS.
VFAT 只有挂载参数为'discard'(而不是fstrim)时才支持 TRIM 。

本文的文件系统的选择部分提供了更多细节。

检验TRIM支持

```
# hdparm -I /dev/sda | grep TRIM
        *    Data Set Management TRIM supported (limit 1 block)
```

需注意的是有几种TRIM支持的规格。因此，输出内容取决于驱动器支持什么。更多内容见 wikipedia:TRIM#ATA 。

通过挂载参数启用TRIM
在/etc/fstab里使用这个参数以启用TRIM。

```
/dev/sda1  /       ext4   defaults,noatime,discard   0  1
/dev/sda2  /home   ext4   defaults,noatime,discard   0  2
```

注意:

* TRIM当在SSD上使用块设备加密时并非默认启用；更多内容见 Dm-crypt/TRIM support for SSD。

```
Solid state drive users should be aware that, by default, TRIM commands are not enabled by the device-mapper, i.e. block-devices are mounted without the discard option unless you override the default.

The device-mapper maintainers have made it clear that TRIM support will never be enabled by default on dm-crypt devices because of the potential security implications.[3][4] Minimal data leakage in the form of freed block information, perhaps sufficient to determine the filesystem in use, may occur on devices with TRIM enabled. An illustration and discussion of the issues arising from activating TRIM is available in the blog of a cryptsetup developer. If you are worried about such factors, keep also in mind that threats may add up: for example, if your device is still encrypted with the previous (cryptsetup <1.6.0) default cipher --cipher aes-cbc-essiv, more information leakage may occur from trimmed sector observation than with the current default.

The following cases can be distinguished:

The device is encrypted with default dm-crypt LUKS mode:
By default the LUKS header is stored at the beginning of the device and using TRIM is useful to protect header modifications. If for example a compromised LUKS password is revoked, without TRIM the old header will in general still be available for reading until overwritten by another operation; if the drive is stolen in the meanwhile, the attackers could in theory find a way to locate the old header and use it to decrypt the content with the compromised password. See cryptsetup FAQ, section 5.19 What about SSDs, Flash and Hybrid Drives? and Full disk encryption on an ssd.
TRIM can be left disabled if the security issues stated at the top of this section are considered a worse threat than the above bullet.
See also Securely wipe disk#Flash memory.
The device is encrypted with dm-crypt plain mode, or the LUKS header is stored separately:
If plausible deniability is required, TRIM should never be used because of the considerations at the top of this section, or the use of encryption will be given away.
If plausible deniability is not required, TRIM can be used for its performance gains, provided that the security dangers described at the top of this section are not of concern.
Warning: Before enabling TRIM on a drive, make sure the device fully supports TRIM commands, or data loss can occur. See Solid State Drives#TRIM.
In linux 3.1 and up, support for dm-crypt TRIM pass-through can be toggled upon device creation or mount with dmsetup. Support for this option also exists in cryptsetup version 1.4.0 and up. To add support during boot, you will need to add :allow-discards to the cryptdevice option. The TRIM option may look like this:

cryptdevice=/dev/sdaX:root:allow-discards
For the main cryptdevice configuration options before the :allow-discards see Dm-crypt/System configuration.

If you are using a systemd based initrd, you must pass:

rd.luks.options=discard
Besides the kernel option, it is also required to periodically run fstrim or mount the filesystem (e.g. /dev/mapper/root in this example) with the discard option in /etc/fstab. For details, please refer to the TRIM page.

For LUKS devices unlocked manually on the console or via /etc/crypttab either discard or allow-discards may be used.
```

* 如果你周期性运行 fstrim 的话没必要使用 discard 参数。
* 在ext3的根分区上使用 discard 参数的话会导致它被挂载为只读模式。
* 警告: 用户试图以 discard 参数挂载分区前需确认你们的SSD支持TRIM，否则会造成数据丢失！


我们推荐在支持TRIM的SSD上启用它。但有时会导致SSD删除文件时 运转缓慢 。此类情况下，必须使用 fstrim 作为替代。

```
# fstrim -v /
```

应用 fstrim 的分区必须已挂载，且必须以挂载点指代。

如果这个方法看起来是个更好的选择，以 cron 来时不时执行它也应是极好的。为了执行每日计划任务， cron 软件包 (cronie)默认包含了一个默认设置用来执行每小时，每日，每周，每月的计划任务的 anacron 实现。
需注意 cronie systemd 服务 在新安装的Arch里并非默认启用。要将其加入每日 cron 任务中，只需创建一个执行你想要行为的脚本并将其放入 /etc/cron.daily, /etc/cron.weekly等中。使用此种方法时，
nice 和 ionice 值设为推荐值。实现后，将discard选项从/etc/fstab中移除。

注意: 将 discard 挂载参数作为首选。常规启用TRIM方法之外再考虑这种方法。
通过 systemd 服务应用TRIM
util-linux 包提供了 fstrim.service 和 fstrim.timer systemd的 unit 文件。 启用 计时器将每周激活这个服务来 trim 设备上所有已挂载的支持discard操作的文件系统。

用 tune2fs 启用TRIM(不推荐)
可用 tune2fs 来静态设置 trim 参数:

```
# tune2fs -o discard /dev/sdXY
```

警告: 这种方法会导致 discard 选项在 mount 里 不出现 。
为LVM启用TRIM
在 /etc/lvm/lvm.conf里把issue_discards 选项的值由 0 改为 1。

注意: 启用该选项会使得当逻辑卷不再使用物理卷的空间(如 lvremove, lvreduce, 等等)时，将discard发给逻辑卷的底层物理卷 (见 lvm.conf(5) 和/或 /etc/lvm/lvm.conf 中的注释内容)。 因此，它似乎并不需要为“常规”TRIM请求（文件系统内的文件删除）来发挥作用。

### 为dm-crypt启用TRIM

警告: discard选项允许丢弃通过加密的块设备传递的请求。这提高了SSD的存储性能，但带来了安全隐患。更多信息见 Dm-crypt/TRIM support for SSD 。
对非根文件系统，为SSD上的块设备配置 /etc/crypttab 来把 discard 加入到选项列表中 (见 Dm-crypt/System configuration#crypttab)。

对于根文件系统，遵循 Dm-crypt/TRIM support for SSD 的指示来将正确的内核参数加入到bootloader配置中。

### I/O调度器

考虑从默认 CFQ (Completely Fair Queuing)调度器切换到 NOOP 或 Deadline。后两者为SSD提供了性能加速。例如NOOP调度器，实现了一个对所有传入I/O请求的简单队列，而无需重新排序和分组那些在物理磁盘上相近的。SSD的寻道时间对所有扇区都想同因此无效，需要重新排序基于它们的I/O队列。

在 Arch 上CFQ调度器默认是启用的。查看 /sys/block/sdX/queue/scheduler的内容来检查:

```
$ cat /sys/block/sdX/queue/scheduler
noop deadline [cfq]
```

当前使用的调度器是可用调度器中括号括起来的那一个。

用户可以随时更改而无需重启:

```
# echo noop > /sys/block/sdX/queue/scheduler
```

或:

```
$ sudo tee /sys/block/sdX/queue/scheduler <<< noop
```

这种设置方法并不持久 (例如，重启后变更撤销)。再次查看文件的内容以及确认"noop"被括起来来确认更改。

内核参数(针对单个设备)
如果在系统中是SSD是唯一的存储设备，考虑通过 elevator=noop 内核参数对整个系统设置I/O调度器.

针对单个设备或者HDD/SDD混合情况使用udev
尽管上述方法无疑可行，并被认为是可靠的工作环境。因此，若想使用首先对设备负责而不是实现调度器的系统的话，采用udev。为此只需一条简单的udev规则。

创建如下内容:

```
/etc/udev/rules.d/60-schedulers.rules
# set deadline scheduler for non-rotating disks
ACTION=="add|change", KERNEL=="sd[a-z]", ATTR{queue/rotational}=="0", ATTR{queue/scheduler}="deadline"
```

当然，将 Deadline/CFQ 设置为想要的调度器。变更在重启后生效。为检验变更成功:

```
$ cat /sys/block/sdX/queue/scheduler  # X是应用变更的设备
```

注意: 此例中选择编号60是因为udev使用该编号用于自身的永久命名规则。因此，块设备此刻似乎可修改，而这是对于该特定规则的安全位置。但是只要以.rules结尾，规则可以随便命名。
SSD上的交换空间
在SSD上可分配 swap 分区，但大多数配置2G以上内存的现代电脑几乎不用swap。值得注意的是当系统启用休眠特性时例外。

要使用SSD上的 swap 分区的话，推荐将 swappiness 的值改得非常低 (比如 1)以此避免对 swap 的写入。

Hdparm 显示 "frozen" 状态
一些主板BIOS在初始化时发送了"security freeze"命令给连接上的存储设备。同样，一些SSD(和HDD) BIOS在工厂已设置为"security freeze"。二者都会导致设备的密码安全设置设为 frozen，如下面的输出:

```
:~# hdparm -I /dev/sda
Security:
 	Master password revision code = 65534
 		supported
 	not	enabled
 	not	locked
 		frozen
 	not	expired: security count
 		supported: enhanced erase
 	4min for SECURITY ERASE UNIT. 2min for ENHANCED SECURITY ERASE UNIT.
```

如格式化设备或安装新系统之类的操作不受"security freeze"影响。

上面的输出显示了设备在启动时not locked，而frozen状态保护设备免于间谍软件侵害。它们试图在运行时设置密码来达到目的。

如果你想为"frozen"的设备设置密码，则必须要主板BIOS支持才可。许多笔记本都支持，这是因为这是 硬件加密 所必需的，但并非台式机/服务器主板所必需。例如，对于 Intel DH67CL/BL 主板，必须用跳线设置为"maintenance mode"来查看设置 (见 [1], [2])。

警告: 不要试图用hdparm来改变上述的lock安全设置，除非你十分清楚自己在干什么。
如果你想擦除SSD，见 Securely wipe disk#hdparm 以及 下文。

### SSD存储单元的清除

有时用户希望通过重置SSD单元到刚安装时的纯净状态以使其恢复到 出厂时的写入性能。即使是原生支持TRIM的SSD，其写入性能也会随时间变差。TRIM只在文件删除时起作用，而不是如增量保存一样的替代保障措施。

https://www.anandtech.com/show/2738/8

重置按wiki文章SSD memory cell clearing指示，三步之内即可轻松完成。

### 处理NCQ错误

部分SSD和SATA芯片组并不在Linux的原生命令队列(NCQ)下正常工作。dmesg错误提示看起来像这样:

[ 9.115544] ata9: exception Emask 0x0 SAct 0xf SErr 0x0 action 0x10 frozen
[ 9.115550] ata9.00: failed command: READ FPDMA QUEUED
[ 9.115556] ata9.00: cmd 60/04:00:d4:82:85/00:00:1f:00:00/40 tag 0 ncq 2048 in
[ 9.115557] res 40/00:18:d3:82:85/00:00:1f:00:00/40 Emask 0x4 (timeout)
问题可由以下某一方法解决:

更新SSD固件。Intelligent partition scheme
更新主板的BIOS/UEFI固件。见 Flashing BIOS from Linux。
启动时禁止NCQ。在Bootloader配置的kernel行里添加 libata.force=noncq 。
如果问题仍未解决或者导致了其它问题， 提交一个bug。

### 最小化硬盘读写的技巧

在定位大量读写操作方面，”简化“是SSD使用中的重要主题。如此会延长SSD的使用寿命。这主要是由于大的擦除块大小(某些情况下512KB)；或是大量小的写入等效于一次大的写入。

注意: 一个32GB，10倍中等的写入放大系数，标准的10000写入/擦除周期，以及每日写入10GB数据的SSD预计会有8年寿命。当考虑更大的SSD，现代的控制器，以及小一点的写入放大系数时，表现会更好。当决定是否限制硬盘写入时参考 [3] 来看是否确实需要。
使用 iotop 以及 sort 对硬盘写入排序来观察程序对硬盘写的量及频率。

提示： iotop 使用 -b 参数可在批处理模式而不是默认的交互模式下运行。 -o 用于查看正在输入输出的程序， -qqq 用于废止字段名和I/O总览。更多选项见 iotop(8)。

```
# iotop -boqqq
```

智能分区方案
对于SSD和HDD同时使用的系统，考虑把 /var 放在HDD上以减少SDD读写损耗。
noatime 挂载参数
见 fstab#atime options。

### 将频繁使用的文件置于内存

浏览器配置文件
人们可以轻松通过tmpfs把浏览器配置文件挂载入内存，如chromium, firefox, opera等，同时也使用rsync，同步与基于硬盘的备份。除了明显的速度的改进，用户也将节省他们的SSD读/写周期。

AUR上有自动完成这些过程的软件，如 profile-sync-daemonAUR。

其他
同理频繁访问的目录如 /srv/http (如果正运行网页服务器)也可挂载入内存。 profile-sync-daemonAUR 的姐妹课题是 anything-sync-daemonAUR， 它允许用户将任意 目录使用相同的基本逻辑和安全防护措施同步入内存。

在tmpfs里编译
在tmpfs里编译是减少硬盘读写的好招数。更多详见 Makepkg#Improving compile times。

### 文件系统上禁用日志

在SSD上使用带日志功能的文件系统(如ext4)不打开日志选项来减少硬盘读写。其明显缺点是非正常卸载分区（即断电后，内核锁定等）会造成数据丢失。Ted Tso 主张在大多数情况下日志可以以最小的多余的读/写周期被启用: 以 noatime 参数挂载的ext4文件系统的写入数据总量 (MB为单位)。

```
operation	journal	w/o journal	percent change
git clone	367.0	353.0	3.81 %
make	207.6	199.4	3.95 %
make clean	6.45	3.73	42.17 %
```

"研究结果表明，重的元数据工作负载，如make clean，确实造成了近两倍写入磁盘的数据量。这是可以预料的，因为元数据块所有的改变都是第一次写日志和日志变更提交之前写入到磁盘上的最终位置。然而，更常见的工作负载，如写数据以及修改文件系统元数据块，差异要小得多。"

注意: 上面表格中的 make clean 实例代表了在tmpfs中编译这个建议的重要性!
文件系统的选择
Btrfs
Btrfs 在Linux主线版本2.6.29发布之后就已支持。有些人觉得它用于生产工作尚不成熟，同时也有早就接纳这个对ext4的潜在胜者的人。用户们应该参阅Btrfs以寻找更多信息。

Ext4
Ext4是另一个支持SSD的文件系统。自从2.6.28之后它就已经稳定并足够成熟来支持日常使用。ext4用户必须用 discard 挂载参数，或 tune2fs -o discard /dev/sdaX 以明确启用TRIM支持。 更多信息见 official in kernel tree documentation.

XFS
许多用户并不知道除了Btrfs和Ext4之外， XFS 也支持TRIM。这可用常规方法启用。即，使用前文提到的 discard 选项，或者是 fstrim 命令。更多信息见XFS wiki.

JFS
自Linux内核3.7版以来，就加入了可用的TRIM支持。目前为止有关此并没有太多讨论，都整理于Linux news sites.显然可通过 discard 挂载选项或者 fstrim 命令。

其他文件系统
有些为SSD而特殊设计的 文件系统，例如 F2FS.

固件升级
ADATA
ADATA 在其支持页面上提供了Linux (i686)版的工具。在选择型号之后会出现到最新固件的链接。最新的Linux上的升级工具打包有固件，并必须以root权限运行。必须首先给二进制文件设置合适的权限。

Crucial
Crucial 提供了以ISO镜像文件升级固件的选项。镜像可以在选择产品和下载"Manual Boot File"之后找到。M4 Crucial 型号的用户须确认固件是否通过 smartctl 来升级。

$ smartctl --all /dev/sdX
==> WARNING: This drive may hang after 5184 hours of power-on time:
http://www.tomshardware.com/news/Crucial-m4-Firmware-BSOD,14544.html
See the following web pages for firmware updates:
http://www.crucial.com/support/firmware.aspx
http://www.micron.com/products/solid-state-storage/client-ssd#software
建议看见这个警告的用户备份所有重要数据并立即升级。

Kingston
Kingston 提供了基于 Sandforce 控制器的设备的Linux版升级工具: SSD support page.点击页面上的图片来跳转到你的SSD的型号。特别地如SH100S3 SSD的支持可在此找到。

Mushkin
不怎么出名的 Mushkin 牌固态硬盘也使用 Sandforce 控制器，提供了Linux版的升级工具 (和 Kingston 的几乎一样)。

OCZ
OCZ 在论坛上提供了Linux (i686 and x86_64) 版的升级工具。

Samsung
Samsung 注意到使用他们的 Magician Software 是"不支持的"，但是是可能的。显然 Magician Software 可以把USB做成以升级固件启动。最简单的方式是使用他们提供的用于升级固件的可启动的ISO镜像。可从这里获取。

注意: Samsung 根本不明确提供这些。他们似乎有四个固件升级页面，每个页面要求做不同的事情。
用户更喜欢从USB的live Linux系统上升级固件(而不是在Microsoft Windows下用三星的 "Magician" 软件)。参见这里。

SanDisk
SanDisk 制作ISO固件镜像来允许用户在 SanDisk SSD 工具包不支持的系统上升级。必须找到正确的SSD 型号以及它的容量(例如 60GB, 或者 256GB)。烧制合适的ISO固件镜像之后，只需重启电脑启动到新创立的CD/DVD启动盘(也可以是USB)。

ISO镜像必须只包含一个linux内核和一个initrd.解压到 /boot 分区并用 GRUB 或者 Syslinux 启动它来升级。

目前我找不到列出固件升级的单独页面(恕我直言网站简直一团糟)，但这里有一些相关链接:

SanDisk Extreme SSD Firmware Release notes and Manual Firmware update version R211

SanDisk Ultra SSD Firmware release notes and Manual Firmware update version 365A13F0

##

https://wiki.archlinux.org/index.php/Solid_State_Drives_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87)
