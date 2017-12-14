# sdcardfs hang

## 一.现象:

摩拜单车和支付宝开启sdcardfs后, 每次打开都ANR.

## 二.分析ANR原因:

```
12-04 11:23:15.657  1696  1863 E ActivityManager: ANR in com.zhiliaoapp.musically (com.zhiliaoapp.musically/.activity.MainShowActivity)
12-04 11:23:15.657  1696  1863 E ActivityManager: PID: 7966
12-04 11:23:15.657  1696  1863 E ActivityManager: Reason: Input dispatching timed out (com.zhiliaoapp.musically/com.zhiliaoapp.musically.activity.MainShowActivity, Waiting to send non-key event because the touched window has not finished processing certain input events that were delivered to it over 500.0ms ago.  Wait queue length: 10.  Wait queue head age: 9309.9ms.)


"main" prio=5 tid=1 Blocked
  | group="main" sCount=1 dsCount=0 obj=0x769bc000 self=0xeb605400
  | sysTid=7966 nice=-4 cgrp=default sched=0/0 handle=0xee4bf538
  | state=S schedstat=( 0 0 0 ) utm=227 stm=43 core=6 HZ=100
  | stack=0xff1cb000-0xff1cd000 stackSize=8MB
  | held mutexes=
  at com.facebook.cache.disk.DiskStorageCache.hasKeySync(DiskStorageCache.java:613)
  - waiting to lock <0x04051433> (a java.lang.Object) held by thread 141
  at com.facebook.imagepipeline.cache.BufferedDiskCache.containsSync(BufferedDiskCache.java:74)
  at com.facebook.imagepipeline.cache.BufferedDiskCache.contains(BufferedDiskCache.java:85)
  at com.facebook.imagepipeline.core.ImagePipeline.isInDiskCache(ImagePipeline.java:542)
  at com.zhiliaoapp.musically.common.utils.MusFrescoUtils.isFirstFramePrefetched(MusFrescoUtils.java:117)
  at com.zhiliaoapp.musically.musicalshow.view.MusicallyVideoView.initMusicalFirstFrame(MusicallyVideoView.java:578)
  at com.zhiliaoapp.musically.musicalshow.view.MusicallyVideoView.initViews(MusicallyVideoView.java:448)
  at com.zhiliaoapp.musically.musicalshow.view.MusicallyVideoView.initViews(MusicallyVideoView.java:418)
  at com.zhiliaoapp.musically.adapter.SimpleVideoViewpagerAdapter.getView(SimpleVideoViewpagerAdapter.java:365)
  at com.zhiliaoapp.musically.adapter.salvage.RecyclingPagerAdapter.instantiateItem(RecyclingPagerAdapter.java:36)
  at com.zhiliaoapp.musically.customview.VerticalViewPager.addNewItem(VerticalViewPager.java:832)
  at com.zhiliaoapp.musically.customview.VerticalViewPager.populate(VerticalViewPager.java:1027)
  at com.zhiliaoapp.musically.customview.VerticalViewPager.populate(VerticalViewPager.java:915)
  at com.zhiliaoapp.musically.customview.VerticalViewPager$3.run(VerticalViewPager.java:250)
  at com.zhiliaoapp.musically.customview.VerticalViewPager.completeScroll(VerticalViewPager.java:1751)
  at com.zhiliaoapp.musically.customview.VerticalViewPager.onInterceptTouchEvent(VerticalViewPager.java:1899)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2175)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2634)
  at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2264)
  at com.android.internal.policy.DecorView.superDispatchTouchEvent(DecorView.java:416)
  at com.android.internal.policy.PhoneWindow.superDispatchTouchEvent(PhoneWindow.java:1808)
  at android.app.Activity.dispatchTouchEvent(Activity.java:3195)
  at android.support.v7.view.WindowCallbackWrapper.dispatchTouchEvent(WindowCallbackWrapper.java:71)
  at com.android.internal.policy.DecorView.dispatchTouchEvent(DecorView.java:378)
  at android.view.View.dispatchPointerEvent(View.java:10258)
  at android.view.ViewRootImpl$ViewPostImeInputStage.processPointerEvent(ViewRootImpl.java:4519)
  at android.view.ViewRootImpl$ViewPostImeInputStage.onProcess(ViewRootImpl.java:4384)
  at android.view.ViewRootImpl$InputStage.deliver(ViewRootImpl.java:3924)
  at android.view.ViewRootImpl$InputStage.onDeliverToNext(ViewRootImpl.java:3977)
  at android.view.ViewRootImpl$InputStage.forward(ViewRootImpl.java:3943)
  at android.view.ViewRootImpl$AsyncInputStage.forward(ViewRootImpl.java:4070)
  at android.view.ViewRootImpl$InputStage.apply(ViewRootImpl.java:3951)
  at android.view.ViewRootImpl$AsyncInputStage.apply(ViewRootImpl.java:4127)
  at android.view.ViewRootImpl$InputStage.deliver(ViewRootImpl.java:3924)
  at android.view.ViewRootImpl$InputStage.onDeliverToNext(ViewRootImpl.java:3977)
  at android.view.ViewRootImpl$InputStage.forward(ViewRootImpl.java:3943)
  at android.view.ViewRootImpl$InputStage.apply(ViewRootImpl.java:3951)
  at android.view.ViewRootImpl$InputStage.deliver(ViewRootImpl.java:3924)
  at android.view.ViewRootImpl.deliverInputEvent(ViewRootImpl.java:6334)
  at android.view.ViewRootImpl.doProcessInputEvents(ViewRootImpl.java:6308)
  at android.view.ViewRootImpl.enqueueInputEvent(ViewRootImpl.java:6269)
  at android.view.ViewRootImpl$WindowInputEventReceiver.onInputEvent(ViewRootImpl.java:6440)
  at android.view.InputEventReceiver.dispatchInputEvent(InputEventReceiver.java:187)
  at android.os.MessageQueue.nativePollOnce(Native method)
  at android.os.MessageQueue.next(MessageQueue.java:323)
  at android.os.Looper.loop(Looper.java:142)
  at android.app.ActivityThread.main(ActivityThread.java:6381)
  at java.lang.reflect.Method.invoke!(Native method)
  at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:901)
  at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:791)

"pool-5-thread-1" prio=5 tid=141 Native
  | group="main" sCount=2 dsCount=0 obj=0x13eec700 self=0xc2752a00
  | sysTid=8269 nice=0 cgrp=default sched=0/0 handle=0xc19be920
  | state=D schedstat=( 0 0 0 ) utm=5 stm=16 core=4 HZ=100
  | stack=0xc18bc000-0xc18be000 stackSize=1038KB
  | held mutexes=
  kernel: __switch_to+0x88/0x94
  kernel: lock_rename+0x9c/0xb0
  kernel: SyS_renameat2+0x178/0x3b4
  kernel: SyS_renameat+0x10/0x18
  kernel: el0_svc_naked+0x24/0x28
  native: (backtrace::Unwind failed for thread 8269: Thread has not repsonded to signal in time)
  at java.io.UnixFileSystem.rename0(Native method)
  at java.io.UnixFileSystem.rename(UnixFileSystem.java:322)
  at java.io.File.renameTo(File.java:1326)
  at com.facebook.common.file.FileUtils.rename(FileUtils.java:63)
  at com.facebook.cache.disk.DefaultDiskStorage$InserterImpl.commit(DefaultDiskStorage.java:702)
  at com.facebook.cache.disk.DiskStorageCache.endInsert(DiskStorageCache.java:347)
  - locked <0x04051433> (a java.lang.Object)
  at com.facebook.cache.disk.DiskStorageCache.insert(DiskStorageCache.java:373)
  at com.facebook.imagepipeline.cache.BufferedDiskCache.writeToDiskCache(BufferedDiskCache.java:367)
  at com.facebook.imagepipeline.cache.BufferedDiskCache.access$500(BufferedDiskCache.java:38)
  at com.facebook.imagepipeline.cache.BufferedDiskCache$3.run(BufferedDiskCache.java:246)
  at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1133)
  at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:607)
  at java.lang.Thread.run(Thread.java:760)
```

## 四.调试RAMDUMP:

### 1.D状态的所有进程:

