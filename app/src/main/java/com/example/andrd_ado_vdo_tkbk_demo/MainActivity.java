package com.example.andrd_ado_vdo_tkbk_demo;

import static com.example.andrd_ado_vdo_tkbk_demo.VideoTalkSettings.*;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import HeavenTao.Media.*;

//主界面消息处理。
class MainActivityHandler extends Handler {
    static String m_CurClsNameStrPt = "MainActivityHandler"; //当前类名称字符串的指针。

    MainActivity m_MainActivityPt; //存放主界面的指针。
    ServiceConnection m_FrgndSrvcCnctPt; //存放前台服务连接器的指针。
    AlertDialog m_RqstCnctDlgPt; //存放请求连接对话框的指针。

    public enum Msg {
        MediaPocsThrdInit, //主界面消息：初始化媒体处理线程。
        MediaPocsThrdDstoy, //主界面消息：销毁媒体处理线程。
        RqstCnctDlgInit, //主界面消息：初始化请求连接对话框。
        RqstCnctDlgDstoy, //主界面消息：销毁请求连接对话框。
        PttBtnInit,  //主界面消息：初始化一键即按即通按钮。
        PttBtnDstoy, //主界面消息：销毁一键即按即通按钮。
        ShowLog, //主界面消息：显示日志。
        Vibrate, //主界面消息：振动。
    }

    /**
     * function: just make buttons enable or disable, no other operations. ---- by JohnLin
     *
     * @param MessagePt 消息。
     */
    public void handleMessage(Message MessagePt) {
        switch (Msg.values()[MessagePt.what]) {
            case MediaPocsThrdInit: {

                //创建并绑定前台服务，从而确保本进程在转入后台或系统锁屏时不会被系统限制运行，且只能放在主线程中执行，因为要使用界面。
                if (m_FrgndSrvcCnctPt == null) {  //这里有个是否前台服务判断，先固定使用前台服务 ---- by JohnLin
                    m_FrgndSrvcCnctPt = new ServiceConnection() //创建存放前台服务连接器。
                    {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) //前台服务绑定成功。
                        {
                            ((FrgndSrvc.FrgndSrvcBinder) service).SetForeground(m_MainActivityPt); //将服务设置为前台服务。
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) //前台服务解除绑定。
                        {

                        }
                    };
                    m_MainActivityPt.bindService(new Intent(m_MainActivityPt, FrgndSrvc.class), m_FrgndSrvcCnctPt, Context.BIND_AUTO_CREATE); //创建并绑定前台服务。
                }
                break;
            }
            case MediaPocsThrdDstoy: {
                m_MainActivityPt.m_MyMediaPocsThrdPt = null;

                if (m_FrgndSrvcCnctPt != null) //如果已经创建并绑定了前台服务。
                {
                    m_MainActivityPt.unbindService(m_FrgndSrvcCnctPt); //解除绑定并销毁前台服务。
                    m_FrgndSrvcCnctPt = null;
                }

                break;
            }
            case RqstCnctDlgInit: {
                AlertDialog.Builder builder = new AlertDialog.Builder(m_MainActivityPt);

                builder.setCancelable(false); //点击对话框以外的区域是否让对话框消失
                builder.setTitle(R.string.app_name);

                if (m_MainActivityPt.m_MyMediaPocsThrdPt.mTalkNetwork.m_IsCreateSrvrOrClnt == 1) //如果是创建服务端。
                {
                    builder.setMessage("您是否允许远端[" + MessagePt.obj + "]的连接？");

                    //设置正面按钮。
                    builder.setPositiveButton("允许", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_MainActivityPt.m_MyMediaPocsThrdPt.m_RqstCnctRslt = 1;
                            m_RqstCnctDlgPt = null;
                        }
                    });
                    //设置反面按钮。
                    builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_MainActivityPt.m_MyMediaPocsThrdPt.m_RqstCnctRslt = 2;
                            m_RqstCnctDlgPt = null;
                        }
                    });
                } else //如果是创建客户端。
                {
                    builder.setMessage("等待远端[" + MessagePt.obj + "]允许您的连接...");

                    //设置反面按钮。
                    builder.setNegativeButton("中断", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_MainActivityPt.m_MyMediaPocsThrdPt.m_RqstCnctRslt = 2;
                            m_RqstCnctDlgPt = null;
                        }
                    });
                }

                m_RqstCnctDlgPt = builder.create(); //创建AlertDialog对象。
                m_RqstCnctDlgPt.show();
                break;
            }
            case RqstCnctDlgDstoy: {
                if (m_RqstCnctDlgPt != null) {
                    m_RqstCnctDlgPt.cancel();
                    m_RqstCnctDlgPt = null;
                }
                break;
            }
            case ShowLog: {
                TextView p_LogTextView = new TextView(m_MainActivityPt);
                p_LogTextView.setText((new SimpleDateFormat("HH:mm:ss SSS")).format(new Date()) + "：" + MessagePt.obj);
                ((LinearLayout) m_MainActivityPt.m_MainLyotViewPt.findViewById(R.id.LogLinearLyotId)).addView(p_LogTextView);
                break;
            }
            case Vibrate: {
                ((Vibrator) m_MainActivityPt.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
                break;
            }
        }
    }
}

