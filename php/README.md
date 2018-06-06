UBUNTU install PHP

一. PHP5安装
1.源码下载
http://php.net/downloads.php
我们选择的版本是php-5.6.36

2.配置编译
1.安装依赖
$ sudo apt-get install autoconf build-essential curl libtool \
  libssl-dev libcurl4-openssl-dev libxml2-dev libreadline7 \
  libreadline-dev libzip-dev libzip4 openssl  \
  pkg-config zlib1g-dev

2.源码编译PHP5
A.安装openssl

https://www.openssl.org/source/ 下载openssl-1.0.2o

$ cd openssl-1.0.2o
$ ./config --prefix=$HOME/bin/libraries/openssl-1.0.2o
$ make -j8 && make install
B.安装curl

$ wget https://curl.haxx.se/download/curl-7.60.0.tar.gz
$ tar -xvf curl-7.60.0.tar.gz
$ ./configure --prefix=$HOME/bin/libraries/curl-7.60.0
$ make -j8 && make install
C.安装php5

./configure --prefix=$HOME/bin/php5-latest \
    --enable-mysqlnd \
    --with-pdo-mysql \
    --with-pdo-mysql=mysqlnd \
    --enable-bcmath \
    --enable-fpm \
    --with-fpm-user=www-data \
    --with-fpm-group=www-data \
    --enable-mbstring \
    --enable-phpdbg \
    --enable-shmop \
    --enable-sockets \
    --enable-sysvmsg \
    --enable-sysvsem \
    --enable-sysvshm \
    --enable-zip \
    --enable-pcntl \
    --with-libzip=/usr/lib/x86_64-linux-gnu \
    --with-zlib \
    --with-curl=$HOME/bin/libraries/curl-7.60.0 \
    --with-pear \
    --with-openssl=$HOME/bin/libraries/openssl-1.0.2o \
    --with-readline \
    --with-mcrypt==$HOME/bin/libraries/mcrypt-2.6.8
拷贝关键文件：

$ cp php.ini-development ~/bin/php5-latest/lib/php.ini
$ cd ~/bin/php5-latest/etc
$ mv php-fpm.conf.default php-fpm.conf
$ mv php-fpm.d/www.conf.default php-fpm.d/www.conf
添加到环境变量到~/.bashrc文件

export PATH=~/bin/php5-latest/bin:$PATH
export PATH=~/bin/php5-latest/sbin:$PATH
二.yaf安装

1.源码下载
http://pecl.php.net/package/yaf

2.配置编译
$ sudo apt install autoconf
编译yaf

liminghao@domingo:~/Downloads/yaf-2.3.5$ phpize
Configuring for:
PHP Api Version:         20131106
Zend Module Api No:      20131226
Zend Extension Api No:   220131226
liminghao@domingo:~/Downloads/yaf-2.3.5$ ./configure --with-php-config=$HOME/bin/php5-latest/bin/php-config
$ make -j8
$ sudo make install
三.本机测试运行
按照如下修改~/bin/php5-latest/etc/php-fpm.conf

pid = run/php-fpm.pid
...
user = liminghao # 在这里将user和group修改成你自己的
group = liminghao
按照如下修改~/bin/php5-latest/lib/php.ini

date.timezone =Asia/Shanghai
...
extension=yaf.so
;extension=zookeeper.so
yaf.library=/home/liminghao/data/mi/iot/miio_cloud/phpapp/phplib/xiaomi/
在终端执行如下命令

$ touch ~/bin/php5-latest/var/run/php-fpm.pid
liminghao@domingo:~/bin/php-latest/etc$ sudo ~/bin/php5-latest/sbin/php-fpm
liminghao@domingo:~/bin/php-latest/etc$ ps -aux | grep php-fpm
root      1229  0.0  0.0 193340 11260 ?        Ss   10:03   0:00 php-fpm: master process (/home/liminghao/bin/php-latest/etc/php-fpm.conf)
www-data  1234  0.0  0.0 195640 11152 ?        S    10:03   0:00 php-fpm: pool www
www-data  1235  0.0  0.0 195640 11152 ?        S    10:03   0:00 php-fpm: pool www
limingh+  1242  0.0  0.0  21536  1148 pts/2    S+   10:03   0:00 grep php-fpm

注意: 停用php-fpm:
liminghao@domingo:~/bin/php5-latest/var/run$ sudo kill -INT `cat php-fpm.pid`
1.安装nginx

