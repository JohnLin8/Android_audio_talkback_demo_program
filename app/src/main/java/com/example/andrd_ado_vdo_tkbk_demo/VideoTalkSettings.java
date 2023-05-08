package com.example.andrd_ado_vdo_tkbk_demo;

public class VideoTalkSettings {

    //传输协议
    public enum Transport {
        UDP, TCP
    }

    //网络连接设置
    public static class NetworkSettings {
        // IP地址
        public static final String IP_ADDRESS = "192.168.1.188";
        // 端口号
        public static final int PORT = 9696;
        // 最大连接次数
        public static final int MAX_CONNECT_TIMES = 5;
        // 是否允许自动连接
        public static final boolean AUTO_CONNECT = true;
    }

    //连接模式：音频、视频、音视频
    public enum ConnectMode {
        AUDIO, VIDEO, AUDIO_VIDEO
    }


    //音频输出模式：扬声器、听筒
    public enum AudioOutputMode {
        SPEAKER, EARPIECE
    }

    //视频输入摄像头：前置、后置
    public enum VideoInputCamera {
        FRONT, BACK
    }

    public static class MediaSettings {
        // 音频输入是否静音
        public static final boolean AUDIO_INPUT_MUTE = false;
        // 音频输出是否静音
        public static final boolean AUDIO_OUTPUT_MUTE = false;
        // 视频输入是否黑屏
        public static final boolean VIDEO_INPUT_BLACK = false;
        // 视频输出是否黑屏
        public static final boolean VIDEO_OUTPUT_BLACK = false;
        // 视频输出是否镜像
        public static final boolean VIDEO_OUTPUT_MIRROR = false;
        //音频输出音量
        public static final int AUDIO_OUTPUT_VOLUME = 100;
    }

    //接收输出帧：链表、自适应抖动缓冲器
    public enum RecvOutputFrame {
        LIST, AJB
    }

    //========自己设计的自适应抖动缓冲器设置========
    public static class AdaptiveJitterBuffer {
        //音频最小缓冲帧数
        public static final int AUDIO_MIN_FRAME_NUM = 5;
        //音频最大缓冲帧数
        public static final int AUDIO_MAX_FRAME_NUM = 20;
        //音频最大允许连续丢帧数
        public static final int AUDIO_MAX_DROP_FRAME_NUM = 20;
        //音频自适应灵敏度0.0-127.0，值越所需缓冲帧数越多
        public static final float AUDIO_ADAPTIVE_SENSITIVITY = 1.0f;
        //视频最小缓冲帧数
        public static final int VIDEO_MIN_FRAME_NUM = 3;
        //视频最大缓冲帧数
        public static final int VIDEO_MAX_FRAME_NUM = 24;
        //视频自适应灵敏度0.0-127.0，值越所需缓冲帧数越多
        public static final float VIDEO_ADAPTIVE_SENSITIVITY = 1.0f;
    }
    //========自己设计的自适应抖动缓冲器设置========


    //====================音频设置====================
    //音视频预设效果：低、中、高、超、特
    public enum PresetEffect {
        LOW, MEDIUM, HIGH, SUPER, SPECIAL
    }

    //音视频预设比特率：低、中、高、超、特
    public enum PresetBitrate {
        LOW, MEDIUM, HIGH, SUPER, SPECIAL
    }

    //音频采样频率：8K、16K、32K、48K
    public enum AudioSampleRate {
        IS_8K, IS_16K, IS_32K, IS_48K
    }

    //音频帧长度：10ms、20ms、30ms
    public enum AudioFrameLength {
        IS_10MS, IS_20MS, IS_30MS
    }

    //使用系统自带的声学回音消除器、噪声抑制器、自动增益控制器
    public static final boolean USE_SYSTEM_AEC = true;
    public static final boolean USE_SYSTEM_NS = true;
    public static final boolean USE_SYSTEM_AGC = true;

    //声学回音消除器：不使用、Speex、WebRTC定点版、WebRTC浮点版、SpeexWebRTC三重声学回音消除器
    public enum AEC {
        NONE, SPEEX, WEBRTC_FIXED, WEBRTC_FLOAT, SPEEX_WEBRTC
    }

    //噪声抑制器：不使用、Speex、WebRTC定点版、WebRTC浮点版、RNNoise
    public enum NoiseSuppressor {
        NONE, SPEEX, WEBRTC_FIXED, WEBRTC_FLOAT, RNNOISE
    }



