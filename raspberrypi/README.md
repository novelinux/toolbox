# Raspberrypi

## Images

https://www.raspberrypi.org/downloads/

## SSH

### Open ssh

```
$ mkdir ssh # on boot partion
```

### Connect

```
$ ssh pi@raspberrypi.local
```

## Sources.list

```
中科大
deb http://mirrors.ustc.edu.cn/raspbian/raspbian/ stretch main contrib non-free rpi
清华
deb https://mirrors.tuna.tsinghua.edu.cn/raspbian/raspbian/ stretch main contrib non-free rpi
```

## WIFI

/etc/wpa_supplicant/wpa_supplicant.conf

###

```
network={
    ssid="ssid"
    psk="password"
}
```

### WPA-EAP

```
network={
	ssid="ssid"
	key_mgmt=WPA-EAP IEEE8021X
	eap=PEAP
	identity="username"
	password="password"
	priority=1
	proactive_key_caching=1
}
```