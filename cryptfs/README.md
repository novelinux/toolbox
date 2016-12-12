Android Crypt
========================================

全盘加密
----------------------------------------

英文: FDE, Full-Disk Encryption
Android官方文档: https://source.android.com/security/encryption/full-disk.html
支持版本: 在Android 4.4引入, Android 5.0可选, Android 6.0强制

基于文件的加密
----------------------------------------

英文: FBE, File-Based Encryption
Android官方文档: https://source.android.com/security/encryption/file-based.html
支持版本: Android 7.0支持
主要作用: Android 7.0及更高版本支持基于文件的加密（FBE）。基于文件的加密允许不同文件被不同keys加密并且解锁也是独立的.

Different from encrypting full disk, file based encryption can encrypt each file via different keys. The most noticeable usage is that different user profile now using different keys.
Previously, different users share the same disk encryption key. OK, user/developer visible change is that we will have a so-called direct boot feature.
Previously after rebooting, a user has to unlock the entire disk (same password as the primary user), on a black screen with password input, and then unlock his own profile (mostly primary user).
Now, you can reboot directly to user profile unlock. No black screen unlocking any more. For developer, you can run your app in user0 space which doesn't require user's profile unlocked.
That means your app can run before user input their password and swipe to unlock.

### Flow Chart

#### mount_all

```
/* mount_all <fstab> [ <path> ]*
 *
 * This function might request a reboot, in which case it will
 * not return.
 */
static int do_mount_all(const std::vector<std::string>& args) {
      ...
    } else if (ret == FS_MGR_MNTALL_DEV_FILE_ENCRYPTED) {
        if (e4crypt_install_keyring()) {
            return -1;
        }
        property_set("ro.crypto.state", "encrypted");
        property_set("ro.crypto.type", "file");

        // Although encrypted, we have device key, so we do not need to
        // do anything different from the nonencrypted case.
        ActionManager::GetInstance().QueueEventTrigger("nonencrypted");
    } else if (ret > 0) {
        ERROR("fs_mgr_mount_all returned unexpected error %d\n", ret);
    }
    /* else ... < 0: error */

    return ret;
}
```

#### init.rc

```
on post-fs-data
    # We chown/chmod /data again so because mount is run as root + defaults
    chown system system /data
    chmod 0771 /data
    # We restorecon /data in case the userdata partition has been reset.
    restorecon /data

    ...

    # Make sure we have the device encryption key.
    start vold
    installkey /data

    # Start bootcharting as soon as possible after the data partition is
    # mounted to collect more data.
    mkdir /data/bootchart 0755 shell shell
    bootchart_init

    # Avoid predictable entropy pool. Carry over entropy from previous boot.
    copy /data/system/entropy.dat /dev/urandom
```

##### installkey

* 1.do_installkey

path: system/core/init/builtins.cpp
```
static int do_installkey(const std::vector<std::string>& args) {
    if (!is_file_crypto()) {
        return 0;
    }
    return e4crypt_create_device_key(args[1].c_str(),
                                     do_installkeys_ensure_dir_exists);
}
```

* 2.e4crypt_create_device_key

path: system/extras/ext4_utils/ext4_crypt_init_extensions.cpp
```
int e4crypt_create_device_key(const char* dir,
                              int ensure_dir_exists(const char*))
{
    init_logging();

    // Make sure folder exists. Use make_dir to set selinux permissions.
    std::string unencrypted_dir = std::string(dir) + e4crypt_unencrypted_folder;
    if (ensure_dir_exists(unencrypted_dir.c_str())) {
        KLOG_ERROR(TAG, "Failed to create %s (%s)\n",
                   unencrypted_dir.c_str(),
                   strerror(errno));
        return -1;
    }

    const char* argv[] = { "/system/bin/vdc", "--wait", "cryptfs", "enablefilecrypto" };
    int rc = android_fork_execvp(4, (char**) argv, NULL, false, true);
    LOG(INFO) << "enablefilecrypto result: " << rc;
    return rc;
}
```

* 3.enablefilecrypto

path: system/vold/cryptfs.c
```
int cryptfs_enable_file()
{
    return e4crypt_initialize_global_de();
}
```

* 4.e4crypt_initialize_global_de