    //编解码器：PCM原始数据、Speex
    public static final int CODEC_PCM = 0;
    public static final int CODEC_SPEEX = 1;

    //====================视频设置====================
    //采样频率：12fps、15fps、24fps、30fps
    public enum VideoFrameRate {
        IS_12FPS, IS_15FPS, IS_24FPS, IS_30FPS
    }

    //帧大小：120x160、240x320、480x640、960x1280
    public enum VideoFrameSize {
        IS_120x160, IS_240x320, IS_480x640, IS_960x1280
    }

    //编解码器：YU12、OpenH264、系统自带的H.264
    public enum VideoCodec {
        YU12, OPEN_H264, SYSTEM_H264
    }

    //================Speex声学回音消除器参数================
    public static class SpeexAec {
        //滤波器长度
        public static final int FILTER_LENGTH = 500;
        //使用残余回音消除
        public static final boolean USE_REVERSE = true;
        //残余回音倍数，值越大，消除效果越好，但是会导致音频变小。0.0-100.0
        public static final float REVERSE_SUPPRESS = 3.0f;
        //残余回音的持续系数，值越大，消除越强，0.0-0.9
        public static final float REVERSE_SUPPRESS_ACTIVE = 0.65f;
        //残余回音最大衰减分贝值，越小衰减越大，-2147483648-0
        public static final int REVERSE_SUPPRESS_MAX_DB = -32768;
        //有近端语音活动时，残余回音的最大衰减分贝值，越小衰减越大，-2147483648-0
        public static final int REVERSE_SUPPRESS_ACTIVE_MAX_DB = -32768;
    }


    //================WebRTC定点版声学回音消除器参数================
    //回音消除器：使用舒适噪音生成模式
    public static final boolean WEBRTC_AEC_USE_COMFORT_NOISE = true;
    //回音消除器：消除模式，值越大，消除越强，0-4
    public static final int WEBRTC_AEC_SUPPRESS_LEVEL = 4;
    //回音消除器：回音消除时延ms，值越小，消除越强，[-2147483648,2147483648],为零表示自适应
    public static final int WEBRTC_AEC_DELAY = 0;

    //================WebRTC浮点版声学回音消除器参数================
    public static class WebRtcAec {
        //消除模式，值越大，消除越强，0-2
        public static final int SUPPRESS_LEVEL = 2;
        //回音消除时延ms，值越小，消除越强，[-2147483648,2147483648],为零表示自适应
        public static final int DELAY = 0;
        //使用回音延迟不可知模式
        public static final boolean USE_DELAY_AGNOSTIC = true;
        //使用扩展滤波器模式
        public static final boolean USE_EXPERIMENTAL_AGC = true;
        //使用精制滤波器自适应AEC模式
        public static final boolean USE_REFINED_FILTER_ADAPTATION = false;
        //使用自适应调节回音的延迟
        public static final boolean USE_ADAPTIVE_DELAY = true;
    }

    //================SpeexWebRTC三重声学回音消除器参数================
    public static class TripleEchoCanceller {
        //工作模式，取值区间为[1,3]
        public static final int MODE_SPEEX_WEBRTC_FIXED = 1; //Speex声学回音消除器+WebRtc定点版声学回音消除器
        public static final int MODE_WEBRTC_FIXED_FLOAT = 2; //WebRtc定点版声学回音消除器+WebRtc浮点版声学回音消除器
        public static final int MODE_SPEEX_WEBRTC_FLOAT = 3; //Speex声学回音消除器+WebRtc定点版声学回音消除器+WebRtc浮点版声学回音消除器

        //Speex声学回音消除器参数
        public static final int SPEEX_AEC_FILTER_LENGTH = 500; //滤波器数据长度，单位毫秒
        public static final int SPEEX_AEC_USE_REVERSE = 1; //使用残余回音消除
        public static final float SPEEX_AEC_REVERSE_MULTIPLE = 1.0f; //在残余回音消除时，残余回音的倍数，倍数越大消除越强，取值区间为[0.0,100.0]
        public static final float SPEEX_AEC_REVERSE_LAST_ACTIVE = 0.6f; //在残余回音消除时，残余回音的持续系数，系数越大消除越强，取值区间为[0.0,0.9]
        public static final int SPEEX_AEC_REVERSE_SUPPRESS_MAX_DB = -32768; //在残余回音消除时，残余回音最大衰减的分贝值，分贝值越小衰减越大，取值区间为[-32768-214748364801]
        public static final int SPEEX_AEC_REVERSE_SUPPRESS_ACTIVE_MAX_DB = -32768; //有近端语音活动时，在残余回音消除时，残余回音最大衰减的分贝值，分贝值越小衰减越大，取值区间为[-2147483648,0]