```
crash64> ps | grep UN
     82      2   2  ffffffc05576de00  UN   0.0       0      0  [kworker/u16:1]
    171      2   0  ffffffc0b4908bc0  UN   0.0       0      0  [mdss_dsi_event]
    510      2   1  ffffffc170e21780  UN   0.0       0      0  [mdss_dp_event]
    541      2   3  ffffffc0b1df69c0  UN   0.0       0      0  [msm-core:sampli]
   1070      2   3  ffffffc08572bac0  UN   0.0       0      0  [mdss_fb0]
   5200    824   0  ffffffc091108000  UN   0.9 1994712  62532  Thread-8
   5223    824   4  ffffffc14c098000  UN   1.7 1985888 123532  obike.mobikeapp
   8495   8517   4  ffffffc08d0f8000  UN   1.7 1987672 125392  obike.mobikeapp
```

### 2.先找锁的地址 mutex_lock:

```
crash64> bt 5223
PID: 5223   TASK: ffffffc14c098000  CPU: 4   COMMAND: "obike.mobikeapp"
 #0 [ffffffc08d01fc50] __switch_to at ffffff8008085538
 #1 [ffffffc08d01fc80] __schedule at ffffff8008f0ce1c
 #2 [ffffffc08d01fce0] schedule at ffffff8008f0d16c
 #3 [ffffffc08d01fd00] schedule_preempt_disabled at ffffff8008f0d4a8
 #4 [ffffffc08d01fd20] __mutex_lock_slowpath at ffffff8008f0e9e0
 #5 [ffffffc08d01fd80] mutex_lock at ffffff8008f0ea70
 #6 [ffffffc08d01fda0] lock_rename at ffffff80081bd454
 #7 [ffffffc08d01fdd0] sys_renameat2 at ffffff80081c070c
 #8 [ffffffc08d01feb0] sys_renameat at ffffff80081c0958
 #9 [ffffffc08d01fed0] el0_svc_naked at ffffff800808462c
     PC: 00000000ffffff9c   LR: 00000000f32ab040   SP: 00000000800f0010
    X29: 0000000000000000  X28: 0000000000000000  X27: 0000000000000000
    X26: 0000000000000000  X25: 0000000000000000  X24: 0000000000000000
    X23: 0000000000000000  X22: 0000000000000000  X21: 0000000000000000
    X20: 0000000000000000  X19: 0000000000000000  X18: 0000000000000000
    X17: 0000000000000000  X16: 0000000000000000  X15: 0000000000000000
    X14: 0000000000000000  X13: 0000000000000000  X12: 00000000cc2da18b
    X11: 00000000ffcdc738  X10: 000000000000005c   X9: 00000000f2585400
     X8: 00000000ffcdced0   X7: 00000000f2585400   X6: 00000000ffcdd010
     X5: 0000000000000149   X4: 00000000ffcdc74c   X3: 00000000ffcdc744
     X2: 00000000f32ee008   X1: 00000000ffcdc94c   X0: 00000000ffffff9c
    ORIG_X0: 0000000000000000  SYSCALLNO: 0  PSTATE: 00000149
```

找到lock_rename block住的位置

```
$ aarch64-linux-android-addr2line -e vmlinux ffffff80081bd454
/home/work/sagit-n-alpha-build/kernel/msm-4.4/fs/namei.c:2643
```

* lock_rename

```
/* 2632 * p1 and p2 should be directories on the same fs. 2633 */
2634struct dentry *lock_rename(struct dentry *p1, struct dentry *p2)
2635{
2636	struct dentry *p;
2637
2638	if (p1 == p2) {
2639		mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_PARENT);
2640		return NULL;
2641	}
2642
2643	mutex_lock(&p1->d_inode->i_sb->s_vfs_rename_mutex);  <== block在这个位置
2644
2645	p = d_ancestor(p2, p1);
2646	if (p) {
2647		mutex_lock_nested(&p2->d_inode->i_mutex, I_MUTEX_PARENT);
2648		mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_CHILD);
2649		return p;
2650	}
2651
2652	p = d_ancestor(p1, p2);
2653	if (p) {
2654		mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_PARENT);
2655		mutex_lock_nested(&p2->d_inode->i_mutex, I_MUTEX_CHILD);
2656		return p;
2657	}
2658
2659	mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_PARENT);
2660	mutex_lock_nested(&p2->d_inode->i_mutex, I_MUTEX_PARENT2);
2661	return NULL;
2662}
2663EXPORT_SYMBOL(lock_rename);
```
先找出这个锁的地址 mutex_lock:

```
66#ifndef CONFIG_DEBUG_LOCK_ALLOC
67/* 68 * We split the mutex lock/unlock logic into separate fastpath and 69 * slowpath functions, to reduce the register pressure on the fastpath. 70 * We also put the fastpath first in the kernel image, to make sure the 71 * branch is predicted by the CPU as default-untaken. 72 */
73__visible void __sched __mutex_lock_slowpath(atomic_t *lock_count);
74
75/** 76 * mutex_lock - acquire the mutex 77 * @lock: the mutex to be acquired 78 * 79 * Lock the mutex exclusively for this task. If the mutex is not 80 * available right now, it will sleep until it can get it. 81 * 82 * The mutex must later on be released by the same task that 83 * acquired it. Recursive locking is not allowed. The task 84 * may not exit without first unlocking the mutex. Also, kernel 85 * memory where the mutex resides must not be freed with 86 * the mutex still locked. The mutex must first be initialized 87 * (or statically defined) before it can be locked. memset()-ing 88 * the mutex to 0 is not allowed. 89 * 90 * ( The CONFIG_DEBUG_MUTEXES .config option turns on debugging 91 * checks that will enforce the restrictions and will also do 92 * deadlock debugging. ) 93 * 94 * This function is similar to (but not equivalent to) down(). 95 */
96void __sched mutex_lock(struct mutex *lock)
97{
98	might_sleep();
99	/* 100 * The locking fastpath is the 1->0 transition from 101 * 'unlocked' into 'locked' state. 102 */
103	__mutex_fastpath_lock(&lock->count, __mutex_lock_slowpath);
104	mutex_set_owner(lock);
105}
106
107EXPORT_SYMBOL(mutex_lock);
108#endif
```

反汇编:

```
crash64> p mutex_lock
mutex_lock = $2 =
 {void (struct mutex *)} 0xffffff8008f0ea48 <mutex_lock>
crash64> dis mutex_lock
0xffffff8008f0ea48 <mutex_lock>:        stp     x29, x30, [sp,#-32]!
0xffffff8008f0ea4c <mutex_lock+4>:      mov     x29, sp
0xffffff8008f0ea50 <mutex_lock+8>:      str     x19, [sp,#16]
0xffffff8008f0ea54 <mutex_lock+12>:     mov     x19, x0
0xffffff8008f0ea58 <mutex_lock+16>:     prfm    pstl1strm, [x0]
0xffffff8008f0ea5c <mutex_lock+20>:     ldaxr   w1, [x0]
0xffffff8008f0ea60 <mutex_lock+24>:     sub     w1, w1, #0x1
0xffffff8008f0ea64 <mutex_lock+28>:     stxr    w2, w1, [x0]
0xffffff8008f0ea68 <mutex_lock+32>:     cbnz    w2, 0xffffff8008f0ea5c <mutex_lock+20>
0xffffff8008f0ea6c <mutex_lock+36>:     tbz     w1, #31, 0xffffff8008f0ea74 <mutex_lock+44>
0xffffff8008f0ea70 <mutex_lock+40>:     bl      0xffffff8008f0e8f8 <__mutex_lock_slowpath>
0xffffff8008f0ea74 <mutex_lock+44>:     mrs     x0, sp_el0
0xffffff8008f0ea78 <mutex_lock+48>:     ldr     x0, [x0,#16]
0xffffff8008f0ea7c <mutex_lock+52>:     str     x0, [x19,#24]
0xffffff8008f0ea80 <mutex_lock+56>:     ldr     x19, [sp,#16]
0xffffff8008f0ea84 <mutex_lock+60>:     ldp     x29, x30, [sp],#32
0xffffff8008f0ea88 <mutex_lock+64>:     ret
```

mutex_lock的第一个参数就是我们要找的struct mutex，在函数入口处被保存在x19寄存器中：
```
0xffffff8008f0ea54 <mutex_lock+12>: mov x19, x0
```

下一步会调用__mutex_lock_slowpath：
```
0xffffff8008f0ea70 <mutex_lock+40>: bl 0xffffff8008f0e8f8 <__mutex_lock_slowpath>
```

一般x19寄存器会保存在下一级函数的栈帧中，比如

