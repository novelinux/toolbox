1、获取有关权限管理的帮助

```
[sql] view plain copy
root@localhost[(none)]> help Account Management
For more information, type 'help <item>', where <item> is one of the following
topics:
You asked for help about help category: "Account Management"
   CREATE USER
   DROP USER
   GRANT
   RENAME USER
   REVOKE
   SET PASSWORD
```

2、创建mysql数据库用户

```
[sql] view plain copy
--创建用户的语法
root@localhost[(none)]> help create user;
Name: 'CREATE USER'
Description:
Syntax:
CREATE USER user_specification [, user_specification] ...

user_specification:
    user
    [
      | IDENTIFIED WITH auth_plugin [AS 'auth_string']
        IDENTIFIED BY [PASSWORD] 'password'
    ]

create user命令会创建一个新帐户，同时也可以为其指定密码。该命令将添加一条记录到user表。
该命令仅仅授予usage权限。需要再使用grant命令进行进一步授权。也可以使用grant命令直接来创建账户见后续的相关演示。
下面是mysql官方手册对usage的解释。
The USAGE privilege specifier stands for “no privileges.” It is used at the global level with
GRANT to modify account attributes such as resource limits or SSL characteristics without affecting
existing account privileges.

--当前演示环境
root@localhost[(none)]> show variables like 'version';
+---------------+------------+
| Variable_name | Value      |
+---------------+------------+
| version       | 5.5.39-log |
+---------------+------------+

--创建新用户(未指定密码)
root@localhost[(none)]> create user 'fred'@'localhost';
Query OK, 0 rows affected (0.00 sec)

--指定密码创建新用户,%表示任意，即frank可以从任意主机访问数据库
root@localhost[(none)]> create user 'frank'@'%' identified by 'frank';
Query OK, 0 rows affected (0.00 sec)

--查看刚刚添加的账户
root@localhost[(none)]> select host,user,password from mysql.user where user like 'fr%';
+-----------+-------+-------------------------------------------+
| host      | user  | password                                  |
+-----------+-------+-------------------------------------------+
| %         | frank | *63DAA25989C7E01EB96570FA4DBE154711BEB361 |
| localhost | fred  |                                           |
+-----------+-------+-------------------------------------------+
```

3、使用grant授予权限

