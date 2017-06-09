# iozone

```
iozone: help mode

    Usage: iozone [-s filesize_kB] [-r record_size_kB] [-f [path]filename] [-h]
                  [-i test] [-E] [-p] [-a] [-A] [-z] [-Z] [-m] [-M] [-t children]
                  [-l min_number_procs] [-u max_number_procs] [-v] [-R] [-x] [-o]
                  [-d microseconds] [-F path1 path2...] [-V pattern] [-j stride]
                  [-T] [-C] [-B] [-D] [-G] [-I] [-H depth] [-k depth] [-U mount_point]
                  [-S cache_size] [-O] [-L cacheline_size] [-K] [-g maxfilesize_kB]
                  [-n minfilesize_kB] [-N] [-Q] [-P start_cpu] [-e] [-c] [-b Excel.xls]
                  [-J milliseconds] [-X write_telemetry_filename] [-w] [-W]
                  [-Y read_telemetry_filename] [-y minrecsize_kB] [-q maxrecsize_kB]
                  [-+u] [-+m cluster_filename] [-+d] [-+x multiplier] [-+p # ]
                  [-+r] [-+t] [-+X] [-+Z] [-+w percent dedupable] [-+y percent_interior_dedup]
                  [-+C percent_dedup_within]

           -a  Auto mode
           -A  Auto2 mode
           -b Filename  Create Excel worksheet file
           -B  Use mmap() files
           -c  Include close in the timing calculations
           -C  Show bytes transferred by each child in throughput testing
           -d #  Microsecond delay out of barrier
           -D  Use msync(MS_ASYNC) on mmap files
           -e  Include flush (fsync,fflush) in the timing calculations
           -E  Run extension tests
           -f filename  to use
           -F filenames  for each process/thread in throughput test
           -g #  Set maximum file size (in kBytes) for auto mode (or #m or #g)
           -G  Use msync(MS_SYNC) on mmap files
           -h  help
           -H #  Use POSIX async I/O with # async operations
           -i #  Test to run (0=write/rewrite, 1=read/re-read, 2=random-read/write
                 3=Read-backwards, 4=Re-write-record, 5=stride-read, 6=fwrite/re-fwrite
                 7=fread/Re-fread, 8=random_mix, 9=pwrite/Re-pwrite, 10=pread/Re-pread
                 11=pwritev/Re-pwritev, 12=preadv/Re-preadv)
           -I  Use VxFS VX_DIRECT, O_DIRECT,or O_DIRECTIO for all file operations
           -j #  Set stride of file accesses to (# * record size)
           -J #  milliseconds of compute cycle before each I/O operation
           -k #  Use POSIX async I/O (no bcopy) with # async operations
           -K  Create jitter in the access pattern for readers
           -l #  Lower limit on number of processes to run
           -L #  Set processor cache line size to value (in bytes)
           -m  Use multiple buffers
           -M  Report uname -a output
           -n #  Set minimum file size (in kBytes) for auto mode (or #m or #g)
           -N  Report results in microseconds per operation
           -o  Writes are synch (O_SYNC)
           -O  Give results in ops/sec.
           -p  Purge on
           -P #  Bind processes/threads to processors, starting with this cpu
           -q #  Set maximum record size (in kBytes) for auto mode (or #m or #g)
           -Q  Create offset/latency files
           -r #  record size in Kb
              or -r #k .. size in kB
              or -r #m .. size in MB
              or -r #g .. size in GB
           -R  Generate Excel report
           -s #  file size in Kb
              or -s #k .. size in kB
              or -s #m .. size in MB
              or -s #g .. size in GB
           -S #  Set processor cache size to value (in kBytes)
           -t #  Number of threads or processes to use in throughput test
           -T  Use POSIX pthreads for throughput tests
           -u #  Upper limit on number of processes to run
           -U  Mount point to remount between tests
           -v  version information
           -V #  Verify data pattern write/read
           -w  Do not unlink temporary file
           -W  Lock file when reading or writing
           -x  Turn off stone-walling
           -X filename  Write telemetry file. Contains lines with (offset reclen compute_time) in ascii
           -y #  Set minimum record size (in kBytes) for auto mode (or #m or #g)
           -Y filename  Read  telemetry file. Contains lines with (offset reclen compute_time) in ascii
           -z  Used in conjunction with -a to test all possible record sizes
           -Z  Enable mixing of mmap I/O and file I/O
           -+b #,#  burst size (KB),sleep between burst (mili-second)
           -+E Use existing non-Iozone file for read-only testing
           -+F Truncate file before write in thread_mix_test
           -+J Include think time (-j #) in throughput calculation
           -+K Sony special. Manual control of test 8.
           -+m  Cluster_filename   Enable Cluster testing
           -+d  File I/O diagnostic mode. (To troubleshoot a broken file I/O subsystem)
           -+u  Enable CPU utilization output (Experimental)
           -+x # Multiplier to use for incrementing file and record sizes
           -+p # Percentage of mix to be reads
           -+r Enable O_RSYNC|O_SYNC for all testing.
           -+t Enable network performance test. Requires -+m
           -+n No retests selected.
           -+k Use constant aggregate data set size.
           -+q Delay in seconds between tests.
           -+l Enable record locking mode.
           -+L Enable record locking mode, with shared file.
           -+B Sequential mixed workload.
           -+D Enable O_DSYNC mode.
           -+A #  Enable madvise. 0 = normal, 1=random, 2=sequential
                                  3=dontneed, 4=willneed
           -+N Do not truncate existing files on sequential writes.
           -+S # Dedup-able data is limited to sharing within each numerically
                 identified file set.
           -+W # Add this value to the child thread ID, so that additional files
                 can be added while maintaining the proper dedupability with previously
                 existing files that are within the same seed group (-+S).
           -+V Enable shared file. No locking.
           -+X Enable short circuit mode for filesystem testing ONLY
               ALL Results are NOT valid in this mode.
           -+Z Enable old data set compatibility mode. WARNING.. Published
               hacks may invalidate these results and generate bogus, high
               values for results.
           -+w ## Percent of dedup-able data in buffers.
           -+y ## Percent of dedup-able within & across files in buffers.
           -+C ## Percent of dedup-able within & not across files in buffers.
           -+H Hostname    Hostname of the PIT server.
           -+P Service     Service  of the PIT server.
           -+z Enable latency histogram logging.
```
