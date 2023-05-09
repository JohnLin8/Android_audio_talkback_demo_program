package com.example.andrd_ado_vdo_tkbk_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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


}