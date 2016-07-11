LOCAL_PATH := $(call my-dir)

# target
include $(CLEAR_VARS)

LOCAL_SRC_FILES := test.c
LOCAL_STATIC_LIBRARIES := libcutils
LOCAL_C_INCLUDES := system/core/include
LOCAL_CFLAGS := -g
LOCAL_MODULE := test_perf

include $(BUILD_EXECUTABLE)