path: system/vold/Ext4Crypt.cpp
```
bool e4crypt_initialize_global_de() {
    LOG(INFO) << "e4crypt_initialize_global_de";

    if (s_global_de_initialized) {
        LOG(INFO) << "Already initialized";
        return true;
    }

    std::string device_key;
    if (path_exists(device_key_path)) {
        if (!android::vold::retrieveKey(device_key_path,
                kEmptyAuthentication, &device_key)) return false;
    } else {
        LOG(INFO) << "Creating new key";
        if (!random_key(&device_key)) return false;
        if (!store_key(device_key_path, device_key_temp,
                kEmptyAuthentication, device_key)) return false;
    }

    std::string device_key_ref;
    if (!install_key(device_key, &device_key_ref)) {
        LOG(ERROR) << "Failed to install device key";
        return false;
    }

    std::string ref_filename = std::string("/data") + e4crypt_key_ref;
    if (!android::base::WriteStringToFile(device_key_ref, ref_filename)) {
        PLOG(ERROR) << "Cannot save key reference";
        return false;
    }

    s_global_de_initialized = true;
    return true;
}
```

* 5.install_key

path: system/vold/Ext4Crypt.cpp
```
// Install password into global keyring
// Return raw key reference for use in policy
static bool install_key(const std::string& key, std::string* raw_ref) {
    ext4_encryption_key ext4_key;
    if (!fill_key(key, &ext4_key)) return false;
    *raw_ref = generate_key_ref(ext4_key.raw, ext4_key.size);
    auto ref = keyname(*raw_ref);
    key_serial_t device_keyring;
    if (!e4crypt_keyring(&device_keyring)) return false;
    key_serial_t key_id =
        add_key("logon", ref.c_str(), (void*)&ext4_key, sizeof(ext4_key), device_keyring);
    if (key_id == -1) {
        PLOG(ERROR) << "Failed to insert key into keyring " << device_keyring;
        return false;
    }
    LOG(DEBUG) << "Added key " << key_id << " (" << ref << ") to keyring " << device_keyring
               << " in process " << getpid();
    return true;
}
```

##### mkdir

* do_mkdir

path: system/core/init/builtins.cpp
```
static int do_mkdir(const std::vector<std::string>& args) {
    mode_t mode = 0755;
    int ret;

    /* mkdir <path> [mode] [owner] [group] */

    if (args.size() >= 3) {
        mode = std::stoul(args[2], 0, 8);
    }

    ret = make_dir(args[1].c_str(), mode);
    /* chmod in case the directory already exists */
    if (ret == -1 && errno == EEXIST) {
        ret = fchmodat(AT_FDCWD, args[1].c_str(), mode, AT_SYMLINK_NOFOLLOW);
    }
    if (ret == -1) {
        return -errno;
    }

    if (args.size() >= 4) {
        uid_t uid = decode_uid(args[3].c_str());
        gid_t gid = -1;

        if (args.size() == 5) {
            gid = decode_uid(args[4].c_str());
        }

        if (lchown(args[1].c_str(), uid, gid) == -1) {
            return -errno;
        }

        /* chown may have cleared S_ISUID and S_ISGID, chmod again */
        if (mode & (S_ISUID | S_ISGID)) {
            ret = fchmodat(AT_FDCWD, args[1].c_str(), mode, AT_SYMLINK_NOFOLLOW);
            if (ret == -1) {
                return -errno;
            }
        }
    }

    if (e4crypt_is_native()) {
        if (e4crypt_set_directory_policy(args[1].c_str())) {
            wipe_data_via_recovery(std::string() + "set_policy_failed:" + args[1]);
            return -1;
        }
    }
    return 0;
}
```

* 2.e4crypt_is_native

path: path: system/extras/ext4_utils/ext4_crypt.cpp
```
bool e4crypt_is_native() {
    char value[PROPERTY_VALUE_MAX];
    property_get("ro.crypto.type", value, "none");
    return !strcmp(value, "file");
}
```

* 3.e4crypt_set_directory_policy

