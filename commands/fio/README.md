fio
========================================

fio这个工具实在太强大了，列举一下他的NB之处吧:

* 1.支持十几种存储引擎，可以自定义
* 2.自带做图工具，调用gnuplot做图
* 3.支持几乎所有的存储描述参数
* 4.大量对CPU，内存，进程/线程，文件，IO特性的配置
* 5.压缩，trace回放，。。。这些都包含，灵活的配置

简介
----------------------------------------

fio最初是用来节省为特定负载写专门测试程序，或是进行性能测试，或是找到和重现bug的时间。
写这么一个测试应用是非常浪费时间的。因此需要一个工具来模拟给定的io负载，而不用重复的写
一个又一个的特定的测试程序。但是test负载很难定义。因为可能会产生很多进程或线程，他们
每一个都用他们自己的方式产生io。fio需要足够灵活得来模拟这些case。

典型的fio的工作过程
----------------------------------------

* 1.写一个job文件来描述要访真的io负载。一个job文件可以控制产生任意数目的线程和文件。
    典型的job文件有一个global段（定义共享参数），一个或多少job段（描述具体要产生的job）。
* 2.运行时，fio从文件读这些参数，做处理，并根据这些参数描述，启动这些访真线程/进程

运行fio
----------------------------------------

```
$ fio job_file
```

它会根据job_file的内容来运行。你可以在命令行中指定多个job file，
fio进串行化运行这些文件。相当于在同一个job file不同的section之间
使用了stonewall参数。如果某个job file只包含一个job,可以在命令行中
给出参数，来直接运行，不再需要读取job file。命令行参数同job file参数
的格式是一样的。比如，在job file中的参数iodepth=2，在命令行中
可以写为–iodepth 2 或是 –iodepth=2.fio不需要使用root来支行，除非使用到
的文件或是设备需要root权限。一些选项可能会被限制，比如内存锁，io调度器切换，
或是nice value降级。

job文件格式
----------------------------------------

job file格式采用经典的ini文件:
[]中的值表示job name，可以采用任意的ASCII字符，'global'除外，global有特殊的意义。
Global section描述了job file中各个job的默认配置值。一个job section可以覆盖
global section中的参数，一个job file可以包含几个global section.一个job只会受到
它上面的global section的影响。‘;’和‘#’可以用作注释两个进程，分别从一个从128MB文件中，
随机读的job file.

```
;–start job file–
[global]
rw=randread
size=128m

[job1]

[job2]
;–end job file–
```

job1和job2 section是空的，因为所有的描述参数是共享的。没有给出filename=选项，
fio会为每一个job创建一个文件名，如果用命令写，则是：

```
$fio –name=global –rw=randread –size=128m –name=job1 –name=job2
```

#### 多个进程随机写文件的实例

```
;–start job file –
[random-writers]
ioengine=libaio
iodepth=4
rw=randwrite
bs=32k
direct=0
size=64m
numjobs=4
;–end job file–
```

没有global section,只有一个job section.

上一个实例的说明：采用async,每一个文件的队列长度为4，采用随机写，采用32k的块，
采用非direct io，共有4个进程，每个进程随机写64M的文件。也可以采用下面的命令

```
$ fio –name=random-writers –ioengine=libaio –iodepth=4 –rw=randwrite –bs=32k –direct=0 –size=64m –numjobs=4
```

环境变量
----------------------------------------

在job file中支持环境变量扩展。类似于${VARNAME}可以作为选项的值（在=号右边）。

#### 实例：

```
$SIZE=64m  NUMJOBS=4 fio jobfile,fio
;–start job files–
[random-writers]
rw=randwrite
size=${SIZE}
numjobs=${NUMJOBS}
;–end job file–

将被扩展为
；–start job file–
[random-writers]
rw=randwrite
size=64m
numjobs=4
;–end job file–
```

保留keywords
----------------------------------------

fio有一些保留keywords，在内部将其替换成合适的值，这些keywords是：

* pagesize   当前系统的页大小
* mb_memory 系统的总内存的大小，以MB为单位
* ncpus 在线有效的cpu数

这引起在命令行中和job file中都可以用，当job运行的时候，会自动的用当前系统的徝进行替换。支持简单的数学计算，如：
size=8*$mb_memory

#### 类型

```
str 字符串
time时间（int)
int 整数
bool
irange 整数范围
float_list 符点数列
```

一个job包含的基本的参数
----------------------------------------

### readwrite=str,rw=str