```
crash64> dis __mutex_lock_slowpath
0xffffff8008f0e8f8 <__mutex_lock_slowpath>:     stp     x29, x30, [sp,#-96]!
0xffffff8008f0e8fc <__mutex_lock_slowpath+4>:   mov     x29, sp
0xffffff8008f0e900 <__mutex_lock_slowpath+8>:   stp     x19, x20, [sp,#16]
0xffffff8008f0e904 <__mutex_lock_slowpath+12>:  stp     x21, x22, [sp,#32]
0xffffff8008f0e908 <__mutex_lock_slowpath+16>:  stp     x23, x24, [sp,#48]
0xffffff8008f0e90c <__mutex_lock_slowpath+20>:  mrs     x20, sp_el0
0xffffff8008f0e910 <__mutex_lock_slowpath+24>:  ldr     w1, [x20,#24]
0xffffff8008f0e914 <__mutex_lock_slowpath+28>:  mov     x19, x0
```

因此，我们只需要在__mutex_lock_slowpath的栈帧中找到x19，它就是我们要找的struct mutex了。
通过bt的内容可知，__mutex_lock_slowpath的栈帧范围是ffffffc08d01fd20 ~ ffffffc08d01fd80

```
#4 [ffffffc08d01fd20] __mutex_lock_slowpath at ffffff8008f0e9e0
#5 [ffffffc08d01fd80] mutex_lock at ffffff8008f0ea70
```

通过rd命令查看这个地址：
```
crash64> rd ffffffc08d01fd20 -e ffffffc08d01fd80
                  x29                    x30
ffffffc08d01fd20: ffffffc08d01fd80 ffffff8008f0ea74 ........t.......
                  x19                    x20
ffffffc08d01fd30: ffffffc1479dbc70 ffffffc082c1c480 p..G............
                  x21                     x22
ffffffc08d01fd40: ffffffc082c1ca80 0000000000000000 ................
                  x23                     x24
ffffffc08d01fd50: ffffffc13fb21000 ffffffc13fb24000 ...?.....@.?....
ffffffc08d01fd60: ffffffc0989f2000 ffffffc14d1e7d68 . ......h}.M....
ffffffc08d01fd70: ffffffc1479dbc78 ffffffc14c098000 x..G.......L....
```

我们要找的struct mutex就是x19，也就是ffffffc1479dbc70

```
crash64> struct mutex ffffffc1479dbc70
struct mutex {
  count = {
    counter = -2
  },
  wait_lock = {
    {
      rlock = {
        raw_lock = {
          owner = 2,
          next = 2
        }
      }
    }
  },
  wait_list = {
    next = 0xffffffc08d01fd68,
    prev = 0xffffffc14d1e7d68
  },
  owner = 0xffffffc091108000,
  osq = {
    tail = {
      counter = 0
    }
  }
}
```

其中onwer就是持有该所的线程的task_struct指针。它的pid为：

```
crash64> task_struct.pid 0xffffffc091108000
  pid = 5200
crash64> ps -g 5200
PID: 5062   TASK: ffffffc14c8b69c0  CPU: 0   COMMAND: "ipayGphone:push"
  PID: 5200   TASK: ffffffc091108000  CPU: 0   COMMAND: "Thread-8"
```

5200进程的stack trace

```
crash64> bt 5200
PID: 5200   TASK: ffffffc091108000  CPU: 0   COMMAND: "Thread-8"
 #0 [ffffffc091b5bc50] __switch_to at ffffff8008085538
 #1 [ffffffc091b5bc80] __schedule at ffffff8008f0ce1c
 #2 [ffffffc091b5bce0] schedule at ffffff8008f0d16c
 #3 [ffffffc091b5bd00] schedule_preempt_disabled at ffffff8008f0d4a8
 #4 [ffffffc091b5bd20] __mutex_lock_slowpath at ffffff8008f0e9e0
 #5 [ffffffc091b5bd80] mutex_lock at ffffff8008f0ea70
 #6 [ffffffc091b5bda0] lock_rename at ffffff80081bd4c0
 #7 [ffffffc091b5bdd0] sys_renameat2 at ffffff80081c070c
 #8 [ffffffc091b5beb0] sys_renameat at ffffff80081c0958
 #9 [ffffffc091b5bed0] el0_svc_naked at ffffff800808462c
     PC: 00000000ffffff9c   LR: 00000000f32ab040   SP: 00000000a0030010
    X29: 0000000000000000  X28: 0000000000000000  X27: 0000000000000000
    X26: 0000000000000000  X25: 0000000000000000  X24: 0000000000000000
    X23: 0000000000000000  X22: 0000000000000000  X21: 0000000000000000
    X20: 0000000000000000  X19: 0000000000000000  X18: 0000000000000000
    X17: 0000000000000000  X16: 0000000000000000  X15: 0000000000000000
    X14: 0000000000000000  X13: 0000000000000000  X12: 00000000ec54ac33
    X11: 00000000cacb4090  X10: 0000000000000000   X9: 0000000012ec4880
     X8: 000000007100ed24   X7: 00000000ea98c3c0   X6: 00000000ea9a73f0
     X5: 0000000000000149   X4: 0000000000200009   X3: 0000000000000005
     X2: 00000000f2555670   X1: 00000000ea98c3c0   X0: 00000000ffffff9c
    ORIG_X0: 0000000000000000  SYSCALLNO: 0  PSTATE: 00000149
```

找到被锁住的位置:

```
$ aarch64-linux-android-addr2line -e vmlinux ffffff80081bd4c0
/home/work/sagit-n-alpha-build/kernel/msm-4.4/fs/namei.c:2660

/* 2632 * p1 and p2 should be directories on the same fs. 2633 */
2634struct dentry *lock_rename(struct dentry *p1, struct dentry *p2)
2635{
2636	struct dentry *p;
2637
2638	if (p1 == p2) {
2639		mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_PARENT);
2640		return NULL;
2641	}
2642
2643	mutex_lock(&p1->d_inode->i_sb->s_vfs_rename_mutex);
2644
2645	p = d_ancestor(p2, p1);
2646	if (p) {
2647		mutex_lock_nested(&p2->d_inode->i_mutex, I_MUTEX_PARENT);
2648		mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_CHILD);
2649		return p;
2650	}
2651
2652	p = d_ancestor(p1, p2);
2653	if (p) {
2654		mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_PARENT);
2655		mutex_lock_nested(&p2->d_inode->i_mutex, I_MUTEX_CHILD);
2656		return p;
2657	}
2658
2659	mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_PARENT);
2660	mutex_lock_nested(&p2->d_inode->i_mutex, I_MUTEX_PARENT2); <== block在这个位置
2661	return NULL;
2662}
2663EXPORT_SYMBOL(lock_rename);
```

### 3.找出摩拜单车和支付宝分别操作的文件名称:
这里我们再来一次栈推导，算出dentry的值：首先，查看lock_rename的汇编代码：

