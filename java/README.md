## Install OpenJDK

```
sudo apt install mercurial
hg clone http://hg.openjdk.java.net/jdk8u/jdk8u60/
cd jdk8u60-d8f4022fe0
sh get_source.sh
```

### Ubuntu

```
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt-get install openjdk-8-jdk
sudo update-alternatives --config java
java -version
openjdk version "1.8.0_72-internal"
OpenJDK Runtime Environment (build 1.8.0_72-internal-b05)
OpenJDK 64-Bit Server VM (build 25.72-b05, mixed mode)
```