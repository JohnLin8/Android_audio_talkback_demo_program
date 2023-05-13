package com.example.andrd_ado_vdo_tkbk_demo;

import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import HeavenTao.Media.HTSurfaceView;
import HeavenTao.Media.MediaPocsThrd;

public class VideoTalkActivity extends AppCompatActivity {

    private static final String TAG = "VideoTalkActivity";

    private static MyMediaPocsThrd mMyMediaProcessThread;   //媒体处理线程
    private VideoTalkHandler mVideoTalkHandler;

    private String mExternalDirFullAbsPath;   //存放扩展目录完整绝对路径字符串的指针。
    private HTSurfaceView mVideoInputView, mVideoOutputView;
    private TextView mIpAddrTextView;

    public enum Msg {
        MediaPocsThrdInit, //主界面消息：初始化媒体处理线程。
        MediaPocsThrdDstoy, //主界面消息：销毁媒体处理线程。
        RqstCnctDlgInit, //主界面消息：初始化请求连接对话框。
        RqstCnctDlgDstoy, //主界面消息：销毁请求连接对话框。
        PttBtnInit, //主界面消息：初始化一键即按即通按钮。
        PttBtnDstoy, //主界面消息：销毁一键即按即通按钮。
        ShowLog, //主界面消息：显示日志。
        Vibrate, //主界面消息：振动。
    }

    private static class VideoTalkHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            switch (Msg.values()[msg.what]) {
                case MediaPocsThrdInit:    //主界面消息：初始化媒体处理线程。
                {
                    if (mMyMediaProcessThread.mTalkNetwork.m_IsCreateSrvrOrClnt == 1) {
                        Log.i(TAG, "handleMessage: 创建服务端");
                    } else {
                        Log.i(TAG, "handleMessage: 创建客户端");
                    }
                    break;
                }
                case MediaPocsThrdDstoy:    //主界面消息：销毁媒体处理线程。
                {
                    mMyMediaProcessThread = null;

                    break;
                }
                case RqstCnctDlgInit: {
                    if (mMyMediaProcessThread.mTalkNetwork.m_IsCreateSrvrOrClnt == 1) {
                        //如果是创建服务端
                        //允许连接或拒绝连接选择
                        Log.i(TAG, "handleMessage: 连接选择");
                    } else {
                        //如果是创建客户端
                        Log.i(TAG, "handleMessage: 等待远端允许连接");

                        //是否中断选择
                    }
                    break;
                }
                case RqstCnctDlgDstoy: {
                    Log.i(TAG, "handleMessage: 请求连接对话框销毁");
                    break;
                }
                case ShowLog: {
                    Log.i(TAG, "handleMessage: 显示日志");

                    break;
                }
                case Vibrate: {
                    Log.i(TAG, "handleMessage: 振动");
                    break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_talk);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //请求权限。
        MediaPocsThrd.RqstPrmsn(this, 1, 1, 1, 1, 0, 1, 1, 1, 1);

        //初始化消息处理。
        mVideoTalkHandler = new VideoTalkHandler();

        //视频显示View
        mVideoInputView = findViewById(R.id.inputVideoView);
        mVideoOutputView = findViewById(R.id.outputVideoView);
        mIpAddrTextView = findViewById(R.id.tvMyIP);

        //获取外部存储器路径
        getExternalDirPath();

        String mIpAddr = getIpAddr();
        Log.i(TAG, "onCreate: IP地址：" + mIpAddr);
        mIpAddrTextView.setText(mIpAddr);

        // 创建服务器
        createServerOrClient(1);

    }

