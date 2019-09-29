一.问题起因
A1 N上接连收到两例用户在使用过程中重启后无法正常启动,通过查看log, ps查看进程, mount等命令查看分区,确认是数据分区没有被成功挂载导致. dmesg信息大致如下:

`

[ 3.879480] EXT4-fs (sda14): Ignoring removed nomblk_io_submit option
[ 3.879485] EXT4-fs (sda14): Couldn't mount because of unsupported optional features (768b3408)
[ 3.879529] fs_mgr: check_fs(): mount(/dev/block/bootdevice/by-name/userdata,/data,ext4)=-1: Invalid argument
[ 3.882770] fs_mgr: Running /system/bin/e2fsck on /dev/block/bootdevice/by-name/userdata
[ 3.890959] random: e2fsck urandom read with 91 bits of entropy available
[ 3.920609] e2fsck: e2fsck 1.42.9 (28-Dec-2013)
[ 3.920609]
[ 3.920671] e2fsck: /system/bin/e2fsck: Filesystem revision too high while trying to open /dev/block/bootdevice/by-name/userdata
[ 3.920671]
[ 3.920681] e2fsck:
[ 3.920681]
[ 3.920691] e2fsck: The filesystem revision is apparently too high for this version of e2fsck.
[ 3.920691]
[ 3.920701] e2fsck: (Or the filesystem superblock is corrupt)
[ 3.920701]
[ 3.920709] e2fsck:
[ 3.920709]
[ 3.920718] e2fsck:
[ 3.920718]
[ 3.920727] e2fsck: The superblock could not be read or does not describe a correct ext2
[ 3.920727]
[ 3.920736] e2fsck: filesystem. If the device is valid and it really contains an ext2
[ 3.920736]
[ 3.920746] e2fsck: filesystem (and not swap or ufs or something else), then the superblock
[ 3.920746]
[ 3.920755] e2fsck: is corrupt, and you might try running e2fsck with an alternate superblock:
[ 3.920755]
[ 3.920764] e2fsck: e2fsck -b 8193 <device>
[ 3.920764]
[ 3.920773] e2fsck:
[ 3.920773]
[ 3.920786] e2fsck: e2fsck terminated by exit(8)
[ 3.920786]
[ 3.921415] fs_mgr: Running /system/bin/tune2fs on /dev/block/bootdevice/by-name/userdata
[ 3.932411] EXT4-fs (sda13): mounted filesystem with ordered data mode. Opts: barrier=1
[ 3.932435] SELinux: initialized (dev sda13, type ext4), uses xattr
[ 3.935723] EXT4-fs (sde32): mounted filesystem with ordered data mode. Opts: barrier=1
[ 3.935748] SELinux: initialized (dev sde32, type ext4), uses xattr
[ 3.936955] SELinux: initialized (dev sde35, type vfat), uses mountpoint labeling
[ 3.938077] SELinux: initialized (dev sde26, type vfat), uses mountpoint labeling
[ 3.963593] logd.auditd: start
[ 3.963644] logd.klogd: 3963616403

`

二.代码分析

Android系统中的分区挂载是在init进程中通过mount_all命令来执行的,如下:

path: init.target.rc

`

on fs
 wait /dev/block/platform/soc/1da4000.ufshc
 symlink /dev/block/platform/soc/1da4000.ufshc /dev/block/bootdevice
 mount_all fstab.qcom
`

对应mount_all的实现如下所示:

path: system/core/init/builtins.cpp