        //WebRtc定点版声学回音消除器参数
        public static final int WEBRTC_FIXED_AEC_USE_COMFORT_NOISE = 0; //使用舒适噪音生成模式
        public static final int WEBRTC_FIXED_AEC_SUPPRESS_LEVEL = 4; //消除模式，消除模式越高消除越强，取值区间为[0,4]
        public static final int WEBRTC_FIXED_AEC_DELAY = 0; //回音延迟，单位毫秒，取值区间为[-2147483648.2147483647]，为0表示自适应设置

        //WebRtc浮点版声学回音消除器参数
        public static final int WEBRTC_FLOAT_AEC_SUPPRESS_LEVEL = 2; //消除模式，消除模式越高消除越强，取值区间为[0, 2]
        public static final int WEBRTC_FLOAT_AEC_DELAY = 0; //回音延迟，单位毫秒,取值区间为[-2147483648,2147483647]，为0表示自适应设置
        public static final int WEBRTC_FLOAT_AEC_USE_DELAY_AGNOSTIC = 1; //使用回音延迟不可知模式
        public static final int WEBRTC_FLOAT_AEC_USE_EXPERIMENTAL_AGC = 1; //使用扩展滤波器模式
        public static final int WEBRTC_FLOAT_AEC_USE_REFINED_FILTER_ADAPTATION = 0; //使用精制滤波器自适应Aec模式
        public static final int WEBRTC_FLOAT_AEC_USE_ADAPTIVE_DELAY = 1; //使用自适应调节回音的延迟

        //同一房间声学回音消除参数
        public static final int USE_SIMULTANEOUS_SPEECH_DETECTION = 1; //使用同一房间声学回音消除
        public static final int MIN_VAD_SATISFIED_MS = 380; //同一房间回音最小延迟，单位毫秒，取值区间为[1,2147483647]
    }

    /*
    Speex预处理器设置
    使用语音活动检测：1
    在语音活动检测时，从无语音活动到有语音活动的判断百分比概率，概率越大越难判断为有语音活，取值区简为[0,100]:95
    在语音活动检测时，从有语音活动到无语音活动的判断百分比概率，概率越大越容易判断为无语音活动，取值区间为[0,100]:95

    使用自动增益控制: 1
    自动增益控制时，增益的目标等级，目标等级越大增益越大，取值区间为[1,2147483647]: 20000
    在自动增益控制时，每秒最大增益的分贝值，分贝值越大增益越大，取值区间为[0,2147483647]：10

    在自动增益控制时，每秒最大减益的分贝值，分贝值越小减益越大，取值区间为[-2147483648, 0]：-200
    在自动增益控制时，最大增益的分贝值，分贝值越大增益越大，取值区间为[0,2147483647]: 20
    */
    public static class SpeexPreprocessor {
        //使用Speex预处理器
        public static final int USE_SPEEX_PREPROCESSOR = 1;
        //使用语音活动检测，默认为1
        public static final int USE_VOICEACTIVITY_DETECTION = 1;
        //在语音活动检测时，从无语音活动到有语音活动的判断百分比概率，概率越大越难判断为有语音活，取值区间为[0,100]
        public static final int VAD_PROB_START_SPEECH = 95;
        //在语音活动检测时，从有语音活动到无语音活动的判断百分比概率，概率越大越容易判断为无语音活动，取值区间为[0,100]
        public static final int VAD_PROB_STOP_SPEECH = 95;
        //使用自动增益控制（AGC），默认为0
        public static final int USE_AGC = 1;
        //自动增益控制时，增益的目标等级，目标等级越大增益越大，取值区间为[1, 2147483647]
        public static final int AGC_TARGET_LEVEL_DBFS = 20000;
        //在自动增益控制时，每秒最大增益的分贝值，分贝值越大增益越大，取值区间为[0,2147483647]
        public static final int AGC_MAX_DB_PER_SEC = 10;
        //在自动增益控制时，每秒最大减益的分贝值，分贝值越小减益越大，取值区间为[-2147483648, 0]
        public static final int AGC_MAX_GAIN_DB = -200;
        //在自动增益控制时，最大增益的分贝值，分贝值越大增益越大，取值区间为[0,2147483647]
        public static final int AGC_TARGET_GAIN_DB = 20;
    }


}