//主界面。
public class MainActivity extends AppCompatActivity {
    String m_CurClsNameStrPt = this.getClass().getSimpleName(); //存放当前类名称字符串。

    View m_MainLyotViewPt; //存放主布局视图的指针。

    View m_CurActivityLyotViewPt; //存放当前界面布局视图的指针。
    MyMediaPocsThrd m_MyMediaPocsThrdPt; //存放媒体处理线程的指针。
    MainActivityHandler m_MainActivityHandlerPt; //存放主界面消息处理的指针。

    String m_ExternalDirFullAbsPathStrPt; //存放扩展目录完整绝对路径字符串的指针。

    //Activity创建消息。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(m_CurClsNameStrPt, "onCreate");

        //创建布局。
        {
            LayoutInflater p_LyotInflater = LayoutInflater.from(this);

            m_MainLyotViewPt = p_LyotInflater.inflate(R.layout.main_lyot, null);

        }

        //显示布局。
        setContentView(m_MainLyotViewPt); //设置主界面的内容为主布局。
        m_CurActivityLyotViewPt = m_MainLyotViewPt; //设置当前界面布局视图。


        //初始化消息处理。
        m_MainActivityHandlerPt = new MainActivityHandler();
        m_MainActivityHandlerPt.m_MainActivityPt = this;

        //设置IP地址编辑框的内容。
        try {
            OutSetIPAddrEdit:
            {
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
                                ((EditText) m_MainLyotViewPt.findViewById(R.id.IPAddrEdTxtId)).setText(clInetAddress.getHostAddress());
                                break OutSetIPAddrEdit;
                            }
                        }
                    }
                }

                ((EditText) m_MainLyotViewPt.findViewById(R.id.IPAddrEdTxtId)).setText("0.0.0.0"); //如果没有获取到IP地址，就设置为本地地址。
            }
        } catch (SocketException e) {
        }


        //设置默认设置
        //TODO: 效果等级
