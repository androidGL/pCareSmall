package com.pcare.api.base;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.pcare.api.activity.Constant;

/**
 * @Author: gl
 * @CreateDate: 2019/10/29
 * @Description:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 进行科大讯飞SDK的初始化
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "="+Constant.APPID_XFYUN);
    }
}
