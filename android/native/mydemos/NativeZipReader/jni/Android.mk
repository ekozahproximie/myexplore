# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# 

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)


#build the main shared library
LOCAL_MODULE := NativeZipReader

#LOCAL_BUILD_MODE=release 
 
# compile in ARM mode, since the glyph loader/renderer is a hotspot
# when loading complex pages in the browser 
#
LOCAL_ARM_MODE := arm
 
LOCAL_SRC_FILES := com_trimble_zipreader_NativeZipReader.cpp 
				   		

LOCAL_LDLIBS += -llog 


include $(BUILD_SHARED_LIBRARY)