```
crash64> dis lock_rename
0xffffff80081bd428 <lock_rename>:       stp     x29, x30, [sp,#-48]!
0xffffff80081bd42c <lock_rename+4>:     cmp     x0, x1
0xffffff80081bd430 <lock_rename+8>:     mov     x29, sp
0xffffff80081bd434 <lock_rename+12>:    stp     x19, x20, [sp,#16]
0xffffff80081bd438 <lock_rename+16>:    str     x21, [sp,#32]
0xffffff80081bd43c <lock_rename+20>:    mov     x20, x1
0xffffff80081bd440 <lock_rename+24>:    mov     x21, x0
0xffffff80081bd444 <lock_rename+28>:    ldr     x0, [x0,#48]
0xffffff80081bd448 <lock_rename+32>:    b.eq    0xffffff80081bd4bc <lock_rename+148>
0xffffff80081bd44c <lock_rename+36>:    ldr     x0, [x0,#40]
0xffffff80081bd450 <lock_rename+40>:    add     x0, x0, #0x470
0xffffff80081bd454 <lock_rename+44>:    bl      0xffffff8008f0ea48 <mutex_lock>
0xffffff80081bd458 <lock_rename+48>:    mov     x0, x20
0xffffff80081bd45c <lock_rename+52>:    mov     x1, x21
0xffffff80081bd460 <lock_rename+56>:    bl      0xffffff80081c6e6c <d_ancestor>
0xffffff80081bd464 <lock_rename+60>:    mov     x19, x0
0xffffff80081bd468 <lock_rename+64>:    cbz     x0, 0xffffff80081bd480 <lock_rename+88>
0xffffff80081bd46c <lock_rename+68>:    ldr     x0, [x20,#48]
0xffffff80081bd470 <lock_rename+72>:    add     x0, x0, #0xa8
0xffffff80081bd474 <lock_rename+76>:    bl      0xffffff8008f0ea48 <mutex_lock>
0xffffff80081bd478 <lock_rename+80>:    ldr     x0, [x21,#48]
0xffffff80081bd47c <lock_rename+84>:    b       0xffffff80081bd4a4 <lock_rename+124>
0xffffff80081bd480 <lock_rename+88>:    mov     x0, x21
0xffffff80081bd484 <lock_rename+92>:    mov     x1, x20
0xffffff80081bd488 <lock_rename+96>:    bl      0xffffff80081c6e6c <d_ancestor>
0xffffff80081bd48c <lock_rename+100>:   mov     x19, x0
0xffffff80081bd490 <lock_rename+104>:   ldr     x0, [x21,#48]
0xffffff80081bd494 <lock_rename+108>:   add     x0, x0, #0xa8
0xffffff80081bd498 <lock_rename+112>:   cbz     x19, 0xffffff80081bd4b4 <lock_rename+140>
0xffffff80081bd49c <lock_rename+116>:   bl      0xffffff8008f0ea48 <mutex_lock>
0xffffff80081bd4a0 <lock_rename+120>:   ldr     x0, [x20,#48]
0xffffff80081bd4a4 <lock_rename+124>:   add     x0, x0, #0xa8
0xffffff80081bd4a8 <lock_rename+128>:   bl      0xffffff8008f0ea48 <mutex_lock>
0xffffff80081bd4ac <lock_rename+132>:   mov     x0, x19
0xffffff80081bd4b0 <lock_rename+136>:   b       0xffffff80081bd4c8 <lock_rename+160>
0xffffff80081bd4b4 <lock_rename+140>:   bl      0xffffff8008f0ea48 <mutex_lock>
0xffffff80081bd4b8 <lock_rename+144>:   ldr     x0, [x20,#48]
0xffffff80081bd4bc <lock_rename+148>:   add     x0, x0, #0xa8
0xffffff80081bd4c0 <lock_rename+152>:   bl      0xffffff8008f0ea48 <mutex_lock>
0xffffff80081bd4c4 <lock_rename+156>:   mov     x0, #0x0                        // #0
0xffffff80081bd4c8 <lock_rename+160>:   ldp     x19, x20, [sp,#16]
0xffffff80081bd4cc <lock_rename+164>:   ldr     x21, [sp,#32]
0xffffff80081bd4d0 <lock_rename+168>:   ldp     x29, x30, [sp],#48
0xffffff80081bd4d4 <lock_rename+172>:   ret
```

对于mobike, 第一个参数struct dentry的值x0，保存在x20寄存器中, 第二个参数struct dentry的值x1，保存在x21寄存器中。
0xffffff80081bd454行中mutex_lock函数之前x20,x21一直没变过。因此我们查看mutex_lock的栈帧中找x20,下1。

```
crash64> dis mutex_lock
0xffffff8008f0ea48 <mutex_lock>:        stp     x29, x30, [sp,#-32]!
0xffffff8008f0ea4c <mutex_lock+4>:      mov     x29, sp
0xffffff8008f0ea50 <mutex_lock+8>:      str     x19, [sp,#16]
0xffffff8008f0ea54 <mutex_lock+12>:     mov     x19, x0
0xffffff8008f0ea58 <mutex_lock+16>:     prfm    pstl1strm, [x0]
0xffffff8008f0ea5c <mutex_lock+20>:     ldaxr   w1, [x0]
0xffffff8008f0ea60 <mutex_lock+24>:     sub     w1, w1, #0x1
0xffffff8008f0ea64 <mutex_lock+28>:     stxr    w2, w1, [x0]
0xffffff8008f0ea68 <mutex_lock+32>:     cbnz    w2, 0xffffff8008f0ea5c <mutex_lock+20>
0xffffff8008f0ea6c <mutex_lock+36>:     tbz     w1, #31, 0xffffff8008f0ea74 <mutex_lock+44>
0xffffff8008f0ea70 <mutex_lock+40>:     bl      0xffffff8008f0e8f8 <__mutex_lock_slowpath>
0xffffff8008f0ea74 <mutex_lock+44>:     mrs     x0, sp_el0
0xffffff8008f0ea78 <mutex_lock+48>:     ldr     x0, [x0,#16]
0xffffff8008f0ea7c <mutex_lock+52>:     str     x0, [x19,#24]
0xffffff8008f0ea80 <mutex_lock+56>:     ldr     x19, [sp,#16]
0xffffff8008f0ea84 <mutex_lock+60>:     ldp     x29, x30, [sp],#32
0xffffff8008f0ea88 <mutex_lock+64>:     ret
```

在mutex_lock的栈帧中并没有找到x20,x21的操作,解析来我们到__mutex_lock_slowpath的栈帧中去找:

```
crash64> dis __mutex_lock_slowpath
0xffffff8008f0e8f8 <__mutex_lock_slowpath>:     stp     x29, x30, [sp,#-96]!
0xffffff8008f0e8fc <__mutex_lock_slowpath+4>:   mov     x29, sp
0xffffff8008f0e900 <__mutex_lock_slowpath+8>:   stp     x19, x20, [sp,#16]
0xffffff8008f0e904 <__mutex_lock_slowpath+12>:  stp     x21, x22, [sp,#32]
0xffffff8008f0e908 <__mutex_lock_slowpath+16>:  stp     x23, x24, [sp,#48]
0xffffff8008f0e90c <__mutex_lock_slowpath+20>:  mrs     x20, sp_el0
```

通过rd命令查看x20,x21保存在mobike(5223)进程内存栈中的地址：

```
crash64> rd ffffffc08d01fd20 -e ffffffc08d01fd80
                  x29              x30
ffffffc08d01fd20: ffffffc08d01fd80 ffffff8008f0ea74 ........t.......
                  x19              x20
ffffffc08d01fd30: ffffffc1479dbc70 ffffffc082c1c480 p..G............
                  x21              x22
ffffffc08d01fd40: ffffffc082c1ca80 0000000000000000 ................
                  x23              x24
ffffffc08d01fd50: ffffffc13fb21000 ffffffc13fb24000 ...?.....@.?....
ffffffc08d01fd60: ffffffc0989f2000 ffffffc14d1e7d68 . ......h}.M....
ffffffc08d01fd70: ffffffc1479dbc78 ffffffc14c098000 x..G.......L....
```

内存地址ffffffc082c1c480和ffffffc082c1ca80保存的就是mobike rename的文件,如下:

```
crash64> struct dentry ffffffc082c1c480
struct dentry {
  ...
  d_parent = 0xffffffc082c1c3c0,
  d_name = {
    {
      {
        hash = 18370,
        len = 1
      },
      hash_len = 4294985666
    },
    name = 0xffffffc082c1c4b8 "h"
  },
  d_inode = 0xffffffc07a2b1218,
  d_iname = "h\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000",
  ...
}
```

通过d_parent我们可以推导出全路径,如下:

```
new_path == p2  == x20
crash64> struct dentry.d_parent,d_name.name ffffffc082c1c480
  d_parent = 0xffffffc082c1c3c0
  d_name.name = 0xffffffc082c1c4b8 "h"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c3c0
  d_parent = 0xffffffc082c1c300
  d_name.name = 0xffffffc082c1c3f8 "vmp"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c300
  d_parent = 0xffffffc082c1c240
  d_name.name = 0xffffffc082c1c338 "BaiduMapSDKNew"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c240
  d_parent = 0xffffffc082c1c180
  d_name.name = 0xffffffc082c1c278 "files"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c180
  d_parent = 0xffffffc082c1c000
  d_name.name = 0xffffffc082c1c1b8 "com.mobike.mobikeapp"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c000
  d_parent = 0xffffffc082c17e40
  d_name.name = 0xffffffc082c1c038 "data"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c17e40
  d_parent = 0xffffffc085da7840
  d_name.name = 0xffffffc082c17e78 "Android"
crash64> struct dentry.d_parent,d_name.name 0xffffffc085da7840
  d_parent = 0xffffffc1441ed780
  d_name.name = 0xffffffc085da7878 "0"
crash64> struct dentry.d_parent,d_name.name 0xffffffc1441ed780
  d_parent = 0xffffffc1441ed780
  d_name.name = 0xffffffc1441ed7b8 "/"
crash64> struct dentry.d_parent,d_name.name 0xffffffc1441ed780
  d_parent = 0xffffffc1441ed780
  d_name.name = 0xffffffc1441ed7b8 "/"

new_path == p1 == x21
crash64> struct dentry.d_parent,d_name.name ffffffc082c1ca80
  d_parent = 0xffffffc082c1c9c0
  d_name.name = 0xffffffc082c1cab8 "vmp"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c9c0
  d_parent = 0xffffffc082c1c900
  d_name.name = 0xffffffc082c1c9f8 "BaiduMapSDKNew"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c900
  d_parent = 0xffffffc082c1c840
  d_name.name = 0xffffffc082c1c938 "files"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c840
  d_parent = 0xffffffc082c1c6c0
  d_name.name = 0xffffffc082c1c878 "com.mobike.mobikeapp"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c6c0
  d_parent = 0xffffffc082c1c540
  d_name.name = 0xffffffc082c1c6f8 "data"
crash64> struct dentry.d_parent,d_name.name 0xffffffc082c1c540
  d_parent = 0xffffffc085da7840
  d_name.name = 0xffffffc082c1c578 "Android"
crash64> struct dentry.d_parent,d_name.name 0xffffffc085da7840
  d_parent = 0xffffffc1441ed780
  d_name.name = 0xffffffc085da7878 "0"
crash64> struct dentry.d_parent,d_name.name 0xffffffc1441ed780
  d_parent = 0xffffffc1441ed780
  d_name.name = 0xffffffc1441ed7b8 "/"
```

