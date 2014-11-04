# Copyright 2007-2008 The Android Open Source Project

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
# Include res dir from chips
chips_dir := ../../../frameworks/opt/chips/res
res_dirs := $(chips_dir) res

$(shell rm -f $(LOCAL_PATH)/chips)

LOCAL_MODULE_TAGS := optional

contacts_common_dir := ../ContactsCommon
chips_dir := ../../../frameworks/ex/chips

src_dirs := src $(contacts_common_dir)/src
res_dirs := res $(contacts_common_dir)/res $(chips_dir)/res

# Builds against the public SDK
#LOCAL_SDK_VERSION := current

LOCAL_JAVA_LIBRARIES += telephony-common
LOCAL_STATIC_JAVA_LIBRARIES += android-common jsr305
LOCAL_STATIC_JAVA_LIBRARIES += libchips

LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages com.android.contacts.common \
    --extra-packages com.android.ex.chips

LOCAL_JAVA_LIBRARIES := telephony-common mms-common
LOCAL_STATIC_JAVA_LIBRARIES := \
    com.android.phone.shared \
    com.android.vcard \
    android-common \
    guava \
    android-support-v13 \
    android-support-v4 \
    android-common jsr305 \
    android-common-chips \
    android-ex-variablespeed

LOCAL_REQUIRED_MODULES := libvariablespeed SoundRecorder

LOCAL_PACKAGE_NAME := Mms
LOCAL_CERTIFICATE := shared
LOCAL_PRIVILEGED_MODULE := true

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

# This finds and builds the test apk as well, so a single make does both.
include $(call all-makefiles-under,$(LOCAL_PATH))