    private String getIpAddr() {
        //设置IP地址编辑框的内容。
        try {
            //遍历所有的网络接口设备。
            for (Enumeration<NetworkInterface> clEnumerationNetworkInterface = NetworkInterface.getNetworkInterfaces(); clEnumerationNetworkInterface.hasMoreElements(); ) {
                NetworkInterface clNetworkInterface = clEnumerationNetworkInterface.nextElement();
                if (clNetworkInterface.getName().compareTo("usbnet0") != 0) //如果该网络接口设备不是USB接口对应的网络接口设备。
                {
                    //遍历该网络接口设备所有的IP地址。
                    for (Enumeration<InetAddress> enumIpAddr = clNetworkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress clInetAddress = enumIpAddr.nextElement();
                        if ((!clInetAddress.isLoopbackAddress()) && (clInetAddress.getAddress().length == 4)) //如果该IP地址不是回环地址，且是IPv4的。
                        {
                            return clInetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return "0.0.0.0"; //如果没有获取到IP地址，就设置为本地地址。
    }

    private void getExternalDirPath() {
        //获取扩展目录完整绝对路径字符串。
        {
            if (getExternalFilesDir(null) != null) {
                mExternalDirFullAbsPath = getExternalFilesDir(null).getPath();
            } else {
                mExternalDirFullAbsPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + getApplicationContext().getPackageName();
            }

            String p_InfoStrPt = "扩展目录完整绝对路径：" + mExternalDirFullAbsPath;
            Log.i(TAG, p_InfoStrPt);
        }
    }

    private void setNetWork() {
//        mMyMediaPocsThrd = new MyMediaPocsThrd();
        mMyMediaProcessThread.start();
    }

//    MyMediaPocsThrd m_MyMediaPocsThrdPt;   //

    /**
     * 创建服务端 (如果IP地址不是本机地址，那么就是创建客户端，并连接服务端)
     */
    public void createServerOrClient(int isServerOrClient) {

        if (mMyMediaProcessThread == null) //如果媒体处理线程还没有启动。
        {
            Log.i(TAG, "开始启动媒体处理线程。");

            //创建媒体处理线程。
            mMyMediaProcessThread = new MyMediaPocsThrd(this, mVideoTalkHandler);

            //设置网络。
            {
                //设置IP地址字符串。
                mMyMediaProcessThread.mTalkNetwork.m_IPAddrStrPt = getIpAddr();
                //设置端口字符串。
                mMyMediaProcessThread.mTalkNetwork.m_PortStrPt = "12345";
                //设置使用什么传输协议。
                mMyMediaProcessThread.mTalkNetwork.m_UseWhatXfrPrtcl = 1;  //0--TCP, 1--UDP
                //设置传输模式。
                mMyMediaProcessThread.mTalkNetwork.m_XfrMode = 1;   //全双工
                //设置最大连接次数。
                mMyMediaProcessThread.mTalkNetwork.m_MaxCnctTimes = 5;
                //设置创建服务端或者客户端标记。
                mMyMediaProcessThread.mTalkNetwork.m_IsCreateSrvrOrClnt = isServerOrClient; //标记创建服务端--1, 连接服务端--0。
                //设置是否自动允许连接。
                mMyMediaProcessThread.m_IsAutoAllowCnct = 1;
            }

            //设置是否使用链表。不使用链表

            //使用自己设计的自适应抖动缓冲器。
            mMyMediaProcessThread.m_UseWhatRecvOtptFrm = 1;
            {
                //音频参数
                mMyMediaProcessThread.m_AAjbPt.m_MinNeedBufFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.AUDIO_MIN_FRAME_NUM;
                mMyMediaProcessThread.m_AAjbPt.m_MaxNeedBufFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.AUDIO_MAX_FRAME_NUM;
                mMyMediaProcessThread.m_AAjbPt.m_MaxCntuLostFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.AUDIO_MAX_DROP_FRAME_NUM;
                mMyMediaProcessThread.m_AAjbPt.m_AdaptSensitivity = VideoTalkSettings.AdaptiveJitterBuffer.AUDIO_ADAPTIVE_SENSITIVITY;
                //视频参数
                mMyMediaProcessThread.m_VAjbPt.m_MinNeedBufFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.VIDEO_MIN_FRAME_NUM;
                mMyMediaProcessThread.m_VAjbPt.m_MaxNeedBufFrmCnt = VideoTalkSettings.AdaptiveJitterBuffer.VIDEO_MAX_FRAME_NUM;
                mMyMediaProcessThread.m_VAjbPt.m_AdaptSensitivity = VideoTalkSettings.AdaptiveJitterBuffer.VIDEO_ADAPTIVE_SENSITIVITY;
            }

            //默认打印Logcat日志、显示Toast。
            mMyMediaProcessThread.SetIsPrintLogcatShowToast(1, 1, this);

            //默认使用唤醒锁。
            mMyMediaProcessThread.SetIsUseWakeLock(1);

            //默认不要保存音视频输入输出到文件。
            mMyMediaProcessThread.SetIsSaveAdoVdoInptOtptToAviFile(
                    mExternalDirFullAbsPath + "/AdoVdoInptOtpt.avi",
                    8 * 1024, 0, 0, 0, 0);

            //设置音频输入。
            mMyMediaProcessThread.SetAdoInpt(16000, 20);

            //设置音频输入是否使用系统自带的声学回音消除器、噪音抑制器和自动增益控制器。
            mMyMediaProcessThread.AdoInptSetIsUseSystemAecNsAgc(1);    //TODO: 是否使用系统自带的声学回音消除器、噪音抑制器和自动增益控制器。


            //如果传输模式为实时全双工。
            {
                //默认使用SpeexWebRtc三重声学回音消除器。
                mMyMediaProcessThread.AdoInptSetUseSpeexWebRtcAec(
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
            mMyMediaProcessThread.AdoInptSetUseRNNoise();

            //设置音频输入是否使用Speex预处理器。(默认使用Speex预处理器)
            mMyMediaProcessThread.AdoInptSetIsUseSpeexPrpocs(
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
            mMyMediaProcessThread.AdoInptSetUseSpeexEncd(
                    1,
                    10,
                    10,
                    100);


            //设置音频输入是否使用Opus编码器。

            //设置音频输入是否保存音频到文件。
            mMyMediaProcessThread.AdoInptSetIsSaveAdoToWaveFile(
                    0,
                    mExternalDirFullAbsPath + "/AdoInpt.wav",
                    mExternalDirFullAbsPath + "/AdoRslt.wav",
                    8 * 1024);

            //设置音频输入是否绘制音频波形到Surface。
            mMyMediaProcessThread.AdoInptSetIsDrawAdoWavfmToSurface(
                    0,
                    null,
                    null
            );

            //设置音频输入是否静音。
            mMyMediaProcessThread.AdoInptSetIsMute(0);

            //设置音频输出。
            mMyMediaProcessThread.SetAdoOtpt(16000, 20);
            mMyMediaProcessThread.AddAdoOtptStrm(0);

            //设置音频输出是否使用PCM原始数据。
            //m_MyMediaPocsThrdPt.AdoOtptSetStrmUsePcm(0);


            //设置音频输出是否使用Speex解码器。
            mMyMediaProcessThread.AdoOtptSetStrmUseSpeexDecd(0, 1);


            //设置音频输出是否使用Opus解码器。
            //m_MyMediaPocsThrdPt.AdoOtptSetStrmUseOpusDecd(0);

            //设置音频输出流是否使用。
            mMyMediaProcessThread.AdoOtptSetStrmIsUse(0, 1);

            //设置音频输出是否保存音频到文件。
            mMyMediaProcessThread.AdoOtptSetIsSaveAdoToWaveFile(
                    0,
                    mExternalDirFullAbsPath + "/AdoOtpt.wav",
                    8 * 1024);

            //设置音频输出是否绘制音频波形到Surface。
            mMyMediaProcessThread.AdoOtptSetIsDrawAdoWavfmToSurface(
                    0,
                    null);

            //设置音频输出使用的设备。
            mMyMediaProcessThread.AdoOtptSetUseDvc(
                    0,
                    0);

            //设置音频输出是否静音。
            mMyMediaProcessThread.AdoOtptSetIsMute(0);

            //设置视频输入。
            mMyMediaProcessThread.SetVdoInpt(
                    12,
                    640,
                    480,
                    0,
                    mVideoInputView);   //TODO: HTSurfaceView


            //设置视频输入是否使用YU12原始数据。
            //m_MyMediaPocsThrdPt.VdoInptSetUseYU12();

            //设置视频输入是否使用OpenH264编码器。
            mMyMediaProcessThread.VdoInptSetUseOpenH264Encd(
                    0,
                    60,
                    3,
                    24,
                    1);
            //设置视频输入是否使用系统自带H264编码器。
            //m_MyMediaPocsThrdPt.VdoInptSetUseSystemH264Encd(............);

            //设置视频输入使用的设备。
            mMyMediaProcessThread.VdoInptSetUseDvc(
                    0,
                    -1,
                    -1);

            //设置视频输入是否黑屏。
            mMyMediaProcessThread.VdoInptSetIsBlack(0);

            //设置视频输出。
            mMyMediaProcessThread.VdoOtptAddStrm(0);
            mMyMediaProcessThread.VdoOtptSetStrm(
                    0,
                    mVideoOutputView);    //TODO: HTSurfaceView

            //设置视频输出是否使用YU12原始数据。
            //m_MyMediaPocsThrdPt.VdoOtptSetStrmUseYU12(0);


            //设置视频输出是否使用OpenH264解码器。
            mMyMediaProcessThread.VdoOtptSetStrmUseOpenH264Decd(0, 0);

            //设置视频输出是否使用系统自带H264解码器。
            //m_MyMediaPocsThrdPt.VdoOtptSetStrmUseSystemH264Decd(0);

            //设置视频输出是否黑屏。
            mMyMediaProcessThread.VdoOtptSetStrmIsBlack(
                    0,
                    0);

            //设置视频输出流是否使用。
            mMyMediaProcessThread.VdoOtptSetStrmIsUse(0, 1);

            //设置本端对讲模式。
            mMyMediaProcessThread.SendUserMsg(MyMediaPocsThrd.UserMsgTyp.LclTkbkMode, MyMediaPocsThrd.TkbkMode.AdoVdo);

            //设置是否保存设置到文件。
            //TODO 设置保存到文件
            mMyMediaProcessThread.SaveStngToFile(mExternalDirFullAbsPath + "/Setting.txt");

            //启动媒体处理线程。
            mMyMediaProcessThread.start();
            Log.i(TAG, "启动媒体处理线程完毕。");
        }

    }

    private void exitVideoTalkServer() {
        Log.i(TAG, "开始请求并等待媒体处理线程退出。");
        mMyMediaProcessThread.m_IsInterrupt = 1;
        mMyMediaProcessThread.RqirExit(1, 1);
        Log.i(TAG, "结束请求并等待媒体处理线程退出。");
        mMyMediaProcessThread = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "退出媒体处理");
        if (mMyMediaProcessThread != null) {
            Log.i(TAG, "开始请求并等待媒体处理线程退出。");
            mMyMediaProcessThread.m_IsInterrupt = 1;
            mMyMediaProcessThread.RqirExit(1, 1);
            Log.i(TAG, "结束请求并等待媒体处理线程退出。");
        }
        if (mVideoTalkHandler != null) {
            mVideoTalkHandler = null;
        }
    }
}