old_path 也就是lock_rename p1描述的文件就是 "/0/Android/data/com.mobike.mobikeapp/files/BaiduMapSDKNew/vmp/h" , 从手机上我们可以看到文件:
new_path 也就是lock_rename p2描述的文件就是 "/0/Android/data/com.mobike.mobikeapp/files/BaiduMapSDKNew/vmp" , 从手机上我们可以看到文件:

```
sagit:/ # ls /sdcard/Android/data/com.mobike.mobikeapp/files/BaiduMapSDKNew/vmp/
DVUserdat.cfg beijing_131.dat_seg h hangzhou_179.dat
```

经过查证上下文代码,这个只是rename的只是需要重名文件的上一级目录,我们需要找到对应的文件名, 首先反汇编sys_renameat

```
crash64> dis sys_renameat2
0xffffff80081c0598 <sys_renameat2>:     stp     x29, x30, [sp,#-224]!
0xffffff80081c059c <sys_renameat2+4>:   tst     w4, #0xfffffff8
0xffffff80081c05a0 <sys_renameat2+8>:   mov     x29, sp
0xffffff80081c05a4 <sys_renameat2+12>:  stp     x21, x22, [sp,#32]
0xffffff80081c05a8 <sys_renameat2+16>:  stp     x27, x28, [sp,#80]
0xffffff80081c05ac <sys_renameat2+20>:  str     w0, [x29,#140]
0xffffff80081c05b0 <sys_renameat2+24>:  mov     x28, x3
0xffffff80081c05b4 <sys_renameat2+28>:  stp     x19, x20, [sp,#16]
0xffffff80081c05b8 <sys_renameat2+32>:  stp     x23, x24, [sp,#48]
0xffffff80081c05bc <sys_renameat2+36>:  stp     x25, x26, [sp,#64]
0xffffff80081c05c0 <sys_renameat2+40>:  str     x1, [x29,#112]
...
```

其中x1和x3保存分别保存的就是oldname和newname, X1很容易找出,其就在renameat2栈帧中的距离栈底112(0x70)偏移位置处

```
crash64> set 5223 // 我们这里切换到当前进程的目的是为了使页表对应上,这样的目的是为了方便虚拟地址到物理地址的映射,我们就直接能够读取虚拟地址了
    PID: 5223
COMMAND: "obike.mobikeapp"
   TASK: ffffffc14c098000  [THREAD_INFO: ffffffc08d01c000]
    CPU: 4
  STATE: TASK_UNINTERRUPTIBLE
crash64> bt
PID: 5223   TASK: ffffffc14c098000  CPU: 4   COMMAND: "obike.mobikeapp"
 #0 [ffffffc08d01fc50] __switch_to at ffffff8008085538
 #1 [ffffffc08d01fc80] __schedule at ffffff8008f0ce1c
 #2 [ffffffc08d01fce0] schedule at ffffff8008f0d16c
 #3 [ffffffc08d01fd00] schedule_preempt_disabled at ffffff8008f0d4a8
 #4 [ffffffc08d01fd20] __mutex_lock_slowpath at ffffff8008f0e9e0
 #5 [ffffffc08d01fd80] mutex_lock at ffffff8008f0ea70
 #6 [ffffffc08d01fda0] lock_rename at ffffff80081bd454
 #7 [ffffffc08d01fdd0] sys_renameat2 at ffffff80081c070c
 #8 [ffffffc08d01feb0] sys_renameat at ffffff80081c0958
 #9 [ffffffc08d01fed0] el0_svc_naked at ffffff800808462c
     PC: 00000000ffffff9c   LR: 00000000f32ab040   SP: 00000000800f0010
    X29: 0000000000000000  X28: 0000000000000000  X27: 0000000000000000
    X26: 0000000000000000  X25: 0000000000000000  X24: 0000000000000000
    X23: 0000000000000000  X22: 0000000000000000  X21: 0000000000000000
    X20: 0000000000000000  X19: 0000000000000000  X18: 0000000000000000
    X17: 0000000000000000  X16: 0000000000000000  X15: 0000000000000000
    X14: 0000000000000000  X13: 0000000000000000  X12: 00000000cc2da18b
    X11: 00000000ffcdc738  X10: 000000000000005c   X9: 00000000f2585400
     X8: 00000000ffcdced0   X7: 00000000f2585400   X6: 00000000ffcdd010
     X5: 0000000000000149   X4: 00000000ffcdc74c   X3: 00000000ffcdc744
     X2: 00000000f32ee008   X1: 00000000ffcdc94c   X0: 00000000ffffff9c
    ORIG_X0: 0000000000000000  SYSCALLNO: 0  PSTATE: 00000149
crash64> rd ffffffc08d01fdd0 -e ffffffc08d01feb0
ffffffc08d01fdd0:  ffffffc08d01feb0 ffffff80081c095c   ........\.......
ffffffc08d01fde0:  0000000000400000 0000000000000000   ..@.............
ffffffc08d01fdf0:  ffffffffffffffff 00000000f32ab040   ........@.*.....
ffffffc08d01fe00:  00000000800f0010 0000000000000011   ................
ffffffc08d01fe10:  0000000000000186 0000000000000149   ........I.......
ffffffc08d01fe20:  ffffff8009004000 ffffffc08d01c000   .@..............
ffffffc08d01fe30:  ffffffc14c098000 ffffffc14c09865c   ...L....\..L....
                   0x70 (X1寄存器值)
ffffffc08d01fe40:  00000000ffcdc74c 0000080000000800   L...............
ffffffc08d01fe50:  0000000000000000 ffffff9cffffff9c   ................
ffffffc08d01fe60:  0000000000000000 0000000000000000   ................
ffffffc08d01fe70:  ffffffc13352e920 ffffffc082c1c480    .R3............
ffffffc08d01fe80:  ffffffc13352e920 ffffffc082c1ca80    .R3............
ffffffc08d01fe90:  0000000d29f53f22 ffffffc13fb2106d   "?.)....m..?....
ffffffc08d01fea0:  0000000d29f53f22 ffffffc13fb2406b   "?.)....k@.?....

crash64> rd 00000000ffcdc74c -e 00000000ffcdc7ac
        ffcdc74c:  656761726f74732f 6574616c756d652f   /storage/emulate
        ffcdc75c:  72646e412f302f64 617461642f64696f   d/0/Android/data
        ffcdc76c:  626f6d2e6d6f632f 69626f6d2e656b69   /com.mobike.mobi
        ffcdc77c:  69662f707061656b 646961422f73656c   keapp/files/Baid
        ffcdc78c:  4e4b445370614d75 682f706d762f7765   uMapSDKNew/vmp/h
        ffcdc79c:  6c6966695756442f 00006766632e676f   /DVWifilog.cfg..
```

从sys_renameat2栈中我们发现x3寄存器值保存到了x28寄存器中, 但是我们在sys_renameat2栈帧中没有发现x28寄存器入栈操作, 我们一级级函数都没有发现有入栈操作,
但是最顶层的栈是函数__switch_to, 则说明做了线程切换,线程切换的话,当前线程上下文会被保存到当前task的thread_info结构中, 如下:

```
crash64> task_struct.thread ffffffc14c098000
  thread = {
    cpu_context = {
      x19 = 18446743804402302976,
      x20 = 18446743805232046336,
      x21 = 18446743805113550464,
      x22 = 18446743801154665472,
      x23 = 18446743524115791872,
      x24 = 18446743524103737712,
      x25 = 0,
      x26 = 18446743804402304296,
      x27 = 4,
      x28 = 4291676492,
      fp = 18446743801197362256,
      sp = 18446743801197362256,
      pc = 18446743524088501564
    },
```

