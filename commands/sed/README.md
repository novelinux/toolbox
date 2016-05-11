sed
========================================

将www目录下所有文件中old字符串替换成new字符串.

```
$ sed -i "s/old/new/g" `grep old -rl www`
```