#include <jni.h>

//
// Created by wind on 2023/4/24.
//

void encode(const char* pcmPath,const char* aacPath){


}

JNIEXPORT void JNICALL Java_com_wind_ndk_audiorecoder_AudioEncoder_encode
(JNIEnv *env, jobject jobj, jstring jpcmPath, jint channels, jint jbitRate, jint jsampleRate, jstring jaccPath){

   const char* pcmPath =env->GetStringUTFChars(jpcmPath,0);
   const char* aacPath =env->GetStringUTFChars(jaccPath,0);

   encode(pcmPath,aacPath);

   env->ReleaseStringUTFChars(jpcmPath,pcmPath);
   env->ReleaseStringUTFChars(jaccPath,aacPath);


}