```
[sql] view plain copy
--grant命令语法
root@localhost[mysql]> help grant
Name: 'GRANT'
Description:
Syntax:
GRANT
    priv_type [(column_list)]
      [, priv_type [(column_list)]] ...
    ON [object_type] priv_level
    TO user_specification [, user_specification] ...
    [REQUIRE {NONE | ssl_option [[AND] ssl_option] ...}]
    [WITH with_option ...]

GRANT PROXY ON user_specification
    TO user_specification [, user_specification] ...
    [WITH GRANT OPTION]

object_type:
    TABLE
  | FUNCTION
  | PROCEDURE

priv_level:
    *
  | *.*
  | db_name.*
  | db_name.tbl_name
  | tbl_name
  | db_name.routine_name

user_specification:
    user
    [
      | IDENTIFIED WITH auth_plugin [AS 'auth_string']
        IDENTIFIED BY [PASSWORD] 'password'
    ]

如何授权
  a、需要指定授予哪些权限
  b、权限应用在那些对象上(全局，特定对象等)
  c、授予给哪个帐户
  d、可以指定密码(可选项,用此方式会自动创建用户)

授权权限的范围：
  ON　*.*
  ON  db_name.*
  ON  db_name.table_name
  ON  db_name.table_name.column_name
  ON  db_name.routine_name

--权限一览表，我们直接查询root账户所有的权限，如下
--mysql的权限相对于oracle而言，相对简单，而且也没有涉及到角色方面的定义与配置
root@localhost[(none)]> select * from mysql.user where user='root' and host='localhost'\G
*************************** 1. row ***************************
                  Host: localhost
                  User: root
              Password:
           Select_priv: Y
           Insert_priv: Y
           Update_priv: Y
           Delete_priv: Y
           Create_priv: Y
             Drop_priv: Y
           Reload_priv: Y
         Shutdown_priv: Y
          Process_priv: Y
             File_priv: Y
            Grant_priv: Y
       References_priv: Y
            Index_priv: Y
            Alter_priv: Y
          Show_db_priv: Y
            Super_priv: Y
 Create_tmp_table_priv: Y
      Lock_tables_priv: Y
          Execute_priv: Y
       Repl_slave_priv: Y
      Repl_client_priv: Y
      Create_view_priv: Y
        Show_view_priv: Y
   Create_routine_priv: Y
    Alter_routine_priv: Y
      Create_user_priv: Y
            Event_priv: Y
          Trigger_priv: Y
Create_tablespace_priv: Y
              ssl_type:
            ssl_cipher:
           x509_issuer:
          x509_subject:
         max_questions: 0
           max_updates: 0
       max_connections: 0
  max_user_connections: 0
                plugin:
 authentication_string:
1 row in set (0.00 sec)

--说明，本文中描述的mysql提示符为user@hostname[(dbname)]，不同的帐户，不同的主机登录会显示不同。
--其次，不同的提示符下所代表的用户身份及权限。
--查看当前的连接用户
root@localhost[(none)]> select current_user();
+----------------+
| current_user() |
+----------------+
| root@localhost |
+----------------+

--查看当前帐户的权限
root@localhost[(none)]> show grants;  --该账户用于最高权限，带有WITH GRANT OPTION
+---------------------------------------------------------------------+
| Grants for root@localhost                                           |
+---------------------------------------------------------------------+
| GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION |
| GRANT PROXY ON ''@'' TO 'root'@'localhost' WITH GRANT OPTION        |
+---------------------------------------------------------------------+

suse11b:~ # mysql -ufred -p
Enter password:

fred@localhost[(none)]> show grants;
+------------------------------------------+
| Grants for fred@localhost                |
+------------------------------------------+
| GRANT USAGE ON *.* TO 'fred'@'localhost' |
+------------------------------------------+

--下面使用root账户给fred赋予权限all privileges
root@localhost[(none)]> grant all privileges on *.* to 'fred'@'localhost';
Query OK, 0 rows affected (0.01 sec)

root@localhost[(none)]> flush privileges;
Query OK, 0 rows affected (0.00 sec)

fred@localhost[(none)]> show grants;
+---------------------------------------------------+
| Grants for fred@localhost                         |
+---------------------------------------------------+
| GRANT ALL PRIVILEGES ON *.* TO 'fred'@'localhost' |
+---------------------------------------------------+

fred@localhost[(none)]> use tempdb

fred@localhost[tempdb]> create table tb_isam(id int,value varchar(20)) engine=myisam;
Query OK, 0 rows affected (0.10 sec)

fred@localhost[tempdb]> insert into tb_isam values (1,'jack'),(2,'robin');
Query OK, 2 rows affected (0.00 sec)
Records: 2  Duplicates: 0  Warnings: 0

fred@localhost[tempdb]> commit;

--下面的授权收到了错误提示，不能授权
fred@localhost[tempdb]> grant select on tempdb.* to 'frank'@'%';
ERROR 1044 (42000): Access denied for user 'fred'@'localhost' to database 'tempdb'

--下面从root session来给之前创建的frank授权
--授予frank在数据库tempdb上所有对象的select权限
root@localhost[(none)]> grant select on tempdb.* to 'frank'@'%';
Query OK, 0 rows affected (0.00 sec)

--更新cache中的权限
root@localhost[(none)]> flush privileges;
Query OK, 0 rows affected (0.00 sec)

--从另外的主机使用frank账户登录
suse11a:~ # mysql -ufrank -p -h172.16.6.89
Enter password:

--此时frank，此时已经可以访问了tempdb上的表tb_isam
frank@172.16.6.89[(none)]> select * from tempdb.tb_isam;
+------+-------+
| id   | value |
+------+-------+
|    1 | jack  |
|    2 | robin |
+------+-------+

frank@172.16.6.89[(none)]> show grants;
+------------------------------------------------------------------------------------------------------+
| Grants for frank@%                                                                                   |
+------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO 'frank'@'%' IDENTIFIED BY PASSWORD '*63DAA25989C7E01EB96570FA4DBE154711BEB361' |
| GRANT SELECT ON `tempdb`.* TO 'frank'@'%'          --可以看到多出了select权限                         |
+------------------------------------------------------------------------------------------------------+

--下面是一个授予最大权限的例子，授予的同时会自动创建用户，由于我们没有设置密码，所以password列查询结果为空
root@localhost[(none)]> grant all privileges on *.* to 'jack'@'localhost';
Query OK, 0 rows affected (0.00 sec)    --第一个*号代表任意数据库，第二个*号代表数据库上的任意对象

root@localhost[(none)]> select user,host,Password from mysql.user where user='jack';
+------+-----------+----------+
| user | host      | Password |
+------+-----------+----------+
| jack | localhost |          |
+------+-----------+----------+

suse11b:~ # mysql -ujack -p -h localhost
Enter password:

jack@localhost[(none)]> show grants for current_user; --该方式等同于show grants，查看自身权限
+---------------------------------------------------+
| Grants for jack@localhost                         |
+---------------------------------------------------+
| GRANT ALL PRIVILEGES ON *.* TO 'jack'@'localhost' |
+---------------------------------------------------+

--在当前session下查看其它用户的权限，注，当前session登陆的用户也需要有权限才能查看其它用户权限
jack@localhost[(none)]> show grants for 'frank'@'%';
+------------------------------------------------------------------------------------------------------+
| Grants for frank@%                                                                                   |
+------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO 'frank'@'%' IDENTIFIED BY PASSWORD '*63DAA25989C7E01EB96570FA4DBE154711BEB361' |
| GRANT SELECT ON `tempdb`.* TO 'frank'@'%'                                                            |
+------------------------------------------------------------------------------------------------------+

--下面演示基于对象列级别的授权
--首先revoke之前的select权限
root@localhost[(none)]> revoke select on tempdb.* from 'frank'@'%';
Query OK, 0 rows affected (0.00 sec)

fred@localhost[tempdb]> create table tb_user as select * from mysql.user;
Query OK, 9 rows affected (0.15 sec)
Records: 9  Duplicates: 0  Warnings: 0

fred@localhost[tempdb]> grant select(user,host),update(host) on tempdb.tb_user to 'frank'@'%';
ERROR 1142 (42000): GRANT command denied to user 'fred'@'localhost' for table 'tb_user' --授权失败

--下面使用root来授权
root@localhost[(none)]> grant select(user,host),update(host) on tempdb.tb_user to 'frank'@'%';
Query OK, 0 rows affected (0.00 sec)

root@localhost[(none)]> flush privileges;
Query OK, 0 rows affected (0.00 sec)

--下面检查一下frank所拥有的权限
root@localhost[(none)]> show grants for 'frank';
+------------------------------------------------------------------------------------------------------+
| Grants for frank@%                                                                                   |
+------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO 'frank'@'%' IDENTIFIED BY PASSWORD '*63DAA25989C7E01EB96570FA4DBE154711BEB361' |
| GRANT SELECT (user, host), UPDATE (host) ON `tempdb`.`tb_user` TO 'frank'@'%'                        |
+------------------------------------------------------------------------------------------------------+

--下面使用frank身份来验证所授予的权限
frank@172.16.6.89[(none)]> desc tempdb.tb_user;
+-------+----------+------+-----+---------+-------+
| Field | Type     | Null | Key | Default | Extra |
+-------+----------+------+-----+---------+-------+
| Host  | char(60) | NO   |     |         |       |
| User  | char(16) | NO   |     |         |       |
+-------+----------+------+-----+---------+-------+

frank@172.16.6.89[(none)]> select * from tempdb.tb_user;   --访问时不支持通配符，必须指定列名
ERROR 1142 (42000): SELECT command denied to user 'frank'@'suse11a.site' for table 'tb_user'

frank@172.16.6.89[(none)]> select host,user from tempdb.tb_user where user='frank';
+------+-------+
| host | user  |
+------+-------+
| %    | frank |
+------+-------+

--需要注意的是，如果你的对象创建在test相关数据库下，权限限制可能会失效。
--下面这个查询用于查看db的授权表
root@localhost[(none)]> select host,db,user from mysql.db;
+------+---------+------+
| host | db      | user |
+------+---------+------+
| %    | test    |      |
| %    | test\_% |      |
+------+---------+------+

--根据前面的权限授予,列host可以被更新，而列user不行，如下面的2条SQL语句执行的结果
frank@172.16.6.89[(none)]> update tempdb.tb_user set host='localhost' where user='frank';
Query OK, 1 row affected (0.12 sec)
Rows matched: 1  Changed: 1  Warnings: 0

frank@172.16.6.89[(none)]> update tempdb.tb_user set user='jason' where user='jack';
ERROR 1143 (42000): UPDATE command denied to user 'frank'@'suse11a.site' for column 'user' in table 'tb_user'

--关于WITH GRANT OPTION
root@localhost[(none)]> show grants;   --注意root下有WITH GRANT OPTION
+---------------------------------------------------------------------+
| Grants for root@localhost                                           |
+---------------------------------------------------------------------+
| GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION |
| GRANT PROXY ON ''@'' TO 'root'@'localhost' WITH GRANT OPTION        |
+---------------------------------------------------------------------+

root@localhost[(none)]> show grants for 'jack'@'localhost'; --注意jack下没有WITH GRANT OPTION
+---------------------------------------------------+       --这就是前面为什么用户自身创建的对象而无法授权的问题
| Grants for jack@localhost                         |
+---------------------------------------------------+
| GRANT ALL PRIVILEGES ON *.* TO 'jack'@'localhost' |
+---------------------------------------------------+<span style="font-family:'Courier New';">         </span>
4、撤销权限

[sql] view plain copy
撤销权限使用的是revoke关键字，撤销与授权的权限方式基本类似，
其次有哪些权限可以授予，相应地就有哪些权限可以撤销，原来的to子句呢则变成了from子句。
如下面的示例
mysql> revoke SELECT (user, host), UPDATE (host) ON `tempdb`.`tb_user` from 'frank'@'%';
mysql> revoke all privileges, grant option from 'frank'@'%';

root@localhost[(none)]> revoke SELECT (user, host), UPDATE (host) ON `tempdb`.`tb_user` from 'frank'@'%';
Query OK, 0 rows affected (0.00 sec)

-- Author : Leshami
-- Blog   : http://blog.csdn.net/leshami

root@localhost[(none)]> revoke all privileges, grant option from 'frank'@'%';
Query OK, 0 rows affected (0.01 sec)

root@localhost[(none)]> flush privileges;
Query OK, 0 rows affected (0.00 sec)

root@localhost[(none)]> show grants for 'frank';  --查看revoke之后仅拥有最基本权限
+------------------------------------------------------------------------------------------------------+
| Grants for frank@%                                                                                   |
+------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO 'frank'@'%' IDENTIFIED BY PASSWORD '*63DAA25989C7E01EB96570FA4DBE154711BEB361' |
+------------------------------------------------------------------------------------------------------+
5、删除及重命名账户

[sql] view plain copy
使用drop user命令删除用户
--查看当前系统中已存在的用户
root@localhost[(none)]> select user,host,Password from mysql.user;
+-------+-----------+-------------------------------------------+
| user  | host      | Password                                  |
+-------+-----------+-------------------------------------------+
| root  | localhost |                                           |
| root  | suse11b   |                                           |
| root  | 127.0.0.1 |                                           |
| root  | ::1       |                                           |
|       | localhost |                                           |
|       | suse11b   |                                           |
| fred  | localhost |                                           |
| frank | %         | *63DAA25989C7E01EB96570FA4DBE154711BEB361 |
| jack  | localhost |                                           |
+-------+-----------+-------------------------------------------+

--使用drop user命令删除用户
root@localhost[(none)]> drop user 'frank'@'%';
Query OK, 0 rows affected (0.00 sec)

root@localhost[(none)]> drop user 'fred'@'localhost';
Query OK, 0 rows affected (0.00 sec)

root@localhost[(none)]> select user,host,Password from mysql.user where user like 'fr%';
Empty set (0.00 sec)

--如何重命名帐户，使用rename user命令
root@localhost[(none)]> rename user 'jack'@'localhost' to 'jason'@'localhost';
Query OK, 0 rows affected (0.00 sec)

root@localhost[(none)]> select user,host,Password from mysql.user where user like 'j%';
+-------+-----------+----------+
| user  | host      | Password |
+-------+-----------+----------+
| jason | localhost |          |
+-------+-----------+----------+
```

--对于用户的删除也可以直接从mysql.user进行删除相应的记录，但不推荐直接操作mysql系统表