```
read 顺序读
write 顺序写
randwrite 随机写
randread 随机读
rw,readwrite 顺序混合读写
randrw 随机混合读写
```

参数备注:

对于混合io类型，混认是50%的读，50%的写，对于特定的io类型，因为速度可能不同，结果可能会有稍有偏差.
通过在在str之后加“:<nr>”可以配置在执行一下获取offset操作之前要执行的IO次数。

```
For a random read, it would lik ’rw=randread:8′ for passing
in an offset modifier with a value of 8.
```

如果后缀用于顺序IO类型的话，，那么将在每次IO之后，将这个值加到产生的offset之后。
e.g. rw=write:4k每次写之后将会跳过4K。它将顺序的IO转化为带有洞的顺序IO。参考‘rw_sequencer’选项。

### rw_sequencer=str

如果rw=<str>后有offset修饰的话，这个选项可以控制这个数字<nr>如何修饰产生的IO offset.
可以接收的值是：

```
sequential 产生顺序的offset
identical 产生相同的offset
```

参数备注:
‘sequential’仅用于随机IO。通常情况下，fio在每次IO之后，将会生成一个新的随机IO。
e.g.rw=randread:8，将会在每8次IO之后执行seek，而不是每次IO之后。顺序IO已经是顺序的，
再设置为‘sequential’将不会产生任何不同。‘identical’会产生同‘sequential’相似的行为，
只是它会连续产生8次相同的offset，然后生成一个新的offset.

### block size

产生的IO单元的大小，可以是一个孤立的值，也可以是一个范围。

```
blocksize=int,bs=int
```

单次IO的block size,默认为4k。如果是单个值的话，将会对读写都生效。如果是一个逗号，
再跟一个int值的话，则是仅对于写有效。也就是说，格式可以是bs=read_end_write或是
bs=read,write。e.g. bs=4k,8k读使用4k的块，写使用8k的块。e.g.bs=,8k将使得写
采用8k的块，读采用默认的值。

### IO size

将会读/写多少数据

```
size=int
```

这个job IO总共要传输的数据的大小。FIO将会执行到所有的数据传输完成，
除非设定了运行时间（‘runtime’选项）。除非有特定的‘nrfiles’选项
和‘filesize’选项被设置，fio将会在job定义的文件中平分这个大小。如果
这个值不设置的话，fio将会使用这个文件或设备的总大小。如果这些文件不存在的话，
size选项一定要给出。也可以给出一个1到100的百分比。e.g. size=20%，fio将会使用给定的文件或设备的20%的空间。

### IO引擎

发起IO的方式。

```
<1>ioengine=str
定义job向文件发起IO的方式
sync 基本的read,write.lseek用来作定位
psync 基本的pread,pwrite
vsync 基本的readv,writev
libaio Linux专有的异步IO。Linux仅支持非buffered IO的队列行为。
posixaio glibc posix异步IO
solarisaio solaris独有的异步IO
windowsaio windows独有的异步IO
mmap 文件通过内存映射到用户空间，使用memcpy写入和读出数据
splice 使用splice和vmsplice在用户空间和内核之间传输数据
syslet-rw 使用syslet 系统调用来构造普通的read/write异步IO
sg SCSI generic sg v3 io.可以是使用SG_IO ioctl来同步，或是目标是一个sg字符设备，我们使用read和write执行异步IO
null 不传输任何数据，只是伪装成这样。主要用于训练使用fio，或是基本debug/test的目的。
net 根据给定的host:port通过网络传输数据。根据具体的协议，hostname,port,listen,filename这些选项将被用来说明建立哪种连接，协议选项将决定哪种协议被使用。
netsplice 像net，但是使用splic/vmsplice来映射数据和发送/接收数据。
cpuio 不传输任何的数据，但是要根据cpuload=和cpucycle=选项占用CPU周期.
      e.g. cpuload=85将使用job不做任何的实际IO，但要占用85%的CPU周期。
      在SMP机器上，使用numjobs=<no_of_cpu>来获取需要的CPU，因为cpuload仅会载入单个CPU，
      然后占用需要的比例。
guasi GUASI IO引擎是一般的用于异步IO的用户空间异步系统调用接口
rdma RDMA I/O引擎支持RDMA内存语义（RDMA_WRITE/RDMA_READ）和通道主义(Send/Recv）用于InfiniBand,RoCE和iWARP协议
external 指明要调用一个外部的IO引擎（二进制文件）。e.g. ioengine=external:/tmp/foo.o将载入/tmp下的foo.o这个IO引擎
```

