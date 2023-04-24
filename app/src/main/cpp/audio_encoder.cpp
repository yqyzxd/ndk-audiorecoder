//
// Created by wind on 2023/4/24.
//

#include "audio_encoder.h"
#include "thirdparty/ffmpeg/include/libswresample/swresample.h"


#define LOG_TAG "AudioEncoder"
int AudioEncoder::init(int bitRate, int channels, int sampleRate, int bitsPerSample,
                       const char *aacFilePath, const char *codecName) {
    //
    this->bitRate=bitRate;
    this->channels=channels;
    this->sampleRate=sampleRate;


    avFormatContext = avformat_alloc_context();

    // Allocate an AVFormatContext for an output format.
    // avformat_free_context() can be used to free the context and
    // everything allocated by the framework within it.
    int ret = avformat_alloc_output_context2(&avFormatContext, nullptr, nullptr, aacFilePath);
    if (ret < 0) {
        return -1;
    }

    //Create and initialize a AVIOContext for accessing the
    //resource indicated by url.
    ret = avio_open2(&avFormatContext->pb, aacFilePath, AVIO_FLAG_WRITE, nullptr, nullptr);
    if (ret < 0) {
        return -1;
    }
    //创建AudioStream
    allocAudioStream(codecName);

    //Print detailed information about the input or output format, such as
    //duration, bitrate, streams, container, programs, metadata, side data,
    //codec and time base.
    av_dump_format(avFormatContext, 0, aacFilePath, 1);

    ret = avformat_write_header(avFormatContext, nullptr);
    if (ret < 0) {
        return -1;
    }
    isWriteHeaderSuccess=true;

    //创建AvFrame
    allocAvFrame();

    return 1;
}

void AudioEncoder::encode(byte *buffer, int size) {

}

void AudioEncoder::destroy() {

}

int AudioEncoder::allocAudioStream(const char *codecName) {
    audioStream=avformat_new_stream(avFormatContext, nullptr);
    /**
   * Format-specific stream ID.
   * decoding: set by libavformat
   * encoding: set by the user, replaced by libavformat if left unset
   */
    audioStream->id=1;



    AVCodecContext avCodecContext;

    avCodecContext.codec_type= AVMEDIA_TYPE_AUDIO;
    avCodecContext.sample_rate=sampleRate;
    avCodecContext.channels=channels;
    if (bitRate<=0){
        bitRate=BITE_RATE;
    }
    avCodecContext.bit_rate=bitRate;
    //设置sampleFmt
    avCodecContext.sample_fmt=AV_SAMPLE_FMT_S16;
    //设置通道信息
    avCodecContext.channel_layout=channels==1?AV_CH_LAYOUT_MONO:AV_CH_LAYOUT_STEREO;
    avCodecContext.channels= av_get_channel_layout_nb_channels(avCodecContext.channel_layout);
    // encoding: Set by user.
    // decoding: Set by libavcodec.
    avCodecContext.profile = FF_PROFILE_AAC_HE;
    //* Place global headers in extradata instead of every keyframe.
    avCodecContext.flags |=AV_CODEC_FLAG_GLOBAL_HEADER;



    AVCodec* codec=avcodec_find_encoder_by_name(codecName);
    if (codec== nullptr){
        return -1;
    }
    avCodecContext.codec_id=codec->id;

    //设置最合适的sampleFormat
    //array of supported sample formats, or NULL if unknown, array is terminated by -1
    const enum  AVSampleFormat* avSampleFormats=codec->sample_fmts;
    if (avSampleFormats){
        for (; *avSampleFormats!=-1 ; avSampleFormats++) {
            if (*avSampleFormats == avCodecContext.sample_fmt){
                break;
            }
        }
        if ( *avSampleFormats==-1){
            LOGI("sample fmt not supported,user the codec supported sample fmt ");
            avCodecContext.sample_fmt=codec->sample_fmts[0];
        }
    }

    //找到最合适的采样率
    if (codec->supported_samplerates){
        //array of supported audio samplerates, or NULL if unknown, array is terminated by 0
        const int *sampleRates=codec->supported_samplerates;
        int best=0;
        int best_dist=INT_MAX;
        for (; *sampleRates ; sampleRates++) {
            int dist= abs(avCodecContext.sample_rate-*sampleRates);
            if (dist<best_dist){
                best_dist=dist;
                best=*sampleRates;
            }
        }

        avCodecContext.sample_rate=best;
    }

    if (channels !=avCodecContext.channels
        || sampleRate!=avCodecContext.sample_rate
        || AV_SAMPLE_FMT_S16!=avCodecContext.sample_fmt){
        swrContext=swr_alloc_set_opts(nullptr,
                                                  av_get_default_channel_layout(avCodecContext.channels),
                                                  avCodecContext.sample_fmt,avCodecContext.sample_rate,
                                                  av_get_default_channel_layout(channels),
                                                  AV_SAMPLE_FMT_S16,
                                                  sampleRate,
                                                  0, nullptr
                                                  );
        if (!swrContext|| swr_init(swrContext)){
            if (swrContext){
                swr_free(&swrContext);
            }
            return -1;
        }
    }



    //试试能不能打开编解码器
    int ret=avcodec_open2(&avCodecContext,codec, nullptr);
    if (ret<0){
        LOGI("can't open codec,please check");
        return -1;
    }

    avCodecContext.time_base.num=1;
    avCodecContext.time_base.den=avCodecContext.sample_rate;
    avCodecContext.frame_size=1024;//每个frame的大小

    AVCodecParameters* avCodecParameters=audioStream->codecpar;
    //将avCodecContext中的信息拷贝到avCodecParameters
    ret=avcodec_parameters_from_context(avCodecParameters,&avCodecContext);
    if (ret<0){
        LOGI("avcodec_parameters_from_context failed,please check");
        return -1;
    }
    this->avCodecContext=&avCodecContext;

    return 0;

}

int AudioEncoder::allocAvFrame() {

    inputFrame=av_frame_alloc();
    if (!inputFrame){
        LOGI("can't alloc avframe");
        return -1;
    }

    inputFrame->nb_samples=avCodecContext->frame_size;

    return 0;
}
