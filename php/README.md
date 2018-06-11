# Ubuntu 18.04 Install PHP

## Install

```
$ sudo aptitude purge `dpkg -l | grep php| awk '{print $2}' |tr "\n" " "`
Add the PPA

$ sudo add-apt-repository ppa:ondrej/php
Install your PHP Version

$ sudo apt-get update
$ sudo apt-get install php5.6
You can install php5.6 modules too ..

Verify your version

$ sudo php -v
```