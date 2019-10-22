CMake编译时如果需要选择c++编译器版本，需要添加：add_compile_options(-std=c++11)。

如果需要编译出共享库而不是静态库，执行cmake命令时使用cmake .. -DBUILD_SHARED_LIBS=ON。

使用CMake编译的程序，也可以使用gdb进行调试。方法如下。

CMakeList.txt文件前面添加内容：
```
SET(CMAKE_BUILD_TYPE "Debug")
SET(CMAKE_CXX_FLAGS_DEBUG "$ENV{CXXFLAGS} -O0 -Wall -g -ggdb")
SET(CMAKE_CXX_FLAGS_RELEASE "$ENV{CXXFLAGS} -O3 -Wall")
```
重新编译
在build目录下执行编译命令：

```
cmake .. -DCMAKE_BUILD_TYPE=Debug
make
```

启动调试

```
gdb exe #exe为可执行文件
```