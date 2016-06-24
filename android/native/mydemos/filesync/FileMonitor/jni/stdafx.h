#pragma once
#include <stdio.h>
#include <malloc.h>
#include <stdlib.h>
#include <math.h>

#include <sys/types.h>
#include <sys/time.h>
#include <pthread.h>
#include <semaphore.h>
#include <wchar.h> 
#include <android/log.h>
#include <jni.h>

#define LOG_DATA_NDK

#ifdef LOG_DATA_NDK
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#endif

typedef unsigned long DWORD;
typedef unsigned char BYTE;

#define LOG_TAG    "TRM_AG"
#define DEBUG_TAG	"ZIPUTIL"
#define MAX_PATH 255
#define FALSE 0
#define TRUE 1