//        OnClickUseEffectSuperRdBtn(null);   //默认效果等级：超。
//        OnClickUseBitrateSuperRdBtn(null);  //默认比特率等级：超。

        //获取扩展目录完整绝对路径字符串。
        {
            if (getExternalFilesDir(null) != null) {
                m_ExternalDirFullAbsPathStrPt = getExternalFilesDir(null).getPath();
            } else {
                m_ExternalDirFullAbsPathStrPt = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + getApplicationContext().getPackageName();
            }

            String p_InfoStrPt = "扩展目录完整绝对路径：" + m_ExternalDirFullAbsPathStrPt;
            Log.i(m_CurClsNameStrPt, p_InfoStrPt);
            Message p_MessagePt = new Message();
            p_MessagePt.what = MainActivityHandler.Msg.ShowLog.ordinal();
            p_MessagePt.obj = p_InfoStrPt;
            m_MainActivityHandlerPt.sendMessage(p_MessagePt);
        }
    }

    //主界面返回键消息。
    @Override
    public void onBackPressed() {
        Log.i(m_CurClsNameStrPt, "onBackPressed");

        if (m_CurActivityLyotViewPt == m_MainLyotViewPt) {
            Log.i(m_CurClsNameStrPt, "用户在主界面按下返回键，本软件退出。");
            if (m_MyMediaPocsThrdPt != null) {
                Log.i(m_CurClsNameStrPt, "开始请求并等待媒体处理线程退出。");
                m_MyMediaPocsThrdPt.m_IsInterrupt = 1;
                m_MyMediaPocsThrdPt.RqirExit(1, 1);
                Log.i(m_CurClsNameStrPt, "结束请求并等待媒体处理线程退出。");
            }
            System.exit(0);
        }
    }

    //创建服务器和连接服务器按钮。
    public void OnClickCreateSrvrAndCnctSrvrBtn(View ViewPt) {
        int p_Rslt = -1; //存放本函数执行结果的值，为0表示成功，为非0表示失败。

        Out:
        {
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
                    m_MyMediaPocsThrdPt.m_AAjbPt.m_MinNeedBufFrmCnt = AdaptiveJitterBuffer.AUDIO_MIN_FRAME_NUM;
                    m_MyMediaPocsThrdPt.m_AAjbPt.m_MaxNeedBufFrmCnt = AdaptiveJitterBuffer.AUDIO_MAX_FRAME_NUM;
                    m_MyMediaPocsThrdPt.m_AAjbPt.m_MaxCntuLostFrmCnt = AdaptiveJitterBuffer.AUDIO_MAX_DROP_FRAME_NUM;
                    m_MyMediaPocsThrdPt.m_AAjbPt.m_AdaptSensitivity = AdaptiveJitterBuffer.AUDIO_ADAPTIVE_SENSITIVITY;
                    //视频参数
                    m_MyMediaPocsThrdPt.m_VAjbPt.m_MinNeedBufFrmCnt = AdaptiveJitterBuffer.VIDEO_MIN_FRAME_NUM;
                    m_MyMediaPocsThrdPt.m_VAjbPt.m_MaxNeedBufFrmCnt = AdaptiveJitterBuffer.VIDEO_MAX_FRAME_NUM;
                    m_MyMediaPocsThrdPt.m_VAjbPt.m_AdaptSensitivity = AdaptiveJitterBuffer.VIDEO_ADAPTIVE_SENSITIVITY;
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
                            TripleEchoCanceller.MODE_SPEEX_WEBRTC_FLOAT,
                            TripleEchoCanceller.SPEEX_AEC_FILTER_LENGTH,
                            TripleEchoCanceller.SPEEX_AEC_USE_REVERSE,
                            TripleEchoCanceller.SPEEX_AEC_REVERSE_MULTIPLE,
                            TripleEchoCanceller.SPEEX_AEC_REVERSE_LAST_ACTIVE,
                            TripleEchoCanceller.SPEEX_AEC_REVERSE_SUPPRESS_MAX_DB,
                            TripleEchoCanceller.SPEEX_AEC_REVERSE_SUPPRESS_ACTIVE_MAX_DB,
                            TripleEchoCanceller.WEBRTC_FIXED_AEC_USE_COMFORT_NOISE,
                            TripleEchoCanceller.WEBRTC_FIXED_AEC_SUPPRESS_LEVEL,
                            TripleEchoCanceller.WEBRTC_FIXED_AEC_DELAY,
                            TripleEchoCanceller.WEBRTC_FLOAT_AEC_SUPPRESS_LEVEL,
                            TripleEchoCanceller.WEBRTC_FLOAT_AEC_DELAY,
                            TripleEchoCanceller.WEBRTC_FLOAT_AEC_USE_DELAY_AGNOSTIC,
                            TripleEchoCanceller.WEBRTC_FLOAT_AEC_USE_EXPERIMENTAL_AGC,
                            TripleEchoCanceller.WEBRTC_FLOAT_AEC_USE_REFINED_FILTER_ADAPTATION,
                            TripleEchoCanceller.WEBRTC_FLOAT_AEC_USE_ADAPTIVE_DELAY,
                            TripleEchoCanceller.USE_SIMULTANEOUS_SPEECH_DETECTION,
                            TripleEchoCanceller.MIN_VAD_SATISFIED_MS);
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
                        SpeexPreprocessor.USE_SPEEX_PREPROCESSOR,
                        SpeexPreprocessor.USE_VOICEACTIVITY_DETECTION,
                        SpeexPreprocessor.VAD_PROB_START_SPEECH,
                        SpeexPreprocessor.VAD_PROB_STOP_SPEECH,
                        SpeexPreprocessor.USE_AGC,
                        SpeexPreprocessor.AGC_TARGET_LEVEL_DBFS,
                        SpeexPreprocessor.AGC_MAX_DB_PER_SEC,
                        SpeexPreprocessor.AGC_MAX_GAIN_DB,
                        SpeexPreprocessor.AGC_TARGET_GAIN_DB);

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
                m_MyMediaPocsThrdPt.SendUserMsg(MyMediaPocsThrd.UserMsgTyp.LclTkbkMode,MyMediaPocsThrd.TkbkMode.AdoVdo);

                //设置是否保存设置到文件。
                //m_MyMediaPocsThrdPt.SaveStngToFile(m_ExternalDirFullAbsPathStrPt + "/Setting.txt");

                //启动媒体处理线程。
                m_MyMediaPocsThrdPt.start();

                Log.i(m_CurClsNameStrPt, "启动媒体处理线程完毕。");
            } else {
                Log.i(m_CurClsNameStrPt, "开始请求并等待媒体处理线程退出。");
                m_MyMediaPocsThrdPt.m_IsInterrupt = 1;
                m_MyMediaPocsThrdPt.RqirExit(1, 1);
                Log.i(m_CurClsNameStrPt, "结束请求并等待媒体处理线程退出。");
            }

            p_Rslt = 0;
        }

        if (p_Rslt != 0) //如果启动媒体处理线程失败。
        {
            m_MyMediaPocsThrdPt = null;
        }
    }





}