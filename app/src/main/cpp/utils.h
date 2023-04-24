//
// Created by wind on 2023/4/24.
//

#ifndef NDK_AUDIORECODER_UTILS_H
#define NDK_AUDIORECODER_UTILS_H


#include <sys/time.h>
#include "thirdparty/ffmpeg/include/libavformat/avformat.h"

//毫秒
static inline long long getCurrentTimestamp(){
    struct timeval t;
    gettimeofday(&t, nullptr);
    return t.tv_sec*1000+t.tv_usec/1000;
}


#endif //NDK_AUDIORECODER_UTILS_H
