# Copyright 2007-2008 The Android Open Source Project

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

chips_dir := ../../../frameworks/ex/chips
contacts_common_dir := ../ContactsCommon

res_dirs := res $(contacts_common_dir)/src $(chips_dir)/res
src_dirs := src $(contacts_common_dir)/res

$(shell rm -f $(LOCAL_PATH)/chips)

LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages com.android.ex.chips \
    --extra-packages com.android.contacts.common \

LOCAL_JAVA_LIBRARIES += telephony-common mms-common
LOCAL_STATIC_JAVA_LIBRARIES := \
    android-common jsr305 \
    android-common-chips \

LOCAL_REQUIRED_MODULES := SoundRecorder

LOCAL_PACKAGE_NAME := Mms
LOCAL_PRIVILEGED_MODULE := true

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

# This finds and builds the test apk as well, so a single make does both.
include $(call all-makefiles-under,$(LOCAL_PATH))