我们很容易确认newname就是

```
crash64> x/g 4291676492
0xffcdc94c:     0x656761726f74732f
crash64> rd 0xffcdc94c
        ffcdc94c:  656761726f74732f                    /storage
crash64> rd 0xffcdc94c -e 0xffcdc9ac
        ffcdc94c:  656761726f74732f 6574616c756d652f   /storage/emulate
        ffcdc95c:  72646e412f302f64 617461642f64696f   d/0/Android/data
        ffcdc96c:  626f6d2e6d6f632f 69626f6d2e656b69   /com.mobike.mobi
        ffcdc97c:  69662f707061656b 646961422f73656c   keapp/files/Baid
        ffcdc98c:  4e4b445370614d75 442f706d762f7765   uMapSDKNew/vmp/D
        ffcdc99c:  676f6c6966695756 000000006766632e   VWifilog.cfg....
```

综上就是,  /storage/emulated/0/Android/data/com.mobike.mobikeapp/files/BaiduMapSDKNew/vmp/h/DVMifilog.cfg =rename=> rename /storage/emulated/0/Android/data/com.mobike.mobikeapp/files/BaiduMapSDKNew/vmp/DVMifilog.cfg

类似地我们可以通过rd命令找到5200线程在rename文件的名称, 也就是x20,x21保存在内存栈中的地址：

```
crash64> bt 5200
PID: 5200   TASK: ffffffc091108000  CPU: 0   COMMAND: "Thread-8"
 #0 [ffffffc091b5bc50] __switch_to at ffffff8008085538
 #1 [ffffffc091b5bc80] __schedule at ffffff8008f0ce1c
 #2 [ffffffc091b5bce0] schedule at ffffff8008f0d16c
 #3 [ffffffc091b5bd00] schedule_preempt_disabled at ffffff8008f0d4a8
 #4 [ffffffc091b5bd20] __mutex_lock_slowpath at ffffff8008f0e9e0
 #5 [ffffffc091b5bd80] mutex_lock at ffffff8008f0ea70
 #6 [ffffffc091b5bda0] lock_rename at ffffff80081bd4c0
 #7 [ffffffc091b5bdd0] sys_renameat2 at ffffff80081c070c
 #8 [ffffffc091b5beb0] sys_renameat at ffffff80081c0958
 #9 [ffffffc091b5bed0] el0_svc_naked at ffffff800808462c
     PC: 00000000ffffff9c   LR: 00000000f32ab040   SP: 00000000a0030010
    X29: 0000000000000000  X28: 0000000000000000  X27: 0000000000000000
    X26: 0000000000000000  X25: 0000000000000000  X24: 0000000000000000
    X23: 0000000000000000  X22: 0000000000000000  X21: 0000000000000000
    X20: 0000000000000000  X19: 0000000000000000  X18: 0000000000000000
    X17: 0000000000000000  X16: 0000000000000000  X15: 0000000000000000
    X14: 0000000000000000  X13: 0000000000000000  X12: 00000000ec54ac33
    X11: 00000000cacb4090  X10: 0000000000000000   X9: 0000000012ec4880
     X8: 000000007100ed24   X7: 00000000ea98c3c0   X6: 00000000ea9a73f0
     X5: 0000000000000149   X4: 0000000000200009   X3: 0000000000000005
     X2: 00000000f2555670   X1: 00000000ea98c3c0   X0: 00000000ffffff9c
    ORIG_X0: 0000000000000000  SYSCALLNO: 0  PSTATE: 00000149


crash64> rd ffffffc091b5bd20 -e ffffffc091b5bd80
ffffffc091b5bd20:  ffffffc091b5bd80 ffffff8008f0ea74   ........t.......
                   x19              x20
ffffffc091b5bd30:  ffffffc144253b40 ffffffc08083b3c0   @;%D............
                   x21
ffffffc091b5bd40:  ffffffc08083bc00 0000000000000000   ................
ffffffc091b5bd50:  ffffffc14b994000 ffffffc14b997000   .@.K.....p.K....
ffffffc091b5bd60:  ffffffc1322c2b00 ffffffc144253b48   .+,2....H;%D....
ffffffc091b5bd70:  ffffffc144253b48 ffffffc091108000   H;%D............


crash64> struct dentry.d_parent,d_name.name ffffffc08083b3c0
  d_parent = 0xffffffc08083b180
  d_name.name = 0xffffffc08083b3f8 "Global"
crash64> struct dentry.d_parent,d_name.name 0xffffffc08083b180
  d_parent = 0xffffffc085da7840
  d_name.name = 0xffffffc08083b1b8 ".UTSystemConfig"
crash64> struct dentry.d_parent,d_name.name 0xffffffc085da7840
  d_parent = 0xffffffc1441ed780
  d_name.name = 0xffffffc085da7878 "0"
crash64> struct dentry.d_parent,d_name.name 0xffffffc1441ed780
  d_parent = 0xffffffc1441ed780
  d_name.name = 0xffffffc1441ed7b8 "/"
crash64> struct dentry.d_parent,d_name.name ffffffc08083bc00
  d_parent = 0xffffffc08083b9c0
  d_name.name = 0xffffffc08083bc38 "Global"
crash64> struct dentry.d_parent,d_name.name 0xffffffc08083b9c0
  d_parent = 0xffffffc085da7840
  d_name.name = 0xffffffc08083b9f8 ".UTSystemConfig"
crash64> struct dentry.d_parent,d_name.name 0xffffffc085da7840
  d_parent = 0xffffffc1441ed780
  d_name.name = 0xffffffc085da7878 "0"
crash64>  struct dentry.d_parent,d_name.name 0xffffffc1441ed780
  d_parent = 0xffffffc1441ed780
  d_name.name = 0xffffffc1441ed7b8 "/"


crash64> set 5200
    PID: 5200
COMMAND: "Thread-8"
   TASK: ffffffc091108000  [THREAD_INFO: ffffffc091b58000]
    CPU: 0
  STATE: TASK_UNINTERRUPTIBLE
crash64> bt
PID: 5200   TASK: ffffffc091108000  CPU: 0   COMMAND: "Thread-8"
 #0 [ffffffc091b5bc50] __switch_to at ffffff8008085538
 #1 [ffffffc091b5bc80] __schedule at ffffff8008f0ce1c
 #2 [ffffffc091b5bce0] schedule at ffffff8008f0d16c
 #3 [ffffffc091b5bd00] schedule_preempt_disabled at ffffff8008f0d4a8
 #4 [ffffffc091b5bd20] __mutex_lock_slowpath at ffffff8008f0e9e0
 #5 [ffffffc091b5bd80] mutex_lock at ffffff8008f0ea70
 #6 [ffffffc091b5bda0] lock_rename at ffffff80081bd4c0
 #7 [ffffffc091b5bdd0] sys_renameat2 at ffffff80081c070c
 #8 [ffffffc091b5beb0] sys_renameat at ffffff80081c0958
 #9 [ffffffc091b5bed0] el0_svc_naked at ffffff800808462c
     PC: 00000000ffffff9c   LR: 00000000f32ab040   SP: 00000000a0030010
    X29: 0000000000000000  X28: 0000000000000000  X27: 0000000000000000
    X26: 0000000000000000  X25: 0000000000000000  X24: 0000000000000000
    X23: 0000000000000000  X22: 0000000000000000  X21: 0000000000000000
    X20: 0000000000000000  X19: 0000000000000000  X18: 0000000000000000
    X17: 0000000000000000  X16: 0000000000000000  X15: 0000000000000000
    X14: 0000000000000000  X13: 0000000000000000  X12: 00000000ec54ac33
    X11: 00000000cacb4090  X10: 0000000000000000   X9: 0000000012ec4880
     X8: 000000007100ed24   X7: 00000000ea98c3c0   X6: 00000000ea9a73f0
     X5: 0000000000000149   X4: 0000000000200009   X3: 0000000000000005
     X2: 00000000f2555670   X1: 00000000ea98c3c0   X0: 00000000ffffff9c
    ORIG_X0: 0000000000000000  SYSCALLNO: 0  PSTATE: 00000149
crash64> rd ffffffc091b5bdd0 -e ffffffc091b5beb0
ffffffc091b5bdd0:  ffffffc091b5beb0 ffffff80081c095c   ........\.......
ffffffc091b5bde0:  0000000000400000 0000000000000000   ..@.............
ffffffc091b5bdf0:  ffffffffffffffff 00000000f32ab040   ........@.*.....
ffffffc091b5be00:  00000000a0030010 0000000000000011   ................
ffffffc091b5be10:  0000000000000186 0000000000000149   ........I.......
ffffffc091b5be20:  ffffff8009004000 ffffffc091b58000   .@..............
ffffffc091b5be30:  ffffffc091108000 ffffffc09110865c   ........\.......
ffffffc091b5be40:  00000000ea9a73f0 0000080000000800   .s..............
ffffffc091b5be50:  0000000000000000 ffffff9cffffff9c   ................
ffffffc091b5be60:  0000000000000000 0000000000000000   ................
ffffffc091b5be70:  ffffffc13342c620 ffffffc08083b3c0    .B3............
ffffffc091b5be80:  ffffffc13342c620 ffffffc08083bc00    .B3............
ffffffc091b5be90:  0000000ac3adf587 ffffffc14b994047   ........G@.K....
ffffffc091b5bea0:  0000000e30bf0ee3 ffffffc14b997047   ...0....Gp.K....
crash64> rd 00000000ea9a73f0 -e 00000000ea9a74f0 ==> oldname
        ea9a73f0:  656761726f74732f 6574616c756d652f   /storage/emulate
        ea9a7400:  5354552e2f302f64 6e6f436d65747379   d/0/.UTSystemCon
        ea9a7410:  626f6c472f676966 6e69766c412f6c61   fig/Global/Alvin
        ea9a7420:  0000006c6d782e32 0000000000000000   2.xml...........
crash64> task_struct.thread ffffffc091108000
  thread = {
    cpu_context = {
      x19 = 18446743801265422336,
      x20 = 18446743805231702272,
      x21 = 18446743804405448704,
      x22 = 18446743805019864064,
      x23 = 18446743524115791872,
      x24 = 18446743524103737712,
      x25 = 0,
      x26 = 18446743801265423656,
      x27 = 0,
      x28 = 3935880128,
      fp = 18446743801276251216,
      sp = 18446743801276251216,
      pc = 18446743524088501564
    },
...

crash64> x/g 3935880128
0xea98c3c0:     0x656761726f74732f
crash64> rd 0xea98c3c0 -e 0xea98c410 ==> newname
        ea98c3c0:  656761726f74732f 6574616c756d652f   /storage/emulate
        ea98c3d0:  5354552e2f302f64 6e6f436d65747379   d/0/.UTSystemCon
        ea98c3e0:  626f6c472f676966 6e69766c412f6c61   fig/Global/Alvin
        ea98c3f0:  61622e6c6d782e32 000000000000006b   2.xml.bak.......
```