### IO depth

如果IO引擎是异步的，这个指定我们需要保持的队列深度

```
<1>iodepth=int
```

加于文件之上的保持的IO单元。默认对于每个文件来说是1，可以设置一个更大的值来提供并发度。
iodepth大于1不会影响同步IO引擎（除非verify_async这个选项被设置）。even async engines
may impose OS restrictions causing the desired depth not to be achieved.
这会在Linux使用libaio并且设置direct=1的时候发生，因为buffered io在OS中不是异步的。
在外部通过类似于iostat这些工具来观察队列深度来保证这个IO队列深度是我们想要的。
这个可以参考褚霸的博客http://blog.yufeng.info/archives/2104

### IO type

```
<1>direct=bool
true,则标明采用non-buffered io.同O_DIRECT效果一样。ZFS和Solaris不支持direct io，在windows同步IO引擎不支持direct io
<2>buffered=bool
true,则标明采用buffered io。是direct的反义词，默认是true
```

### Num files

负载将分发到几个文件之中

```
<1>nrfiles=int
用于这个job的文件数目,默认为1
<2>openfiles=int
在同一时间可以同时打开的文件数目，默认同nrfiles相等，可以设置小一些，来限制同时打开的文件数目。
```

### Num threads

````
<1>numjobs=int
创建特定数目的job副本。可能是创建大量的线程/进程来执行同一件事。我们将这样一系列的job，看作一个特定的group
详细参数：
<1>name=str
job名，用于输出信息用的名字。如果不设置的话，fio输出信息时将采用job name，如果设置的话，将用设置的名字。在命令行中，这个参数有特殊的作用，标明一个新job的开始。
<2>description=str
job的说明信息,在job运行的时候不起作用，只是在输出文件描述信息的时候才起作用。
<3>directory=str
使用的文件的路径前缀，默认是./
<4>filename=str
一般情况下，fio会根据job名，线程号，文件名来产生一个文件名。如果，想在多个job之间共享同一个文件的话，可以设定一个名字来代替默认的名字.如果ioengine是‘net’的话，文件名则是以这种格式=host,port,protocol.如果ioengine是基于文件的话，可以通过‘:’分割来设定一系列的文件。e.g. filename=/dev/sda:/dev/sdb 希望job打开/dev/sda和/dev/sdb作为两个工作文件。
<5>opendir=str
让fio递归的添加目录下和子目录下的所有文件。
<6>lockfile=str
fio在文件上执行IO之前默认是不锁文件的，这样的话，当有多个线程在此文件上执行IO的话，会造成结果的不一致。这个选项可以用来共享文件的负载，支持的锁类型：
none 默认不使用锁
exclusive 排它锁
readwrite 读写锁
在后面可以加一个数字后缀，如果设置的话，每一个线程将会执行这个数字指定的IO后才会放弃锁，因为锁的开销是比较大的，所以这种方式可以加速IO。
<7>kb_base=int
size换算单位，1000/1024,默认为1024
<8>randrepeat=bool
对于随机IO负载，配置生成器的种子，使得路径是可以预估的，使得每次重复执行生成的序列是一样的。
<9>use_os_rand=bool
fio可以使用操作系统的随机数产生器，也可以使用fio内部的随机数产生器（基于tausworthe），默认是采用fio内部的产生器,质量更数，速度更快。
<7>fallocate=str
如何准备测试文件
none 不执行预分配空间
posix 通过posix_fallocate()预分配空间
keep 通过fallocate()（设置FALLOC_FL_KEEP_SIZE）预分配空间
0 none的别名,出于兼容性
1 posix的别名，出于兼容性
并不是在所有的平台上都有效，‘keep’仅在linux上有效，ZFS不支持。默认为‘posix’
<8>fadvise_hint=bool
默认fio将使用fadvise()来告知内核fio要产生的IO类型，如果不想告诉kernel来执行一些特定的IO类型的话，可行关闭这个选项。如果设置的话，fio将使用POSIX_FADV_SEWUENTIAL来作顺序IO，使用POSIX_FADV_RANDOM来做随机IO
<9>filesize=int
单个文件的大小，可以是一个范围，在这种情况下，fio将会在一个范围内选择一个大小来决定单个文件大小，如果没有设置的话，所有的文件将会是同样的大小。
<10>fill_device=bool,fill_fs=bool
填满空间直到达到终止条件ENOSPC，只对顺序写有意义。对于读负载，首行要填满挂载点，然后再启动IO，对于裸设备结点，这个设置则没有什么意义，因为，它的大小已被被文件系统知道了，此外写的超出文件将不会返回ENOSPC.
<11>blockalign=int,ba=int
配置随机io的对齐边界。默认是与blocksize的配置一致，对于direct_io，最小为512b,因为它与依赖的硬件块大小，对于使用文件的随机map来说，这个选项不起作用。
<14>blocksize_range=irange,bsrange=irange
不再采用单一的块大小，而是定义一个范围，fio将采用混合io块大小.IO单元大小一般是给定最小值的备数。同时应用于读写，当然也可以通过‘,’来隔开分别配置读写。
<15>bssplit=str
可以更为精确的控制产生的block size.这个选项可以用来定义各个块大小所占的权重.格式是
bssplit=blocksize/percentage;blocksize/percentage
bssplit=4k/10:64k/50;32k/40
产生的这样的负载：50% 64k的块，10% 4k的块, 40% 32k的块
可以分别为读和写来设置
e.g. bssplit=2k/50:4k/50,4k/90:8k/10
产生这样的负载：读（50% 64k的块，50% 4k的块），写（90% 4k的块, 10% 8k的块）
<16>blocksize_unaligned,bs_unaligned
如果这个选项被设置的，在bsrange范围内的大小都可以产生，这个选项对于direct io没有作用，因为对于direct io至少需要扇区对齐
<17>zero_buffers
如果这个选项设置的话，IO buffer全部位将被初始为0,如果没有置位的话，将会是随机数.
<18>refill_buffers
如果这个选项设置的话，fio将在每次submit之后都会将重新填满IO buffer,默认都会在初始是填满，以后重复利用。这个选项只有在zero_buffers没有设置的话，这个选项才有作用。
<19>scramble_buffer=bool
如果refilee_buffers成本太高的话，但是负载要求不使用重复数据块，设置这个选项的话，可以轻微的改动IO buffer内容，这种方法骗不过聪明的块压缩算法，但是可以骗过一些简单的算法。
<20>buffer_compress_percentage=int
如果这个设置的话，fio将会尝试提供可以压缩到特定级别的Buffer内容。FIO是能完提供混合的0和随机数来实现的。Note that this is per block size unit, for file/disk wide compression level that matches this setting, you’ll also want to set refill_buffers.
<21>buffer_compress_chunk=int
See buffer_compress_percentage. This setting allows fio to manage how big the ranges of random data and zeroed data is. Without this set, fio will provide buffer_compress_percentage of blocksize random data, followed by the remaining zeroed. With this set to some chunk size smaller than the block size, fio can alternate random and zeroed data throughout the IO buffer.
<22>file_service_type=str
fio切换job时，如何选择文件，支持下面的选项
random 随机选择一个文件
roundrobin 循环使用打开的文件，默认
sequential 完成一个文件后，再移动到下一个文件
这个选项可以加后缀数字，标明切换到下一个新的频繁程度。
e.g. random:4 每4次IO后，将会切换到一下随机的文件
<23>iodepth_batch_submit=int,iodepth_batch=int
这个定义了一次性提交几个IO，默认是1，意味着一旦准备好就提交IO，这个选项可以用来一次性批量提交IO
<24>iodepth_batch_complete=int
这个选项定义了一次取回多少个IO，如果定义为1的话，意味着我们将向内核请求最小为1个IO.The IO retrieval will go on until we hit the limit set by iodetph_low.If this variable is set to 0, then fi will always check for completed events before quuing more IO.This helps reduce IO latency, at the cost of more retrieval sysstem calls.
<25>iodepth_low=int
这个水位标志标明什么时候开始重新填充这个队列，默认是同iodepth是一样的，意味着，每时每刻都在尝试填满这个队列。如果iodepth设置为16，而iodepth设置为4的话，那么fio将等到depth下降到4才开始重新填充
<26>offset=int
在文件特定的偏移开始读数据,在这个offset之前的数据将不会被使用，有效的文件大小=real_size-offset
<27>offset_increment=int
如果这个选项被设置的话，实际的offset=offset+offset_increment * thread_number,线程号是从0开始的一个计数器，对于每一个job来说是递增的。这个选项对于几个job同时并行在不交界的地方操作一个文件是有用的。
<28>fsync=int
如果写一个文件的话，每n次IO传输完block后，都会进行一次同步脏数据的操作。
e.g. fsync=int
fio每32次写之后，同步一次文件。如果采用non-buffered io，不需要使用sync同步文件
对于sg io引擎的话，可以在任何情况下同步磁盘cache.
<29>fdatasync=int
同fsync，但是采用fdatasync()来同步数据，但不同步元数据
<30>sync_file_range=str:val
对于每‘val’个写操作，将执行sync_file_range()。FIO将跟踪从上次sync_file_range()调用之扣的写范围，‘str’可以是以下的选择
wait_before SYNC_FILE_RANGE_WAIT_BEFORE
write SYNC_FILE_RANGE_WRITE
wait_after SYNC_FILE_RANGE_WAIT_AFTER
e.g.sync_file_range=wait_before,write:8,fio将在每8次写后使用SYNC_FILE_RANGE_WAIT_BEFORE|SYNC_FILE_RANGE_WRITE
<31>overwrite=bool
如果是true的话，写一个文件的话，将会覆盖已经存在的数据。如果文件不存在的话，它将会在写阶段开始的时候创建这个文件。
<32>end_fsync=bool
如果是true的话，当job退出的话，fsync将会同步文件内容
<33>fsync_on_close=bool
如果是true的话，关闭时，fio将会同步脏文件，不同于end_fsync的时，它将会在每个文件关闭时都会发生，而不是只在job结束时。
<34>rwmixread=int
混合读写中，读占的百分比
<35>rwmixwrite=int
混合读写中，写占的百分比；如果rwmixread=int和rwmixwrite=int同时使用的话并且相加不等于100%的话，第二个值将会覆盖第一个值。这可能要干扰比例的设定,如果要求fio来限制读和写到一定的比率。在果在这种情况下，那么分布会的有点的不同。
<36>norandommap
一般情况下，fio在做随机IO时，将会覆盖文件的每一个block.如果这个选项设置的话，fio将只是获取一个新的随机offset,而不会查询过去的历史。这意味着一些块可能没有读或写，一些块可能要读/写很多次。在个选项与verify=互斥，并只有多个块大小（bsrange=）正在使用，因为fio只会记录完整的块的重写。
<37>softrandommap=bool
See norandommap. If fio runs with the random block map enabled and it fails to allocate the map, if this option is set it will continue without a random block map. As coverage will not be as complete as with random maps, this option is disabled by default.
<38>nice=int
根据给定的nice值来运行这个job
<39>prio=int
设置job的优先级，linux将这个值限制在0-7之间，0是最高的。
<40>prioclass=int
设置优先级等级。
<41>thinktime=int
上一个IO完成之后，拖延x毫秒，然后跳到下一个。可以用来访真应用进行的处理。
<42>thinktime_spin=int
只有在thinktime设置时才有效，在为了sleep完thinktime规定的时间之前，假装花费CPU时间来做一些与数据接收有关的事情。
<43>thinktime_blocks
只有在thinktime设置时才有效，控制在等等‘thinktime’的时间内产生多少个block，如果没有设置的话，这个值将是1，每次block后，都会将等待‘thinktime’us。
<44>rate=int
限制job的带宽。
e.g.rate=500k,限制读和写到500k/s
e.g.rate=1m,500k,限制读到1MB/s，限制写到500KB/s
e.g.rate=,500k , 限制写到500kb/s
e.g.rate=500k, 限制读到500KB/s
<45>ratemin=int
告诉fio尽最在能力来保证这个最小的带宽，如果不能满足这个需要，将会导致程序退出。
<46>rate_iops=int
将带宽限制到固定数目的IOPS，基本上同rate一样，只是独立于带宽，如果job是指定了一个block size范围，而不是一个固定的值的话，最小blocksize将会作为标准。
<47>rate_iops_min=int
如果fio达不到这个IOPS的话，将会导致job退出。
<48>ratecycle=int
几个毫秒内的平均带宽。用于‘rate’和‘ratemin’
<49>cpumask=int
设置job使用的CPU.给出的参数是一个掩码来设置job可以运行的CPU。所以，如果要允许CPU在1和5上的话，可以通过10进制数来设置（1<<1 | 1<<5），或是34。查看sched_setaffinity的man page。它可能并不是支持所有的操作系统和kernel版本。This option doesn’t work well for a higher CPU count than what you can store in an integer mask, so it can only control cpus 1-32. For boxes with larger CPU counts, use cpus_allowed.
<50>cpus_allowed=str
功能同cpumask一样，但是允许通过一段文本来设置允许的CPU。e.g.上面的例子可是这样写cpus_allowed=1,5。这个选项允许设置一个CPU范围，如cpus_allowed=1,5,8-15
<51>startdelay=time
fio启动几秒后再启动job。只有在job文件包含几个jobs时才有效，是为了将某个job延时几秒后执行。
<52>runtime=time
控制fio在执行设定的时间后退出执行。很难来控制单个job的运行时间，所以这个参数是用来控制总的运行时间。
<53>time_based
如果设置的话，即使file已被完全读写或写完，也要执行完runtime规定的时间。它是通过循环执行相同的负载来实现的。
<54>ramp_tim=time
设定在记录任何性能信息之前要运行特定负载的时间。这个用来等性能稳定后，再记录日志结果，因此可以减少生成稳定的结果需要的运行时间。Note that the ramp_time is considered lead in time for a job, thus it will increase the total runtime if a special timeout or runtime is specified.
<55>invalidate=bool
Invalidate the buffer/page cache parts for this file prior to starting io. Defaults to true.
<56>sync=bool
使用sync来进行buffered写。对于多数引擎，这意味着使用O_SYNC
<57>iomem=str,mem=str
fio可以使用各种各样的类型的内存用来io单元buffer.
malloc 使用malloc()
shm 使用共享内存.通过shmget()分配
shmhuge 同shm一样，可以使用huge pages
mmap 使用mmap。可以是匿名内存，或是支持的文件，如果一个文件名在选项后面设置的话，格式是mem=mmap:/path/to/file
mmaphuge 使用mmapped huge file.在mmaphuge扣面添加文件名，alamem=mmaphuge:/hugetlbfs/file
分配的区域是由job允许的最大block size * io 队列的长度。对于shmhuge和mmaphuge，系统应该有空闲的页来分配。这个可以通过检测和设置reading/writing /proc/sys/vm/nr_hugepages来实现（linux）。FIO假设一个huge page是4MB。所以要计算对于一个JOB文件需要的Huge page数量，加上所有jobs的队列长度再乘以最大块大小，然后除以每个huge page的大小。可以通过查看/proc/meminfo来看huge pages的大小。如果通过设置nr_hugepages=0来使得不允许分配huge pages，使用mmaphug或是shmhuge将会失败。
mmaphuge需要挂载hugelbfs而且要指定文件的位置，所以如果要挂载在/huge下的话，可以使用mem=mmaphuge:/huge/somefile
<58>iomem_align=int
标明IO内存缓冲的内存对齐方式。Note that the given alignment is applied to the first IO unit buffer, if using iodepth the alignment of the following buffers are given by the bs used. In other words, if using a bs that is a multiple of the page sized in the system, all buffers will be aligned to this value. If using a bs that is not page aligned, the alignment of subsequent IO memory buffers is the sum of the iomem_align and bs used.
<59>hugepage-size=int
设置huge page的大小。至少要与系统的设定相等。默认是4MB，必然是MB的倍数，所以用hugepage-size=Xm是用来避免出现不是2的整数次方的情况。
<60>exitall
当一个job退出时，会终止运行其它的job，默认是等待所有的job都完成，FIO才退出，但有时候这并不是我们想要的。
<61>bwavgtime=int
在给定时间内的平均带宽。值是以毫秒为单位的
<62>iopsavgtime=int
在给定时间内的平均IOPS，值是以毫秒为单位的
<63>create_serialize=bool
job将会串行化创建job,这将会用来避免数据文件的交叉，这依赖于文件系统和系统的CPU数
<64>create_fsync=bool
创建后同步数据文件，这是默认的值
<65>create_on_open=bool
不会为IO预先创建文件，只是在要向文件发起IO的时候，才创建open()
<66>create_only=bool
如果设置为true的话，fio将只运行到job的配置阶段。如果文件需要部署或是更新的磁盘的话，只有上面的事才会做，实际的文件内容并没有执行。
<67>pre_read=bool
如果这个选项被设置的话，在执行IO操作之前，文件将会被预读到内存.这会删除‘invalidate’标志，因为预读数据，然后丢弃cache中的数据的话，是没有意义的。这只是对可以seek的IO引擎有效，因为这允许读相同的数据多次。因此对于network和splice不起作用。
<68>unlink=bool
完成后将删除job产生的文件。默认是not,如果设置为true的话，将会花很多时间重复创建这些文件。
<69>loops=int
重复运行某个job多次，默认是1
<70>do_verify=bool
写完成后，执行一个校验的阶段，只有当verify设置的时候才有效。默认是true
<80>verify=str
写一个文件时，每次执行完一个job扣，fio可以检验文件内容.允许的校验算法是：
md5,crc64,crc32c,crc32c-intel,crc32,crc16,crc7,sha512,sha256,sha1,meta,null.
这个选项可以用来执行重复的burn-in测试，来保证写数据已经正确的读回。如果是read或随机读，fio将假设它将会检验先前写的文件。如果是各种格式的写，verify将会是对新写入的数据进行校验。
<81>verifysort=bool
如果设置的话，fio will sort written verify blocks when it deems it faster to read them back in a sorted manner. This is often the case when overwriting an existing file, since the blocks are already laid out in the file system. You can ignore this option unless doing huge amounts of really fast IO where the red-black tree sorting CPU time becomes significant.
<82>verify_offset=int
Swap the verification header with data somewhere else in the block before writing. Its swapped back before verifying.
<83>verify_interval=int
Write the verification header at a finer granularity than the blocksize. It will be written for chunks the size of header_interval. blocksize should divide this evenly
<84>verify_pattern=str
<85>verify_fatal=bool
<86>verify_dump=bool
<87>verify_async=int
<88>verify_async_cpus=str
<89>verify_backlog=int
<90>verify_backlog_batch=int
<91>stonewall,wait_for_previous
等待先前的job执行完成后，再启动一个新的job。可以用来在job文件中加入串行化的点。stone wall也用来启动一个新reporting group
<92>new_group
启动一个新的reporting group。如果这个选项没有设置的话，在job文件中的job将属于相同的reporting group，除非通过stonewall隔开
<93>group_reporting
如果‘numjobs’设置的话，我们感兴趣的可能是打印group的统计值，而不是一个单独的job。这在‘numjobs’的值很大时，一般是设置为true的，可以减少输出的信息量。如果‘group_reporting’设置的话，fio将会显示最终的per-groupreport而不是每一个job都会显示
<94>thread
fio默认会使用fork()创建job，如果这个选项设置的话，fio将使用pthread_create来创建线程
<95>zonesize=int
将一个文件分为设定的大小的zone
<96>zoneskip=int
跳过这个zone的数据都被读完后，会跳过设定数目的zone.
<97>write_iolog=str
将IO模式写到一个指定的文件中。为每一个job指定一个单独的文件，否则iolog将会分散的的，文件将会冲突。
<98>read_iolog=str
将开一个指定的文件，回复里面的日志。这可以用来存储一个负载，并进行重放。给出的iolog也可以是一个二进制文件，允许fio来重放通过blktrace获取的负载。
<99>replay_no_stall
当使用read_iolog重放I/O时，默认是尝试遵守这个时间戳，在每个IOPS之前会有适当的延迟。通过设置这个属性，将不会遵守这个时间戳，会根据期望的顺序，尝试回复，越快越好。结果就是相同类型的IO，但是不同的时间
<101>replay_redirect
当使用read_iolog回放IO时，默认的行为是在每一个IOP来源的major/minor设备上回放IOPS。这在有些情况是不是期望的，比如在另一台机器上回放，或是更换了硬件，使是major/minor映射关系发生了改变。Replay_redirect将会导致所有的IOPS回放到单个设备上，不管这些IO来源于哪里。e.g.replay_redirect=/dev/sdc将会使得所有的IO都会重定向到/dev/sdc.这就意味着多个设备的数据都会重放到一个设置，如果想来自己多个设备的数据重放到多个设置的话，需要处理我们的trace，生成独立的trace，再使用fio进行重放，不过这会破坏多个设备访问的严格次序。
<102>write_bw_log=str
在job file写这个job的带宽日志。可以在他们的生命周期内存储job的带宽数据。内部的fio_generate_plots脚本可以使用gnuplot将这些文本转化成图。
<103>write_lat_log=str
同write_bw_log类似，只是这个选项可以存储io提交，完成和总的响应时间。如果没有指定文件名，默认的文件名是jobname_type.log。即使给出了文件名，fio也会添加两种类型的log。
e.g.如果我们指定write_lat_log=foo
实际的log名将是foo_slat.log,foo_slat.log和foo_lat.log.这会帮助fio_generate_plot来自动处理log
<104>write_iops_log=str
类似于write_bw_log,但是写的是IOPS.如果没有给定文件名的话，默认的文件名是jobname_type.log。
<105>log_avg_msec=int
默认，fio每完成一个IO将会记录一个日志（iops,latency,bw log）。当向磁盘写日志的时候，将会很快变的很大。设置这个选项的话，fio将会在一定的时期内平均这些值，指少日志的数量，默认是0
<106>lockmem=int
使用mlock可以指定特定的内存大小，用来访真少量内存
<107>exec_preren=str
运行job之前，通过过system执行指定的命令
<108>exec_postrun=str
job执行完成后，通过system执行指定的命令
<109>ioscheduler=str
在运行之前，尝试将文件所在的设备切换到指定的调度器。
<110>cpuload=int
如果job是非常占用CPU周期的，可以指定战胜CPU周期的百分比。
<120>cpuchunks=int
如果job是非常战胜CPU周期的，将load分拆为时间的cycles，以毫秒为单位
<121>disk_util=bool
产生磁盘利用率统计信息。默认是打开的
<122>disable_lat=bool
延迟的有效数字。Disable measurements of total latency numbers. Useful only for cutting back the number of calls to gettimeofday,as that does impact performance at really high IOPS rates.Note that to really get rid of a large amount of these calls, this option must be used with disable_slat and disable_bw as well.
<123>disable_clat=bool
<124>disable_slat_bool
<125>disable_bw=bool
<126>clat_percentiles=bool
允许报告完成完成响应时间的百分比
<127>percentile_list=float_list
<128>gtod_reduce=bool
<129>gtod_cpu=int
<130>continue_on_error=str
一般情况下，一旦检测到错误，fio将会退出这个job.如果这个选项设置的话，fio将会一直执行到有‘non-fatal错误‘（EIO或EILSEQ）或是执行时间耗完，或是指定的I/Osize完成。如果这个选项设置的话，将会添加两个状态，总的错误计数和第一个error。允许的值是
none 全部IO或检验错误后，都会退出
read 读错误时会继续执行，其它的错误会退出
write 写错误时会继续执行，其它的错误会退出
io 任何IO error时会继续执行，其它的错误会退出
verify 校验错误时会继续执行，其它的错误会退出
all 遇到所有的错误都会继续执行
<131>cgroup=str
<132>cgroup_weitht=int
<133>cgroup_weight=int
<134>cgroup_nodelete=bool
<135>uid=int
不是使用调用者的用户来执行，而是指定用户ID
<136>gid=int
设置group id
<137>flow_id=int
<138>flow=int
<139>flow_watermark=int
<140>flow_sleep=int
```

下面的参数只对指定的IO引擎有效：
[libaio] userspace_reap
[netsplice]hostname=str
[net]hostname=str
[netsplice]port=int
[netsplice]proto=str
[net]protocol=str
[net]proto=str
[net]listen
输出
在运行时，fio将打印当前job创建的状态
e.g.
Threads: 1: [_r] [24.8% done] [ 13509/  8334 kb/s] [eta 00h:01m:31s]
生命周期
P   线程已经启动，还没有启动
C 线程启动
I 纯种已经初始化，等待中
p 线程运行中，预读文件
R 顺序读
r 随机读
W 顺序写
w 随机写
M 混合顺序读写
m 混合随机读写
F 等待执行fsync()
V 运行，检验写的数据
E 线程退出,还没有被主线程获取状态
_ Thread reaped, or
X Thread reaped, exited with an error.
K Thread reaped, exited due to signal.

其它的值都是可以自解释的：
当前正在运行的IO线程数。
从上次检查之后的IO速度（读速度/写速度）
估计的完成百分比
整个group的估计完成时间

当fio完成的时候（或是通过ctrl-c终止的时候），将会打印每一个线程的数据，每个group的数据，和磁盘数据。

io= 执行了多少M的IO
bw= 平均IO带宽
iops=   IOPS
runt= 线程运行时间
slat 提交延迟
clat 完成延迟
lat响应时间
bw 带宽
cpu利用率
IO depths=io队列
IO submit=单个IO提交要提交的IO数
IO complete= Like the above submit number, but for completions instead.
IO issued= The number of read/write requests issued, and how many
of them were short.
IO latencies=IO完延迟的分布

io= 总共执行了多少size的IO
aggrb= group总带宽
minb= 最小平均带宽.
maxb= 最大平均带宽.
mint= group中线程的最短运行时间.
maxt= group中线程的最长运行时间.

ios= 所有group总共执行的IO数.
merge= 总共发生的IO合并数.
ticks= Number of ticks we kept the disk busy.
io_queue= 花费在队列上的总共时间.
util= 磁盘利用率

为了便于脚本分析，可能需要将结果生成表或图，fio可以生成以分号分割的结果。
trace文件格式
