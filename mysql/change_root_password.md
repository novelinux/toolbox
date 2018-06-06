Centos系统mysql 忘记root用户的密码：
第一步：(停掉正在运行的mysql)
[root@CentOs5 ~]# service mysqld stop
Stopping MySQL:                                            [  OK  ]

第二步：使用 “--skip-grant-tables”参数重新启动mysql
[root@CentOs5 ~]# mysqld_safe --skip-grant-tables &
[1] 23810
[root@CentOs5 ~]# Starting mysqld daemon with databases from /var/lib/mysql

第三步：用帐号登录mysql
[root@CentOs5 ~]# mysql -u root
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 1
Server version: 5.0.77 Source distribution
Type 'help;' or '\h' for help. Type '\c' to clear the buffer.

第四步：改变用户数据库
mysql> use mysql
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A
Database changed

第五步：修改密码，记得密码要用password()函数进行加密，一定不要忘记！！！
mysql> update user set password=password('admin123') where user='root';
Query OK, 1 row affected (0.04 sec)
Rows matched: 1  Changed: 1  Warnings: 0

第六步：刷新权限表
mysql> flush previleges;
ERROR 1064 (42000): You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'previleges' at line 1
mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)

第七步：退出mysql
mysql> quit
Bye

第八步：对mysql进行重启
[root@CentOs5 ~]# service mysqld restart;
STOPPING server from pid file /var/run/mysqld/mysqld.pid
100421 13:44:03  mysqld ended
Stopping MySQL:                                            [  OK  ]
Starting MySQL:                                            [  OK  ]
[1]+  Done                    mysqld_safe --skip-grant-tables

第九步：用更改过的密码重新登录即可。
[root@CentOs5 ~]# mysql -u root -p
Enter password: admin123
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.0.77 Source distribution
Type 'help;' or '\h' for help. Type '\c' to clear the buffer.
mysql> quit
Bye
[root@CentOs5 ~]#