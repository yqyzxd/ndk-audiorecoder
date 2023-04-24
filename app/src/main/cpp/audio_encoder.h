//
// Created by wind on 2023/4/24.
//

#ifndef NDK_AUDIORECODER_AUDIO_ENCODER_H
#define NDK_AUDIORECODER_AUDIO_ENCODER_H
#include "types.h"
#include "log.h"


extern "C"{
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "libavutil/avutil.h"
#include "libavutil/channel_layout.h"
#include "libavutil/samplefmt.h"
#include "libswresample/swresample.h"
};

#define BITE_RATE 64000

class AudioEncoder {
private:
    AVFormatContext* avFormatContext;
    AVStream* audioStream;
    AVCodecContext* avCodecContext;
    SwrContext* swrContext;
    AVFrame* inputFrame;

    //通道数
    int channels;
    //采样率
    int sampleRate;
    //码率
    int bitRate;

    bool isWriteHeaderSuccess;

public:
    int init(int bitRate, int channels ,int sampleRate,int bitsPerSample,const char* aacFilePath,const char* codecName);
    void encode(byte* buffer,int size);
    void destroy();


    int allocAudioStream(const char *codecName);
    int allocAvFrame();
};


#endif //NDK_AUDIORECODER_AUDIO_ENCODER_H
