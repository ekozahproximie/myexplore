/*
 * add.cpp
 *
 *  Created on: Sep 7, 2009
 *      Author: Schaefer
 */

#include <jni.h>
#include <android/log.h>
#include <string.h>
#include"wchar.h"
#include"malloc.h"
#include"stdio.h"

#define LOG_DATA_NDK

#define  LOG_TAG    "SPIME"
#define DEBUG_TAG	"SPIME_DEBUG"

#ifdef LOG_DATA_NDK
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#endif

#define NATIVE_CALL(type, name) extern "C" JNIEXPORT type JNICALL Java_com_spime_AndroidDemo_ ## name

NATIVE_CALL(jstring, add)(JNIEnv * env, jobject obj, jint x, jint y) {
	//size_t nLen =	wcslen(L"hai");
	//size_t nLen = mbstowcs ( NULL, "hai",  0);
	char msg[]={"豆贝尔维"};
FILE *fp= fopen("/sdcard/豆贝尔维.txt","r");
char line [ 128 ];
LOGI("char %d",strlen(msg));
/* or other suitable maximum line size */
while ( fgets ( line, sizeof line, fp ) != NULL )
	/* read a line */
{
	LOGI("char %s",line);

	/* write the line */
}
fclose(fp);
	char inbuf[]= {0XFE ,0XFF,0x00,0x53,0x00,0x70,0x00,0x69,0x00,0x72,0x00, 0x65,0x00,0x6E
			,0x00,0x74,0x00, 0x20 ,0x00 ,0x43 ,0x00 ,0x6F ,0x00 ,0x6D ,0x00 ,0x6D ,0x00, 0x75 ,0x00 ,0x6E ,
			0x00 ,0x69,0x00 ,0x63 ,0x00 ,0x61 ,0x00 ,0x74 ,0x00 ,0x69 ,0x00 ,
			0x6F ,0x00 ,0x6E ,0x00 ,0x73,0x00,0X00};

	int len = 48+1;

	// Copy the result out of the C character buffer into a jchar buffer.  This is where we are converting chars to jchars.
	jchar *jcharBuffer1 = (jchar *)calloc(sizeof(jchar), len);
	LOGI("add in %d %d",len,sizeof(jcharBuffer1));

	for (int i = 0; i < len; i ++) {
	    jcharBuffer1[i] = (jchar)inbuf[i];
	}

	jchar jc[]={
			0XFE ,0XFF,  0x00,  0x53, 0x00, 0x70, 0x00, 0x69,  0x00, 0x72,0x00, 0x65, 0x00 ,0x6E
			,0x00,0x74 , 0x00, 0x20 ,0x00 ,0x43 ,0x00 ,0x6F , 0x00 ,0x6D ,0x00 ,0x6D ,0x00, 0x75
			,0x00 ,0x6E ,0x00 ,0x69, 0x00 ,0x63 , 0x00 ,0x61 ,0x00 ,0x74 ,0x00 ,0x69 ,0x00 ,0x6F
			,0x00 ,0x6E ,0x00 ,0x73, 0x00 ,0X00};
	jstring text = env->NewString(jcharBuffer1,48);
	LOGI("add end %s",inbuf);
	return text;
}