```
int e4crypt_set_directory_policy(const char* dir)
{
    init_logging();

    // Only set policy on first level /data directories
    // To make this less restrictive, consider using a policy file.
    // However this is overkill for as long as the policy is simply
    // to apply a global policy to all /data folders created via makedir
    if (!dir || strncmp(dir, "/data/", 6) || strchr(dir + 6, '/')) {
        return 0;
    }

    // Special case various directories that must not be encrypted,
    // often because their subdirectories must be encrypted.
    // This isn't a nice way to do this, see b/26641735
    std::vector<std::string> directories_to_exclude = {
        "lost+found",
        "system_ce", "system_de",
        "misc_ce", "misc_de",
        "media",
        "data", "user", "user_de",
    };
    std::string prefix = "/data/";
    for (auto d: directories_to_exclude) {
        if ((prefix + d) == dir) {
            KLOG_INFO(TAG, "Not setting policy on %s\n", dir);
            return 0;
        }
    }

    std::string ref_filename = std::string("/data") + e4crypt_key_ref;
    std::string policy;
    if (!android::base::ReadFileToString(ref_filename, &policy)) {
        KLOG_ERROR(TAG, "Unable to read system policy to set on %s\n", dir);
        return -1;
    }
    KLOG_INFO(TAG, "Setting policy on %s\n", dir);
    int result = e4crypt_policy_ensure(dir, policy.c_str(), policy.size());
    if (result) {
        KLOG_ERROR(TAG, "Setting %02x%02x%02x%02x policy on %s failed!\n",
                   policy[0], policy[1], policy[2], policy[3], dir);
        return -1;
    }

    return 0;
}
```

* 4.e4crypt_policy_ensure

```
int e4crypt_policy_ensure(const char *directory, const char *policy, size_t policy_length) {
    bool is_empty;
    if (!is_dir_empty(directory, &is_empty)) return -1;
    if (is_empty) {
        if (!e4crypt_policy_set(directory, policy, policy_length)) return -1;
    } else {
        if (!e4crypt_policy_check(directory, policy, policy_length)) return -1;
    }
    return 0;
}
```

* 5.e4crypt_policy_set

```
static bool e4crypt_policy_set(const char *directory, const char *policy, size_t policy_length) {
    if (policy_length != EXT4_KEY_DESCRIPTOR_SIZE) {
        LOG(ERROR) << "Policy wrong length: " << policy_length;
        return false;
    }
    int fd = open(directory, O_DIRECTORY | O_NOFOLLOW | O_CLOEXEC);
    if (fd == -1) {
        PLOG(ERROR) << "Failed to open directory " << directory;
        return false;
    }

    ext4_encryption_policy eep;
    eep.version = 0;
    eep.contents_encryption_mode = EXT4_ENCRYPTION_MODE_PRIVATE;
    eep.filenames_encryption_mode = EXT4_ENCRYPTION_MODE_AES_256_CTS;
    eep.flags = 0;
    memcpy(eep.master_key_descriptor, policy, EXT4_KEY_DESCRIPTOR_SIZE);
    if (ioctl(fd, EXT4_IOC_SET_ENCRYPTION_POLICY, &eep)) {
        PLOG(ERROR) << "Failed to set encryption policy for " << directory;
        close(fd);
        return false;
    }
    close(fd);

    char policy_hex[EXT4_KEY_DESCRIPTOR_SIZE_HEX];
    policy_to_hex(policy, policy_hex);
    LOG(INFO) << "Policy for " << directory << " set to " << policy_hex;
    return true;
}
```

直接引导模式(Direct Boot):
----------------------------------------

Android官方文档: https://developer.android.com/training/articles/direct-boot.html#notification

当设备通电但用户尚未解锁设备时，Android 7.0以安全的直接引导模式(Direct boot)运行。为了支持这一点，系统提供了两个数据存储位置：

* 凭证加密存储，它是默认存储位置，仅在用户解锁设备后可用。
* 设备加密存储，这是在直接引导模式期间和用户解锁设备后可用的存储位置。

默认情况下，应用程序不会在直接引导模式下运行。如果您的应用程序需要在直接引导模式下采取行动，您可以注册应在此模式下运行的应用程序组件。
在直接引导模式下需要运行的应用程序的一些常见用例包括：

* 已安排通知的应用程式，例如闹钟应用程式。
* 提供重要用户通知的应用，例如短信应用。
* 提供无障碍服务的应用，如Talkback。

如果您的应用程序需要在直接引导模式下运行时访问数据，请使用设备加密存储。设备加密存储包含使用仅在设备执行成功验证的引导后可用的密钥加密的数据。
对于应使用与用户凭据关联的密钥（如PIN或密码）加密的数据，请使用凭据加密存储。凭证加密存储仅在用户成功解锁设备后才可用，直到用户再次重新启动设备为止。
如果用户在解锁设备后启用锁定屏幕，则不会锁定凭证加密存储。
