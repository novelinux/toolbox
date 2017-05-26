# EMMC

## Life Time

/sys/class/block/mmcblk0/device/manfid 是vendor ID，解析如下:

```
#define CID_MANFID_SANDISK    0x2
#define CID_MANFID_TOSHIBA    0x11
#define CID_MANFID_MICRON    0x13
#define CID_MANFID_SAMSUNG    0x15
#define CID_MANFID_HYNIX    0x90
#define CID_MANFID_NUMONYX_MICRON 0xfe
```

以下两个是存储lifetime信息的，

```
/sys/class/block/mmcblk0/device/life_time_est_typ_a
/sys/class/block/mmcblk0/device/life_time_est_typ_b
```

life_time_est_typ_a 和life_time_est_typ_b的值的意义都相同，如下：

https://github.com/novelinux/toolbox/tree/master/emmc/emmc_lifetime.png

不同的vendor可能会使用life_time_est_typ_a或life_time_est_typ_b中的一个来记录芯片的寿命信息。
Hynix的这个芯片使用的是life_time_est_typ_b. life_time_est_typ_a和life_time_est_typ_b 是咱们这边添加的,
对于没有添加这两个节点的机型，就直接读取/sys/kernel/debug/mmc0/mmc0:0001/ext_csd文件，
文件中的第268字节对应life_time_est_typ_a，第269字节对应life_time_est_typ_b

## MANFID

```
#define CID_MANFID_SANDISK    0x2
#define CID_MANFID_TOSHIBA    0x11
#define CID_MANFID_MICRON    0x13
#define CID_MANFID_SAMSUNG    0x15
#define CID_MANFID_HYNIX    0x90
#define CID_MANFID_NUMONYX_MICRON 0xfe
```