$ wget http://nginx.org/download/nginx-1.13.12.tar.gz
$ tar -zxvf nginx-1.13.12.tar.gz
$ cd nginx-1.13.12

$ ./configure --sbin-path=/usr/local/nginx/nginx --conf-path=/usr/local/nginx/nginx.conf --pid-path=/usr/local/nginx/nginx.pid --with-http_ssl_module

$ make
$ sudo make install

配置/usr/local/nginx/conf/nginx.conf

user  liminghao; # 修改成你自己的
...

http {
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';
	...
    server {
        ....
        location ~* \.php$ {
            fastcgi_index   index.php;
            fastcgi_pass    127.0.0.1:9000;
            include         fastcgi_params;
            fastcgi_param   SCRIPT_FILENAME    $document_root$fastcgi_script_name;
            fastcgi_param   SCRIPT_NAME        $fastcgi_script_name;
        }

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
	}

	...

    # Adding follow configurations for IOT
    server {
        listen 80;
        listen 8080;
        server_name  support.io.mi.com;
        root /home/liminghao/data/mi/iot/supportfe/;
        index   index.php;
        access_log  /home/liminghao/data/mi/iot/logs/nginx/support.log main;

        location ~* ^/?$ {
            root /home/liminghao/data/mi/iot/miio_cloud/phpapp/webroot/support/;
        }

        #导出excel文件配置
        #location ~* \.(xls|xlsx|csv)$ {
        #    root /home/liminghao/data/mi/iot/miio_cloud/phpapp/webroot/support/;
        #    add_header Content-Disposition: "attachment";
        #    autoindex on;
        #    autoindex_exact_size on;
        #    autoindex_localtime on;
        #}

        location / {
            #if (-f $request_filename.html) {
            #    rewrite ^(.+) /$1.html last;
            #}

            if ($request_filename ~ \.html$) {
                add_header Cache-Control "no-cache";
            }

            if (!-e $request_filename) {
                rewrite ^(.+) /index.php?$1 last;
            }
        }

        location ~* (^.+\.php$)|(^.+\.php\?.*)|(^.+\.php/.*) {
            root /home/liminghao/data/mi/iot/miio_cloud/phpapp/webroot/support/;
            fastcgi_pass   127.0.0.1:9000;
            fastcgi_index  index.php;
            fastcgi_split_path_info ^(.+\.php)(.*)$;
            fastcgi_param  SCRIPT_FILENAME  $document_root$fastcgi_script_name;
            include        fastcgi_params;
        }
    }

    server {
        listen 80;
        listen 8080;
        server_name  biz.home.com;
        root   /home/liminghao/data/mi/iot/miio_cloud/phpapp/webroot/biz/;
        index   index.php index.html;
        access_log  /home/liminghao/data/mi/iot/logs/nginx/biz.log main;

        location / {
            if (!-e $request_filename) {
                 rewrite ^(.+) /index.php?$1 last;
            }
        }

        location ~* (^.+\.php$)|(^.+\.php\?.*)|(^.+\.php/.*) {
            fastcgi_pass   127.0.0.1:9000;
            fastcgi_index  index.php;
            fastcgi_split_path_info ^(.+\.php)(.*)$;
            fastcgi_param  SCRIPT_FILENAME  $document_root$fastcgi_script_name;
            include        fastcgi_params;
        }
    }

    server {
        listen 80;
        listen 8080;
        server_name  testopen.home.mi.com;
        # cd open-developer
        # fis3 release -d dist-test
        root /home/liminghao/data/mi/iot/open-developer/dist-test/;
        index   index.php;
        access_log  /home/liminghao/data/mi/iot/logs/nginx/testopen.log main;

        #导出excel文件配置
        location ~* \.(xls|xlsx|csv)$ {
            root /home/liminghao/data/mi/iot/miio_cloud/phpapp/webroot/dev/;
            add_header Content-Disposition: "attachment";
            autoindex on;
            autoindex_exact_size on;
            autoindex_localtime on;
        }

        location ~* ^/?$ {
            root /home/liminghao/data/mi/iot/miio_cloud/phpapp/webroot/dev/;
        }

        location / {
            if (-f $request_filename.html) {
                rewrite ^(.+) /$1.html last;
            }
            if ($request_filename ~ \.html$) {
                add_header Cache-Control "no-cache";
            }
            if (!-e $request_filename) {
                rewrite ^(.+) /index.php?$1 last;
            }
        }

        location ~* (^.+\.php$)|(^.+\.php\?.*)|(^.+\.php/.*){
            root /home/liminghao/data/mi/iot/miio_cloud/phpapp/webroot/dev/;
            fastcgi_pass   127.0.0.1:9000;
            fastcgi_index  index.php;
            fastcgi_split_path_info ^(.+\.php)(.*)$;
            fastcgi_param  SCRIPT_FILENAME  $document_root$fastcgi_script_name;
            include        fastcgi_params;
        }
    }

    server {
        listen 8088;
        server_name  abroad.mi.com;
        index   index.php;
        root /home/liminghao/data/mi/iot/miio_cloud/phpapp/webroot/abroad/;

        location / {
            if (!-e $request_filename) {
                rewrite ^(.+) /index.php?$1 last;
            }
        }

        location ~* (^.+\.php$)|(^.+\.php\?.*)|(^.+\.php/.*) {
            fastcgi_pass   127.0.0.1:9000;
            fastcgi_index  index.php;
            fastcgi_split_path_info ^(.+\.php)(.*)$;
            fastcgi_param  SCRIPT_FILENAME  $document_root$fastcgi_script_name;
            include        fastcgi_params;
        }
    }

    server {
        listen 80;
        server_name  testhomeweb.mi.com;
        root  /home/liminghao/data/mi/iot/homeweb/dist/;
        index   index.html;

        location ~* ^/?$ {
            proxy_pass http://t.home.mi.com;
        }

        location / {
            if ($request_filename ~ \.html$) {
                add_header Cache-Control "no-cache";
            }

            if (!-e $request_filename){
                proxy_pass http://t.home.mi.com;
            }
        }
    }
启动nginx

$ sudo /usr/local/nginx/nginx -s stop
$ sudo /usr/local/nginx/nginx
创建测试文件。

$ echo "<?php phpinfo(); ?>" >> /usr/local/nginx/html/hello.php
打开浏览器，访问 http://localhost/hello.php

四.搭建MIIO开发环境
1.创建用户组和用户, 以及一些必须的目录

$ sudo addgroup work
$ sudo useradd work -g work -m
$ passwd work # 设置work的密码
$ su work
$ cd ~
$ mkdir -p ~/data/logs/phplog/third
2.在miio_cloud中添加global.ini文件，内容如下所示

$ cat ./phpapp/conf/global.ini
idc=local
3.数据库配置

$ sudo apt-get install mysql-server
$ sudo mysql -u root
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 5
Server version: 5.7.22-0ubuntu18.04.1 (Ubuntu)

Copyright (c) 2000, 2018, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> SET PASSWORD FOR 'root'@'localhost' = PASSWORD('root_password'); # 设置root用户密码.
Query OK, 0 rows affected, 2 warnings (0.00 sec)

mysql> create database miio;
Query OK, 1 row affected (0.00 sec)

mysql> GRANT ALL PRIVILEGES ON miio.* TO miio@localhost IDENTIFIED BY "helloxiaomi";
Query OK, 0 rows affected, 1 warning (0.00 sec)


local.master_ip = 127.0.0.1
local.master_port = 3306
local.user_write = miio
local.pass_write = helloxiaomi
local.user_read = miio
local.pass_read = helloxiaomi
local.db = miio


$ mysql -umiio -phelloxiaomi miio < miio_test.sql


3.在浏览器中输入http://support.io.mi.com/ 进行测试

$ sudo apt-get install mysql-server
$ sudo mysql -u root
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 5
Server version: 5.7.22-0ubuntu18.04.1 (Ubuntu)

Copyright (c) 2000, 2018, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> SET PASSWORD FOR 'root'@'localhost' = PASSWORD('root_password'); # 设置root用户密码.
Query OK, 0 rows affected, 2 warnings (0.00 sec)

mysql> create database miio;
Query OK, 1 row affected (0.00 sec)

mysql> GRANT ALL PRIVILEGES ON miio.* TO miio@localhost IDENTIFIED BY "helloxiaomi";
Query OK, 0 rows affected, 1 warning (0.00 sec)


local.master_ip = 127.0.0.1
local.master_port = 3306
local.user_write = miio
local.pass_write = helloxiaomi
local.user_read = miio
local.pass_read = helloxiaomi
local.db = miio


$ mysql -umiio -phelloxiaomi miio < miio_test.sql


3.在浏览器中输入http://support.io.mi.com/ 进行测试