`

/* mount_all <fstab> [ <path> ]* 507 * 508 * This function might request a reboot, in which case it will 509 * not return. 510 */
511static int do_mount_all(const std::vector<std::string>& args) {
512    pid_t pid;
513    int ret = -1;
514    int child_ret = -1;
515    int status;
516    struct fstab *fstab;
517
518    const char* fstabfile = args[1].c_str();
519    /*
520 * Call fs_mgr_mount_all() to mount all filesystems. We fork(2) and
521 * do the call in the child to provide protection to the main init
522 * process if anything goes wrong (crash or memory leak), and wait for
523 * the child to finish in the parent.
524 */
525    pid = fork();
526    if (pid > 0) {
527        /* Parent. Wait for the child to return */
528        int wp_ret = TEMP_FAILURE_RETRY(waitpid(pid, &status, 0));
529        if (wp_ret < 0) {
530            /* Unexpected error code. We will continue anyway. */
531            NOTICE("waitpid failed rc=%d: %s\n", wp_ret, strerror(errno));
532        }
533
534        if (WIFEXITED(status)) {
535            ret = WEXITSTATUS(status);
536        } else {
537            ret = -1;
538        }
539    } else if (pid == 0) {
540        /* child, call fs_mgr_mount_all() */
541        klog_set_level(6);  /* So we can see what fs_mgr_mount_all() does */
542        fstab = fs_mgr_read_fstab(fstabfile);
543        child_ret = fs_mgr_mount_all(fstab);
544        fs_mgr_free_fstab(fstab);
545        if (child_ret == -1) {
546            ERROR("fs_mgr_mount_all returned an error\n");
547        }
548        _exit(child_ret);
549    } else {
550        /* fork failed, return an error */
551        return -1;
552    }
553
554    /* Paths of .rc files are specified at the 2nd argument and beyond */
555    import_late(args, 2);
556
557    std::string bootmode = property_get("ro.bootmode");
558    if (strncmp(bootmode.c_str(), "ffbm", 4) == 0) {
559        NOTICE("ffbm mode, not start class main\n");
560        return 0;
561    }
562
563    if (ret == FS_MGR_MNTALL_DEV_NEEDS_ENCRYPTION) {
564        ActionManager::GetInstance().QueueEventTrigger("encrypt");
565    } else if (ret == FS_MGR_MNTALL_DEV_MIGHT_BE_ENCRYPTED) {
566        property_set("ro.crypto.state", "encrypted");
567        property_set("ro.crypto.type", "block");
568        ActionManager::GetInstance().QueueEventTrigger("defaultcrypto");
569    } else if (ret == FS_MGR_MNTALL_DEV_NOT_ENCRYPTED) {
570        property_set("ro.crypto.state", "unencrypted");
571        ActionManager::GetInstance().QueueEventTrigger("nonencrypted");
572    } else if (ret == FS_MGR_MNTALL_DEV_NOT_ENCRYPTABLE) {
573        property_set("ro.crypto.state", "unsupported");
574        ActionManager::GetInstance().QueueEventTrigger("nonencrypted");
575    } else if (ret == FS_MGR_MNTALL_DEV_NEEDS_RECOVERY) {
576        /* Setup a wipe via recovery, and reboot into recovery */
577        ERROR("fs_mgr_mount_all suggested recovery, so wiping data via recovery.\n");
578        ret = wipe_data_via_recovery("wipe_data_via_recovery");
579        /* If reboot worked, there is no return. */
580    } else if (ret == FS_MGR_MNTALL_DEV_FILE_ENCRYPTED) {
581        if (e4crypt_install_keyring()) {
582            return -1;
583        }
584        property_set("ro.crypto.state", "encrypted");
585        property_set("ro.crypto.type", "file");
586
587        // Although encrypted, we have device key, so we do not need to
588        // do anything different from the nonencrypted case.
589        ActionManager::GetInstance().QueueEventTrigger("nonencrypted");
590    } else if (ret > 0) {
591        ERROR("fs_mgr_mount_all returned unexpected error %d\n", ret);
592    }
593    /* else ... < 0: error */
594
595    return ret;
596}
`

大致流程就是在init进程中fork一个子进程通过函数fs_mgr_mount_all来挂载所有分区.在fs_mgr_mount_all函数中又调用mout_with_alternatives函数来进行挂载

