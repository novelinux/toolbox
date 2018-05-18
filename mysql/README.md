# MySql

安装MySQL

sudo apt-get install mysql-server

这个应该很简单了，而且我觉得大家在安装方面也没什么太大问题，所以也就不多说了，下面我们来讲讲配置。

配置MySQL

注意，在Ubuntu下MySQL缺省是只允许本地访问的，如果你要其他机器也能够访问的话，那么需要改变/etc/mysql/my.cnf配置文件了！下面我们一步步地来：

默认的MySQL安装之后根用户是没有密码的，所以首先用根用户进入：

$mysql -u root

在这里之所以用-u root是因为我现在是一般用户（firehare），如果不加-u root的话，mysql会以为是firehare在登录。注意，我在这里没有进入根用户模式，因为没必要。一般来说，对mysql中的数据库进行操作，根本没必要进入根用户模式，只有在设置时才有这种可能。

进入mysql之后，最要紧的就是要设置Mysql中的root用户密码了，否则，Mysql服务无安全可言了。

mysql> GRANT ALL PRIVILEGES ON *.* TO root@localhost IDENTIFIED BY "123456";

注意，我这儿用的是123456做为root用户的密码，但是该密码是不安全的，请大家最好使用大小写字母与数字混合的密码，且不少于8位。

这样的话，就设置好了MySQL中的root用户密码了，然后就用root用户建立你所需要的数据库。我这里就以xoops为例：

mysql>CREATE DATABASE xoops；

mysql>GRANT ALL PRIVILEGES ON xoops.* TO xoops_root@localhost IDENTIFIED BY "654321";

这样就建立了一个xoops_roots的用户，它对数据库xoops有着全部权限。以后就用xoops_root来对xoops数据库进行管理，而无需要再用root用户了，而该用户的权限也只被限定在xoops数据库中。

如果你想进行远程访问或控制，那么你要做两件事：

其一：

mysql>GRANT ALL PRIVILEGES ON xoops.* TO xoops_root@"%" IDENTIFIED BY "654321";

允许xoops_root用户可以从任意机器上登入MySQL。

其二：

$sudo gedit /etc/mysql/my.cnf

老的版本中

>skip-networking => # skip-networking

新的版本中

>bind-address=127.0.0.1 => bind-address= 你机器的IP

这样就可以允许其他机器访问MySQL了。