经过如上推导,我们支付宝在rename同一个目录"/0/.UTSystemConfig/Global"下文件,完整路径如下:我们再回想一下之前支付宝卡住的路径:
/storage/emulated/0/.UTSystemConfig/Global/Alvin2.xml  ==> /storage/emulated/0/.UTSystemConfig/Global/Alvin2.xml.bak
找到被锁住的位置:

```
$ aarch64-linux-android-addr2line -e vmlinux ffffff80081bd4c0
/home/work/sagit-n-alpha-build/kernel/msm-4.4/fs/namei.c:2660


/* 2632 * p1 and p2 should be directories on the same fs. 2633 */
2634struct dentry *lock_rename(struct dentry *p1, struct dentry *p2)
2635{
2636	struct dentry *p;
2637
2638	if (p1 == p2) {
2639		mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_PARENT);
2640		return NULL;
2641	}
2642
2643	mutex_lock(&p1->d_inode->i_sb->s_vfs_rename_mutex);
2644
2645	p = d_ancestor(p2, p1);
2646	if (p) {
2647		mutex_lock_nested(&p2->d_inode->i_mutex, I_MUTEX_PARENT);
2648		mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_CHILD);
2649		return p;
2650	}
2651
2652	p = d_ancestor(p1, p2);
2653	if (p) {
2654		mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_PARENT);
2655		mutex_lock_nested(&p2->d_inode->i_mutex, I_MUTEX_CHILD);
2656		return p;
2657	}
2658
2659	mutex_lock_nested(&p1->d_inode->i_mutex, I_MUTEX_PARENT);
2660	mutex_lock_nested(&p2->d_inode->i_mutex, I_MUTEX_PARENT2); <== block在这个位置
2661	return NULL;
2662}
2663EXPORT_SYMBOL(lock_rename);
```

如果要卡在这个位置那么只有一种可能, p1和p2不相等, 但是其指向的inode却是同一个, 也就是Global目录使用了两个不同dentry, 但是对应inode却是一样的,导致的死锁

```
crash64> dentry.d_inode ffffffc08083b3c0
  d_inode = 0xffffffc144253a98
crash64> dentry.d_inode ffffffc08083bc00
  d_inode = 0xffffffc144253a98
crash64> dentry.d_u ffffffc08083b3c0
  d_u = {
    d_alias = {
      next = 0xffffffc08083b170,
      pprev = 0xffffffc08083bcb0
    },
    d_rcu = {
      next = 0xffffffc08083b170,
      func = 0xffffffc08083bcb0
    }
  }
crash64> p/x &((struct dentry *)0)->d_u.d_alias
$2 = 0xb0
crash64> eval 0xffffffc08083b170-0xb0
hexadecimal: ffffffc08083b0c0
    decimal: 18446743800987758784  (-272721792832)
      octal: 1777777774020040730300
     binary: 1111111111111111111111111100000010000000100000111011000011000000
crash64> eval 0xffffffc08083bcb0-0xb0
hexadecimal: ffffffc08083bc00  (18014398243152111KB)
    decimal: 18446743800987761664  (-272721789952)
      octal: 1777777774020040736000
     binary: 1111111111111111111111111100000010000000100000111011110000000000
crash64> dentry.d_name ffffffc08083b0c0
  d_name = {
    {
      {
        hash = 3237817176,
        len = 6
      },
      hash_len = 29007620952
    },
    name = 0xffffffc08083b0f8 "Global"
  }
crash64> dentry.d_name ffffffc08083bc00
  d_name = {
    {
      {
        hash = 3237817176,
        len = 6
      },
      hash_len = 29007620952
    },
    name = 0xffffffc08083bc38 "Global"
  }
```

经过验证发现确实是指向了同一个inode节点导致. 这个问题只在sdcardfs上出现,那么有可能能就是在通过文件名找到的最后一个文件的父目录的dentry不一致导致.
一个索引节点可以对应多个目录项对象。 分析：显然，要让一个索引节点表示多个目录项对象，肯定会使用文件链接, inode结构中的i_dentry链表结构，把属于
同一个inode的被使用的（dentry结构中的d_count大于0）目录项连接起来。 显然，这里的目录项肯定是使用硬链接的方式来表示的，但是硬链接不能指向目录！
否则在文件系统中会形成环，另外，硬链接还不能跨文件系统, 但是这个Global明显是没有硬链接的.
详情参考: https://github.com/novelinux/linux-4.x.y/blob/master/include/linux/fs.h/struct_inode.md 关于inode成员变量i_dentry的介绍,
通过以上分析, 那么我们的问题肯定出在dentry和inode相关联的地方
到这里我们只能通过分析表明oldname和newname dentry的初始化来进一步调查原因, 相关的初始化操作在sys_renameat2系统调用中通过user_path_parent来实现:

```
SYSCALL_DEFINE5(renameat2, int, olddfd, const char __user *, oldname,
		int, newdfd, const char __user *, newname, unsigned int, flags)
{
	struct dentry *old_dentry, *new_dentry;
	struct dentry *trap;
	struct path old_path, new_path;
	struct qstr old_last, new_last;
	int old_type, new_type;
	struct inode *delegated_inode = NULL;
	struct filename *from;
	struct filename *to;
	unsigned int lookup_flags = 0, target_flags = LOOKUP_RENAME_TARGET;
	bool should_retry = false;
	int error;

	...

retry:
	from = user_path_parent(olddfd, oldname,
				&old_path, &old_last, &old_type, lookup_flags);
	if (IS_ERR(from)) {
		error = PTR_ERR(from);
		goto exit;
	}

	to = user_path_parent(newdfd, newname,
				&new_path, &new_last, &new_type, lookup_flags);
	if (IS_ERR(to)) {
		error = PTR_ERR(to);
		goto exit1;
	}

	...

retry_deleg:
	trap = lock_rename(new_path.dentry, old_path.dentry);
...
}
```

user_path_parent的具体实现可参考: https://github.com/novelinux/linux-4.x.y/blob/master/fs/namei.c/user_path_parent.md
我们做一种推测,在Global没有任何人使用的时候,缓冲区中肯定是没有的, 我们第一次一定是要通过lookup_slow函数在新建一个dentry, 并找到对应inode和其关联上的
那么我们来看下整个lookup_slow的流程:

```
[  105.436272] [<ffffff80081bec08>] link_path_walk+0x240/0x534
[  105.436275] [<ffffff80081befa0>] path_lookupat+0x30/0x10c
[  105.436279] [<ffffff80081c02ac>] filename_lookup+0x5c/0xb8
[  105.436282] [<ffffff80081c0384>] vfs_path_lookup+0x40/0x48
[  105.436286] [<ffffff8008293908>] __sdcardfs_lookup+0x64/0x330  ==> 在这里调用真正的
[  105.436289] [<ffffff8008293d0c>] sdcardfs_lookup+0xb4/0x1a0
[  105.436293] [<ffffff80081bb4e8>] lookup_real+0x34/0x54
[  105.436296] [<ffffff80081bbab4>] __lookup_hash+0x34/0x48 ==> lookup_slow
[  105.436299] [<ffffff80081be7d0>] walk_component+0xe4/0x2dc
[  105.436302] [<ffffff80081bea40>] link_path_walk+0x78/0x534
[  105.436305] [<ffffff80081bef2c>] path_parentat+0x30/0x74
[  105.436309] [<ffffff80081c01d4>] filename_parentat+0x54/0xd0
[  105.436312] [<ffffff80081c147c>] SyS_renameat2+0x108/0x3b4
[  105.436316] [<ffffff80081c1738>] SyS_renameat+0x10/0x18
[  105.436319] [<ffffff8008084630>] el0_svc_naked+0x24/0x2
```

通过进一部分析代码, 我们最终找到是在如下函数通过d_add函数进行关联的:

```
static struct dentry *__sdcardfs_interpose(struct dentry *dentry,
					 struct super_block *sb,
					 struct path *lower_path,
					 userid_t id)
{
	struct inode *inode;
	struct inode *lower_inode;
	struct super_block *lower_sb;
	struct dentry *ret_dentry = NULL;

	lower_inode = d_inode(lower_path->dentry);
	lower_sb = sdcardfs_lower_super(sb);

	/* check that the lower file system didn't cross a mount point */
	if (lower_inode->i_sb != lower_sb) {
		ret_dentry = ERR_PTR(-EXDEV);
		goto out;
	}

	/*
	 * We allocate our new inode below by calling sdcardfs_iget,
	 * which will initialize some of the new inode's fields
	 */

	/* inherit lower inode number for sdcardfs's inode */
	inode = sdcardfs_iget(sb, lower_inode, id);
	if (IS_ERR(inode)) {
		ret_dentry = ERR_CAST(inode);
		goto out;
	}

	//ret_dentry = d_splice_alias(inode, dentry);
	//dentry = ret_dentry ?: dentry;
	d_add(dentry, inode);
	if (!IS_ERR(dentry))
		update_derived_permission_lock(dentry);
out:
	return ret_dentry;
}
```

我们来看下d_add函数的具体实现:

```
static void __d_instantiate(struct dentry *dentry, struct inode *inode)
{
	unsigned add_flags = d_flags_for_inode(inode);

	spin_lock(&dentry->d_lock);
	if (inode)
		hlist_add_head(&dentry->d_u.d_alias, &inode->i_dentry); // 在这个位置进行关联的
        if (!strncmp(dentry->d_name.name, "Global", 6)) {
            pr_err("liminghao:%s:%d: {%s-%p}\n", __FILE__, __LINE__,
                   dentry->d_name.name, dentry);
        }
	raw_write_seqcount_begin(&dentry->d_seq);
	__d_set_inode_and_type(dentry, inode, add_flags);
	raw_write_seqcount_end(&dentry->d_seq);
	spin_unlock(&dentry->d_lock);
	fsnotify_d_instantiate(dentry, inode);
}

/**
 * d_instantiate - fill in inode information for a dentry
 * @entry: dentry to complete
 * @inode: inode to attach to this dentry
 *
 * Fill in inode information in the entry.
 *
 * This turns negative dentries into productive full members
 * of society.
 *
 * NOTE! This assumes that the inode count has been incremented
 * (or otherwise set) by the caller to indicate that it is now
 * in use by the dcache.
 */

void d_instantiate(struct dentry *entry, struct inode * inode)
{
	BUG_ON(!hlist_unhashed(&entry->d_u.d_alias));
	if (inode)
		spin_lock(&inode->i_lock);
	__d_instantiate(entry, inode);
	if (inode)
		spin_unlock(&inode->i_lock);
	security_d_instantiate(entry, inode);
}
EXPORT_SYMBOL(d_instantiate);


...


/**
 * d_add - add dentry to hash queues
 * @entry: dentry to add
 * @inode: The inode to attach to this dentry
 *
 * This adds the entry to the hash queues and initializes @inode.
 * The entry was actually filled in earlier during d_alloc().
 */

static inline void d_add(struct dentry *entry, struct inode *inode)
{
	d_instantiate(entry, inode); // 关联相关inode
	d_rehash(entry);  // 将对应entry添加到dentry_hashtable中以便下次能够直接在缓冲区中找到
}
```

sdcardfs本身是不支持硬链接的, 分析代码我们发现sdcardfs在这里调用d_add函数非常粗暴, 这个函数根本不会判断对应inode是否是会关联多过dentry, 但是我们发现有另外的一个API在关联的时候会做这个判断
那就是d_splice_alias:

```
/**
 * d_splice_alias - splice a disconnected dentry into the tree if one exists
 * @inode:  the inode which may have a disconnected dentry
 * @dentry: a negative dentry which we want to point to the inode.
 *
 * If inode is a directory and has an IS_ROOT alias, then d_move that in
 * place of the given dentry and return it, else simply d_add the inode
 * to the dentry and return NULL.
 *
 * If a non-IS_ROOT directory is found, the filesystem is corrupt, and
 * we should error out: directories can't have multiple aliases.
 *
 * This is needed in the lookup routine of any filesystem that is exportable
 * (via knfsd) so that we can build dcache paths to directories effectively.
 *
 * If a dentry was found and moved, then it is returned.  Otherwise NULL
 * is returned.  This matches the expected return value of ->lookup.
 *
 * Cluster filesystems may call this function with a negative, hashed dentry.
 * In that case, we know that the inode will be a regular file, and also this
 * will only occur during atomic_open. So we need to check for the dentry
 * being already hashed only in the final case.
 */
struct dentry *d_splice_alias(struct inode *inode, struct dentry *dentry)
{
	if (IS_ERR(inode))
		return ERR_CAST(inode);

	BUG_ON(!d_unhashed(dentry));

	if (!inode) {
		__d_instantiate(dentry, NULL);
		goto out;
	}
	spin_lock(&inode->i_lock);
	if (S_ISDIR(inode->i_mode)) {
		struct dentry *new = __d_find_any_alias(inode);
		if (unlikely(new)) {
			/* The reference to new ensures it remains an alias */
                    if (!strncmp(new->d_name.name, "Global", 6)) {
                        pr_err("liminghao:%s:%d: {%s-%p}\n", __FILE__, __LINE__,
                               new->d_name.name, new);
                    }
			spin_unlock(&inode->i_lock);
			write_seqlock(&rename_lock);
			if (unlikely(d_ancestor(new, dentry))) {
				write_sequnlock(&rename_lock);
				dput(new);
				new = ERR_PTR(-ELOOP);
				pr_warn_ratelimited(
					"VFS: Lookup of '%s' in %s %s"
					" would have caused loop\n",
					dentry->d_name.name,
					inode->i_sb->s_type->name,
					inode->i_sb->s_id);
			} else if (!IS_ROOT(new)) {
				int err = __d_unalias(inode, dentry, new);
				write_sequnlock(&rename_lock);
				if (err) {
					dput(new);
					new = ERR_PTR(err);
				}
			} else {
				__d_move(new, dentry, false);
				write_sequnlock(&rename_lock);
				security_d_instantiate(new, inode);
			}
			iput(inode);
			return new;
		}
	}
	/* already taking inode->i_lock, so d_add() by hand */
	__d_instantiate(dentry, inode);
	spin_unlock(&inode->i_lock);
out:
	security_d_instantiate(dentry, inode);
	d_rehash(dentry);
	return NULL;
}
EXPORT_SYMBOL(d_splice_alias);
```

我们在sdcardfs中将d_add替换成类似如下操作:

```
	ret_dentry = d_splice_alias(inode, dentry);
	dentry = ret_dentry ?: dentry;
```
问题得到解决.