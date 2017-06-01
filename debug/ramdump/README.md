# Ramdump

* 1.Ramdump 分区可以 mount上，再adb pull出来。“mkdir /mnt/ramdump | mount –t vfat /dev/block/bootdevice/by-name/ramdump /mnt/ramdump”

* 2.Logcat.bin需要一个工具来解析. 编译环境下，执行： mmm system/core/logparse 会生成这个工具。

* 3.在enable download_mode后，android os以下的部分发生异常重启都会进入到这个模式，比如kernel panic, TZ fatal error之类的。