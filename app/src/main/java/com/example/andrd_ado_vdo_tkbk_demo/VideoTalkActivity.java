package com.example.andrd_ado_vdo_tkbk_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import HeavenTao.Media.MediaPocsThrd;

public class VideoTalkActivity extends AppCompatActivity {

    private static final String TAG = "VideoTalkActivity";

    private MyMediaPocsThrd mMyMediaPocsThrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_talk);

        //请求权限。
        MediaPocsThrd.RqstPrmsn(this, 1, 1, 1, 1, 0, 1, 1, 1, 1);

        //创建媒体处理线程。
        mMyMediaPocsThrd = new MyMediaPocsThrd(this, m_MainActivityHandlerPt);


    }


    private void setNetWork() {
        mMyMediaPocsThrd = new MyMediaPocsThrd();
        mMyMediaPocsThrd.start();
    }

    MyMediaPocsThrd m_MyMediaPocsThrdPt;   //
    /**
     * 创建服务端 (如果IP地址不是本机地址，那么就是创建客户端，并连接服务端)
     *
     */
    public void createService() {
        int p_Rslt = -1; //存放本函数执行结果的值，为0表示成功，为非0表示失败。


        if (m_MyMediaPocsThrdPt == null) //如果媒体处理线程还没有启动。
        {
            Log.i(m_CurClsNameStrPt, "开始启动媒体处理线程。");

            //创建媒体处理线程。
            m_MyMediaPocsThrdPt = new MyMediaPocsThrd(this, m_MainActivityHandlerPt);

            //设置网络。
            {
                //设置IP地址字符串。
                m_MyMediaPocsThrdPt.mTalkNetwork.m_IPAddrStrPt = "192.168.1.189";
                //设置端口字符串。
                m_MyMediaPocsThrdPt.mTalkNetwork.m_PortStrPt = "9696";
                //设置使用什么传输协议。
                m_MyMediaPocsThrdPt.mTalkNetwork.m_UseWhatXfrPrtcl = 1;  //0--TCP, 1--UDP
                //设置传输模式。
                m_MyMediaPocsThrdPt.mTalkNetwork.m_XfrMode = 1;   //全双工
                //设置最大连接次数。
                m_MyMediaPocsThrdPt.mTalkNetwork.m_MaxCnctTimes = 5;
                //设置创建服务端或者客户端标记。
                m_MyMediaPocsThrdPt.mTalkNetwork.m_IsCreateSrvrOrClnt = 1; //标记创建服务端--1, 连接服务端--0。
                //设置是否自动允许连接。
                m_MyMediaPocsThrdPt.m_IsAutoAllowCnct = 1;
            }

            //设置是否使用链表。不使用链表
            //使用自己设计的自适应抖动缓冲器。
            m_MyMediaPocsThrdPt.m_UseWhatRecvOtptFrm = 1;
            {
                //音频参数
                m_MyMediaPocsThrdPt.m_AAjbPt.m_MinNeedBufFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.AUDIO_MIN_FRAME_NUM;
                m_MyMediaPocsThrdPt.m_AAjbPt.m_MaxNeedBufFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.AUDIO_MAX_FRAME_NUM;
                m_MyMediaPocsThrdPt.m_AAjbPt.m_MaxCntuLostFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.AUDIO_MAX_DROP_FRAME_NUM;
                m_MyMediaPocsThrdPt.m_AAjbPt.m_AdaptSensitivity = VideoTalkSettings.AdaptiveJitterBuffer.AUDIO_ADAPTIVE_SENSITIVITY;
                //视频参数
                m_MyMediaPocsThrdPt.m_VAjbPt.m_MinNeedBufFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.VIDEO_MIN_FRAME_NUM;
                m_MyMediaPocsThrdPt.m_VAjbPt.m_MaxNeedBufFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.VIDEO_MAX_FRAME_NUM;
                m_MyMediaPocsThrdPt.m_VAjbPt.m_AdaptSensitivity = VideoTalkSettings.AdaptiveJitterBuffer.VIDEO_ADAPTIVE_SENSITIVITY;
            }

            //默认打印Logcat日志、显示Toast。
            m_MyMediaPocsThrdPt.SetIsPrintLogcatShowToast(1, 1, this);

            //默认使用唤醒锁。
            m_MyMediaPocsThrdPt.SetIsUseWakeLock(1);

            //默认不要保存音视频输入输出到文件。
            m_MyMediaPocsThrdPt.SetIsSaveAdoVdoInptOtptToAviFile(
                    m_ExternalDirFullAbsPathStrPt + "/AdoVdoInptOtpt.avi",
                    8 * 1024, 0, 0, 0, 0);

            //设置音频输入。
            m_MyMediaPocsThrdPt.SetAdoInpt(16000, 20);

            //设置音频输入是否使用系统自带的声学回音消除器、噪音抑制器和自动增益控制器。
            m_MyMediaPocsThrdPt.AdoInptSetIsUseSystemAecNsAgc(1);    //TODO: 是否使用系统自带的声学回音消除器、噪音抑制器和自动增益控制器。


            //如果传输模式为实时全双工。
            {
                //默认使用SpeexWebRtc三重声学回音消除器。
                m_MyMediaPocsThrdPt.AdoInptSetUseSpeexWebRtcAec(
                        VideoTalkSettings.TripleEchoCanceller.MODE_SPEEX_WEBRTC_FLOAT,
                        VideoTalkSettings.TripleEchoCanceller.SPEEX_AEC_FILTER_LENGTH,
                        VideoTalkSettings.TripleEchoCanceller.SPEEX_AEC_USE_REVERSE,
                        VideoTalkSettings.TripleEchoCanceller.SPEEX_AEC_REVERSE_MULTIPLE,
                        VideoTalkSettings.TripleEchoCanceller.SPEEX_AEC_REVERSE_LAST_ACTIVE,
                        VideoTalkSettings.TripleEchoCanceller.SPEEX_AEC_REVERSE_SUPPRESS_MAX_DB,
                        VideoTalkSettings.TripleEchoCanceller.SPEEX_AEC_REVERSE_SUPPRESS_ACTIVE_MAX_DB,
                        VideoTalkSettings.TripleEchoCanceller.WEBRTC_FIXED_AEC_USE_COMFORT_NOISE,
                        VideoTalkSettings.TripleEchoCanceller.WEBRTC_FIXED_AEC_SUPPRESS_LEVEL,
                        VideoTalkSettings.TripleEchoCanceller.WEBRTC_FIXED_AEC_DELAY,
                        VideoTalkSettings.TripleEchoCanceller.WEBRTC_FLOAT_AEC_SUPPRESS_LEVEL,
                        VideoTalkSettings.TripleEchoCanceller.WEBRTC_FLOAT_AEC_DELAY,
                        VideoTalkSettings.TripleEchoCanceller.WEBRTC_FLOAT_AEC_USE_DELAY_AGNOSTIC,
                        VideoTalkSettings.TripleEchoCanceller.WEBRTC_FLOAT_AEC_USE_EXPERIMENTAL_AGC,
                        VideoTalkSettings.TripleEchoCanceller.WEBRTC_FLOAT_AEC_USE_REFINED_FILTER_ADAPTATION,
                        VideoTalkSettings.TripleEchoCanceller.WEBRTC_FLOAT_AEC_USE_ADAPTIVE_DELAY,
                        VideoTalkSettings.TripleEchoCanceller.USE_SIMULTANEOUS_SPEECH_DETECTION,
                        VideoTalkSettings.TripleEchoCanceller.MIN_VAD_SATISFIED_MS);
            }

            //设置音频输入是否不使用噪音抑制器。(默认使用噪音抑制器)
            //m_MyMediaPocsThrdPt.AdoInptSetUseNoNs();

            //设置音频输入是否使用Speex预处理器的噪音抑制。(默认不使用Speex预处理器的噪音抑制)

            //设置音频输入是否使用WebRtc定点版噪音抑制器。(默认不使用WebRtc定点版噪音抑制器)

            //设置音频输入是否使用WebRtc浮点版噪音抑制器。(默认不使用WebRtc浮点版噪音抑制器)

            //设置音频输入是否使用RNNoise噪音抑制器。(默认使用RNNoise噪音抑制器)
            m_MyMediaPocsThrdPt.AdoInptSetUseRNNoise();

            //设置音频输入是否使用Speex预处理器。(默认使用Speex预处理器)
            m_MyMediaPocsThrdPt.AdoInptSetIsUseSpeexPrpocs(
                    VideoTalkSettings.SpeexPreprocessor.USE_SPEEX_PREPROCESSOR,
                    VideoTalkSettings.SpeexPreprocessor.USE_VOICEACTIVITY_DETECTION,
                    VideoTalkSettings.SpeexPreprocessor.VAD_PROB_START_SPEECH,
                    VideoTalkSettings.SpeexPreprocessor.VAD_PROB_STOP_SPEECH,
                    VideoTalkSettings.SpeexPreprocessor.USE_AGC,
                    VideoTalkSettings.SpeexPreprocessor.AGC_TARGET_LEVEL_DBFS,
                    VideoTalkSettings.SpeexPreprocessor.AGC_MAX_DB_PER_SEC,
                    VideoTalkSettings.SpeexPreprocessor.AGC_MAX_GAIN_DB,
                    VideoTalkSettings.SpeexPreprocessor.AGC_TARGET_GAIN_DB);

            //设置音频输入是否使用PCM原始数据。

            //设置音频输入是否使用Speex编码器。(默认使用Speex编码器)
            m_MyMediaPocsThrdPt.AdoInptSetUseSpeexEncd(
                    1,
                    10,
                    10,
                    100);


            //设置音频输入是否使用Opus编码器。

            //设置音频输入是否保存音频到文件。
            m_MyMediaPocsThrdPt.AdoInptSetIsSaveAdoToWaveFile(
                    0,
                    m_ExternalDirFullAbsPathStrPt + "/AdoInpt.wav",
                    m_ExternalDirFullAbsPathStrPt + "/AdoRslt.wav",
                    8 * 1024);

            //设置音频输入是否绘制音频波形到Surface。
            m_MyMediaPocsThrdPt.AdoInptSetIsDrawAdoWavfmToSurface(
                    0,
                    null,
                    null
            );

            //设置音频输入是否静音。
            m_MyMediaPocsThrdPt.AdoInptSetIsMute(0);

            //设置音频输出。
            m_MyMediaPocsThrdPt.SetAdoOtpt(16000, 20);
            m_MyMediaPocsThrdPt.AddAdoOtptStrm(0);

            //设置音频输出是否使用PCM原始数据。
            //m_MyMediaPocsThrdPt.AdoOtptSetStrmUsePcm(0);


            //设置音频输出是否使用Speex解码器。
            m_MyMediaPocsThrdPt.AdoOtptSetStrmUseSpeexDecd(0, 1);


            //设置音频输出是否使用Opus解码器。
            //m_MyMediaPocsThrdPt.AdoOtptSetStrmUseOpusDecd(0);

            //设置音频输出流是否使用。
            m_MyMediaPocsThrdPt.AdoOtptSetStrmIsUse(0, 1);

            //设置音频输出是否保存音频到文件。
            m_MyMediaPocsThrdPt.AdoOtptSetIsSaveAdoToWaveFile(
                    0,
                    m_ExternalDirFullAbsPathStrPt + "/AdoOtpt.wav",
                    8 * 1024);

            //设置音频输出是否绘制音频波形到Surface。
            m_MyMediaPocsThrdPt.AdoOtptSetIsDrawAdoWavfmToSurface(
                    0,
                    null);

            //设置音频输出使用的设备。
            m_MyMediaPocsThrdPt.AdoOtptSetUseDvc(
                    0,
                    0);

            //设置音频输出是否静音。
            m_MyMediaPocsThrdPt.AdoOtptSetIsMute(0);

            //设置视频输入。
            m_MyMediaPocsThrdPt.SetVdoInpt(
                    12,
                    640,
                    480,
                    0,
                    null);   //TODO: HTSurfaceView


            //设置视频输入是否使用YU12原始数据。
            //m_MyMediaPocsThrdPt.VdoInptSetUseYU12();

            //设置视频输入是否使用OpenH264编码器。
            m_MyMediaPocsThrdPt.VdoInptSetUseOpenH264Encd(
                    0,
                    60,
                    3,
                    24,
                    1);
            //设置视频输入是否使用系统自带H264编码器。
            //m_MyMediaPocsThrdPt.VdoInptSetUseSystemH264Encd(............);

            //设置视频输入使用的设备。
            m_MyMediaPocsThrdPt.VdoInptSetUseDvc(
                    0,
                    -1,
                    -1);

            //设置视频输入是否黑屏。
            m_MyMediaPocsThrdPt.VdoInptSetIsBlack(0);

            //设置视频输出。
            m_MyMediaPocsThrdPt.VdoOtptAddStrm(0);
            m_MyMediaPocsThrdPt.VdoOtptSetStrm(
                    0,
                    null);    //TODO: HTSurfaceView

            //设置视频输出是否使用YU12原始数据。
            //m_MyMediaPocsThrdPt.VdoOtptSetStrmUseYU12(0);


            //设置视频输出是否使用OpenH264解码器。
            m_MyMediaPocsThrdPt.VdoOtptSetStrmUseOpenH264Decd(0, 0);

            //设置视频输出是否使用系统自带H264解码器。
            //m_MyMediaPocsThrdPt.VdoOtptSetStrmUseSystemH264Decd(0);

            //设置视频输出是否黑屏。
            m_MyMediaPocsThrdPt.VdoOtptSetStrmIsBlack(
                    0,
                    0);

            //设置视频输出流是否使用。
            m_MyMediaPocsThrdPt.VdoOtptSetStrmIsUse(0, 1);

            //设置本端对讲模式。
            m_MyMediaPocsThrdPt.SendUserMsg(MyMediaPocsThrd.UserMsgTyp.LclTkbkMode, MyMediaPocsThrd.TkbkMode.AdoVdo);

            //设置是否保存设置到文件。
            //m_MyMediaPocsThrdPt.SaveStngToFile(m_ExternalDirFullAbsPathStrPt + "/Setting.txt");

            //启动媒体处理线程。
            m_MyMediaPocsThrdPt.start();
            p_Rslt = 0;
            Log.i(m_CurClsNameStrPt, "启动媒体处理线程完毕。");
        } else {
            Log.i(m_CurClsNameStrPt, "开始请求并等待媒体处理线程退出。");
            m_MyMediaPocsThrdPt.m_IsInterrupt = 1;
            m_MyMediaPocsThrdPt.RqirExit(1, 1);
            Log.i(m_CurClsNameStrPt, "结束请求并等待媒体处理线程退出。");
        }

        if (p_Rslt != 0) //如果启动媒体处理线程失败。
        {
            m_MyMediaPocsThrdPt = null;
        }
    }





}