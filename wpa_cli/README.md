wpa_cli
========================================

1.先开启wpa_supplicant服务：

```
$ wpa_supplicant -B -iwlan0 -c /etc/wpa.../wpa...
```

2.新建一个network 0(默认递增):

```
wpa_cli -iwlan0 add_network 0
```

3.关联无线网络，不同的无线网络认证方式不同设置：

* 1).open(开放式认证方式，分为):

```
$ wpa_cli -iwlan0 set_network 0 ssid "wlan"
$ wpa_cli -iwlan0 set_network 0 key_mgmt NONE
```

* 2).wep（分为开放式和共享式）:

```
$ wpa_cli -iwlan0 set_network 0 ssid '"wlan"'
$ wpa_cli -iwlan0 set_network 0 key_mgmt NONE
$ wpa_cli -iwlan0 set_network 0 wep_key0 '"wlan_key"'
```

如果是共享式,还需要配置：wpa_cli -iwlan0 set_network 0 auth_alg SHARED(默认是开放式，可以不配置)

* 3). WPA/WPA2-Personal认证方式：

```
$ wpa_cli -iwlan0 set_network 0 ssid '"wlan"'
$ wpa_cli -iwlan0 set_network 0 key_mgmt WPA-PSK
$ wpa_cli -iwlan0 set_network 0 proto WPA(WPA2)
$ wpa_cli -iwlan0 set_network 0 pairwise TKIP(CCMP)
$ wpa_cli -iwlan0 set_network 0 group TKIP(CCMP)
$ wpa_cli -iwlan0 set_network 0 psk '"wlan_password"'
```

* 4). WPA/WPA2-Enterprise认证方式：

```
$ wpa_cli -iwlan0 set_network 0 ssid '"wlan"'
$ wpa_cli -iwlan0 set_network 0 key_mgmt WPA-EAP
$ wpa_cli -iwlan0 set_network 0 pairwise TKIP(CCMP)
$ wpa_cli -iwlan0 set_network 0 group TKIP(CCMP)
$ wpa_cli -iwlan0 set_network 0 eap PEAP
$ wpa_cli -iwlan0 set_network 0 identity '"username"'
$ wpa_cli -iwlan0 set_network 0 password '"password"'
```

如果是WPA2-Enterprise认证：需要设置proto：wpa_cli -iwlan0 set_network 0 proto WPA2
然后开始关联：wpa_cli -iwlan0 enable_network 0
如果dhcp获取ip地址，需要等关联上之后去dhcp；查看关联状态wpa_cli -iwlan0 status
如果状态是complete的话：dhcp wlan0

一般操作过程是，先查看无线网络，是否要关联的无线网络可见：

```
$ wpa_cli -iwlan0 scan
$ wpa_cli -iwlan0 scan_results
```

如果可见，则如上配置network 0，然后关联：

```
$ wpa_cli -iwlan0 enable_network 0
$ wpa_cli -iwlan0 status如果关联上，dhcp wlan0
```

之后如果解关联，需要先释放IP，然后up端口，然后解关联：

```
$ dhcp -r wlan0
$ ifconfig wlan0 up
$ wpa_cli -iwlan0 disable_network 0
```