`

503static int mount_with_alternatives(struct fstab *fstab, int start_idx, int *end_idx, int *attempted_idx)
504{
505    int i;
506    int mount_errno = 0;
507    int mounted = 0;
508
509    if (!end_idx || !attempted_idx || start_idx >= fstab->num_entries) {
510      errno = EINVAL;
511      if (end_idx) *end_idx = start_idx;
512      if (attempted_idx) *end_idx = start_idx;
513      return -1;
514    }
515
516    /* Hunt down an fstab entry for the same mount point that might succeed */
517    for (i = start_idx;
518         /* We required that fstab entries for the same mountpoint be consecutive */
519         i < fstab->num_entries && !strcmp(fstab->recs[start_idx].mount_point, fstab->recs[i].mount_point);
520         i++) {
521            /* 522 * Don't try to mount/encrypt the same mount point again. 523 * Deal with alternate entries for the same point which are required to be all following 524 * each other. 525 */
526            if (mounted) {
527                ERROR("%s(): skipping fstab dup mountpoint=%s rec[%d].fs_type=%s already mounted as %s.\n", __func__,
528                     fstab->recs[i].mount_point, i, fstab->recs[i].fs_type, fstab->recs[*attempted_idx].fs_type);
529                continue;
530            }
531
532            if (fstab->recs[i].fs_mgr_flags & MF_CHECK) {
533                check_fs(fstab->recs[i].blk_device, fstab->recs[i].fs_type,
534                         fstab->recs[i].mount_point, &fstab->recs[i]);
535            }
536
537            if (fstab->recs[i].fs_mgr_flags & MF_RESERVEDSIZE) {
538                do_reserved_size(fstab->recs[i].blk_device, fstab->recs[i].fs_type,
539                                 &fstab->recs[i]);
540            }
541
542            if (!__mount(fstab->recs[i].blk_device, fstab->recs[i].mount_point, &fstab->recs[i])) {
543                *attempted_idx = i;
544                mounted = 1;
545                if (i != start_idx) {
546                    ERROR("%s(): Mounted %s on %s with fs_type=%s instead of %s\n", __func__,
547                         fstab->recs[i].blk_device, fstab->recs[i].mount_point, fstab->recs[i].fs_type,
548                         fstab->recs[start_idx].fs_type);
549                }
550            } else {
551                /* back up errno for crypto decisions */
552                mount_errno = errno;
553            }
554    }
555
556    /* Adjust i for the case where it was still withing the recs[] */
557    if (i < fstab->num_entries) --i;
558
559    *end_idx = i;
560    if (!mounted) {
561        *attempted_idx = start_idx;
562        errno = mount_errno;
563        return -1;
564    }
565    return 0;
566}
`

经过对比正常机器发现在调用do_reserved_size函数时导致负责挂载分区的子进程中断了,导致分区无法正常挂载.该函数的实现如下所示:

`

291/* Function to read the primary superblock */
292static int read_super_block(int fd, struct ext4_super_block *sb)
293{
294    off64_t ret;
295    ret = lseek64(fd, 1024, SEEK_SET);
296    if (ret < 0)
297        return ret;
298    ret = read(fd, sb, sizeof(*sb));
299    if (ret < 0)
300        return ret;
301    if (ret != sizeof(*sb))
302        return ret;
303    if (sb->s_magic != EXT4_SUPER_MAGIC)
304        return -EINVAL;
305    return 0;
306}
307
308static ext4_fsblk_t ext4_blocks_count(struct ext4_super_block *es)
309{
310    return ((ext4_fsblk_t)le32_to_cpu(es->s_blocks_count_hi) << 32) |
311            le32_to_cpu(es->s_blocks_count_lo);
312}
313
314static ext4_fsblk_t ext4_r_blocks_count(struct ext4_super_block *es)
315{
316    return ((ext4_fsblk_t)le32_to_cpu(es->s_r_blocks_count_hi) << 32) |
317            le32_to_cpu(es->s_r_blocks_count_lo);
318}
319
320static void do_reserved_size(char *blk_device, char *fs_type, struct fstab_rec *rec)
321{
322    /* Check for the types of filesystems we know how to check */
323    if (!strcmp(fs_type, "ext2") || !strcmp(fs_type, "ext3") || !strcmp(fs_type, "ext4")) {
324        /* 325 * Some system images do not have tune2fs for licensing reasons 326 * Detect these and skip reserve blocks. 327 */
328        if (access(TUNE2FS_BIN, X_OK)) {
329            ERROR("Not running %s on %s (executable not in system image)\n",
330                  TUNE2FS_BIN, blk_device);
331        } else {
332            INFO("Running %s on %s\n", TUNE2FS_BIN, blk_device);
333
334            int status = 0;
335            int ret = 0;
336            unsigned long reserved_blocks = 0;
337            int fd = TEMP_FAILURE_RETRY(open(blk_device, O_RDONLY | O_CLOEXEC));
338            if (fd >= 0) {
339                struct ext4_super_block sb;
340                ret = read_super_block(fd, &sb);
341                if (ret < 0) {
342                    ERROR("Can't read '%s' super block: %s\n", blk_device, strerror(errno));
343                    goto out;
344                }
345                reserved_blocks = rec->reserved_size / EXT4_BLOCK_SIZE(&sb);
346                unsigned long reserved_threshold = ext4_blocks_count(&sb) * 0.02;
347                if (reserved_threshold < reserved_blocks) {
348                    WARNING("Reserved blocks %lu is too large\n", reserved_blocks);
349                    reserved_blocks = reserved_threshold;
350                }
351                if (ext4_r_blocks_count(&sb) == reserved_blocks) {
352                    INFO("Have reserved same blocks\n");
353                    goto out;
354                }
355            } else {
356                ERROR("Failed to open '%s': %s\n", blk_device, strerror(errno));
357                return;
358            }
359
360            char buf[16] = {0};
361            snprintf(buf, sizeof (buf), "-r %lu", reserved_blocks);
362            char *tune2fs_argv[] = {
363                TUNE2FS_BIN,
364                buf,
365                blk_device,
366            };
367
368            ret = android_fork_execvp_ext(ARRAY_SIZE(tune2fs_argv), tune2fs_argv,
369                                          &status, true, LOG_KLOG | LOG_FILE,
370                                          true, NULL, NULL, 0);
371
372            if (ret < 0) {
373                /* No need to check for error in fork, we can't really handle it now */
374                ERROR("Failed trying to run %s\n", TUNE2FS_BIN);
375            }
376      out:
377            close(fd);
378        }
379    }
380}
`

