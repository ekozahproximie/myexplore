#include <string.h>
#include "stdafx.h"
#include <dirent.h>
#include <locale.h>
#include "ZipArchive.h"

#ifdef __cplusplus
extern "C" {
#endif


/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */

JNIEXPORT jboolean JNICALL Java_com_trimble_agmantra_utils_ZipUtils_createJobZipFile
  (JNIEnv * env, jobject thisz, jstring jstInputPath, jstring jstOutputPath)
{

    LOGI("\n Inside the createJobFile function.");
    //char szOutputPath[MAX_PATH] = "/mnt/sdcard/AgMantra/GT-P1000/From_Device/Scout_Disease_20120803_192255_258cdb53.zip";
    //char m_strLocalDataPath[MAX_PATH] = "/mnt/sdcard/AgMantra/286f4958.prj/Scouting/Scout_Disease_20120803_192255_258cdb53/";

	char m_strLocalDataPath[MAX_PATH];
	int len = (env)->GetStringLength(jstInputPath);
	(env)->GetStringUTFRegion(jstInputPath, 0, len, m_strLocalDataPath);
	LOGI("\n Input Path = %s\n", m_strLocalDataPath);

	char szOutputPath[MAX_PATH];
	len = (env)->GetStringLength(jstOutputPath);
	(env)->GetStringUTFRegion(jstOutputPath, 0, len, szOutputPath);
	LOGI("\n Output Path = %s\n", szOutputPath);
    char strZipPath[MAX_PATH] = "";

    TZipArchive ZipFile;
    bool bRet = FALSE;
    bRet = ZipFile.Open( szOutputPath, 2 );
	LOGI("\n Opening zipfile returned %s", (FALSE == bRet)?"FALSE":"TRUE");

    if (TRUE == bRet)
    {
		LOGI("\n Calling AppendFiles \n", (FALSE == bRet)?"FALSE":"TRUE");
		bRet = ZipFile.AppendFiles( m_strLocalDataPath, strZipPath, -1, 8, (BYTE)TRUE );
		LOGI("\n AppendFiles returned %s\n", (FALSE == bRet)?"FALSE":"TRUE");

		ZipFile.Close();
    }
    return bRet;
}

/*jstring
Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
                                                 jobject thiz )
{
    return (env)->NewStringUTF("Hello from JNI !");
}*/
#ifdef __cplusplus
}
#endif