经过我们不断加LOG调试发现出问题的语句是:

`

345                reserved_blocks = rec->reserved_size / EXT4_BLOCK_SIZE(&sb);
`

一般我们推断这条出问题的语句最大可能就是EXT4_BLOCK_SIZE(&sb)这个宏计算出来的值为0导致,除数为0一般会导致进程异常中断,为了验证这个推断

我们打印EXT4_BLOCK_SIZE(&sb)这个宏计算出来的值看下,但是令我们意想不到的情况发生了: 这个宏的值打印不出来,每次调用这个宏程序就中断.

于是,我们进一步推断,难道是这个宏的其他操作导致的么?我们看下这个宏的实现:

path: system/extras/ext4_utils

`

93#define EXT4_MIN_BLOCK_SIZE 1024
94#define EXT4_MAX_BLOCK_SIZE 65536
95#define EXT4_MIN_BLOCK_LOG_SIZE 10
96#define EXT4_BLOCK_SIZE(s) (EXT4_MIN_BLOCK_SIZE << (s)->s_log_block_size)
```
这个宏太简单了就是1024左移操作指定的s_log_block_size个bit数,按照我们的常理来推断的话,这个怎么可能导致进程中断呢?顶多就是整数溢出么,但是也不会

造成程序异常中断呀, 为了验证这个问题, 特意编写了一个测试case如下:

`

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>


int main(int argc, char *argv[])
{
    unsigned int val = 1024;
    int bits = atoi(argv[1]);

    printf("Hello world: %x\n", (val << bits));

    return 0;
}

`

事实证明我们的推断是对的,将这个程序编译push进手机运行,确实左移操作顶多造成这个整数溢出,并不会导致进程异常中断呀,有点颠覆三观了,不禁怀疑自己智商了,

于是再次做一个大胆猜测: 编译器选项不一样?

于是查看了下对应fs_mgr的编译选项发现如下定义:

`

LOCAL_CLANG := true
LOCAL_SANITIZE := integer

`

于是将如上两个选项设置添加到我们的测试case的Android.mk中,编译push进手机,果然LOCAL_SANITIZE := integer这个选项如果设置的话,编译器会对程序插入检查代码,

如果对应整数计算发生溢出了,将会调用abort函数导致对应的进程异常中断.

当然我们最终的问题原因是,此时数据分区是加密的,对应的super block中是乱码,但是恰好对应magic number值不是乱码,对应的check语句没有check到此时是super block是加密状态,

造成对应的s_log_block_size过大,造成1024 移位overflow导致,最终我们修复change如下:

http://git.sys.xiaomi.com/#/c/82980/

