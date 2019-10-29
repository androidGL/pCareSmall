package com.pcare.api.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @Author: gl
 * @CreateDate: 2019/10/29
 * @Description:
 */
public class TTSUtil {
    // 发音人
    public final static String[] COLOUD_VOICERS_VALUE = {"xiaoyan", "xiaoyu", "catherine", "henry", "vimary", "vixy", "xiaoqi", "vixf", "xiaomei","xiaolin", "xiaorong", "xiaoqian", "xiaokun", "xiaoqiang", "vixying", "xiaoxin", "nannan", "vils",};

    private static final String TAG = "TTSUtil";
    // 语音合成对象
    private static SpeechSynthesizer mTts;
    //语音转文字对象
    private SpeechRecognizer mIat;
    //上下文
    private Context mContext;

    private volatile static TTSUtil instance;
    /**
     * 合成回掉监听
     */
    private static SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            Log.d(TAG, "开始播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // TODO 缓冲的进度
            Log.d(TAG, "缓冲 : " + percent);
        }

        @Override
        public void onSpeakPaused() {
            Log.d(TAG, "暂停播放");

        }

        @Override
        public void onSpeakResumed() {
            Log.d(TAG, "继续播放");
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // TODO 说话的进度
            Log.d(TAG, "合成 : " + percent);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Log.d(TAG, "播放完成");

            } else if (error != null) {
                Log.d(TAG, error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    private TTSUtil(Context context) {
        mContext = context;
        // 初始化语音合成对象
        mTts = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    Log.d("fjj", "初始化失败,错误码：" + code);
                }
                Log.d("fjj", "初始化失败,q错误码：" + code);
            }
        });
        //初始化语音识别器
        mIat = SpeechRecognizer.createRecognizer( mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    showTip("初始化失败 ");
                }

            }
        });
    }
    public static TTSUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (TTSUtil.class) {
                if (instance == null) {
                    instance = new TTSUtil(context);
                }
            }
        }
        return  instance;
    }
    /**
     * 开始合成
     *
     * @param text
     */
    public void speaking(String text) {
        if (TextUtils.isEmpty(text))
            return;
        int code = mTts.startSpeaking(text, mTtsListener);

        Log.d("fjj", "-----" + code + "++++++++++");

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                Toast.makeText(mContext, "没有安装语音+ code = " + code, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "语音合成失败,错误码: " + code, Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 停止语音播报
     */
    public static void stopSpeaking() {
        // 对象非空并且正在说话
        if (null != mTts && mTts.isSpeaking()) {
            // 停止说话
            mTts.stopSpeaking();
        }
    }
    /**
     * 判断当前有没有说话
     *
     * @return
     */
    public  static boolean isSpeaking() {
        if (null != mTts) {
            return mTts.isSpeaking();
        } else {
            return false;
        }
    }
    /**
     * 参数设置
     *
     * @return
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 引擎类型 网络
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, COLOUD_VOICERS_VALUE[0]);
        // 设置语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        // 设置音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        // 设置音量
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/KRobot/wavaudio.pcm");
        // 背景音乐  1有 0 无
        // mTts.setParameter("bgs", "1");
    }


    //以下是语音转文字

    public void startSpeech(RecognizerListener listener ) {
        mIat.setParameter(SpeechConstant. DOMAIN, "iat" );// 短信和日常用语： iat (默认)
        mIat.setParameter(SpeechConstant. LANGUAGE, "zh_cn" );// 设置中文
        mIat.setParameter(SpeechConstant. ACCENT, "mandarin" );// 设置普通话
        // 开始听写
        mIat.startListening( listener);

    }

    public void stopSpeech(){
        if(null != mIat&& !mIat.isListening())
            mIat.stopListening();
    }
    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer() ;
        try {
            JSONTokener tokener = new JSONTokener(json) ;
            JSONObject joResult = new JSONObject(tokener) ;

            JSONArray words = joResult.getJSONArray("ws" );
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw" );
                JSONObject obj = items.getJSONObject(0 );
                ret.append(obj.getString("w" ));
//                  如果需要多候选结果，解析数组其他字段
//                 for(int j = 0; j < items.length(); j++)
//                 {
//                      JSONObject obj = items.getJSONObject(j);
//                      ret.append(obj.getString("w"));
//                 }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    private void showTip(String info){
        Log.i(TAG,info);
    }
//    public class PCareRecognizerListener implements RecognizerListener {
//        @Override
//        public void onResult(RecognizerResult recognizerResult, boolean b) {
//
//        }
//
//        // 会话发生错误回调接口
//        @Override
//        public void onError(SpeechError error) {
//            showTip(error.getPlainDescription(true)) ;
//            // 获取错误码描述
//            Log. e(TAG, "error.getPlainDescription(true)==" + error.getPlainDescription(true ));
//        }
//
//        // 开始录音
//        @Override
//        public void onBeginOfSpeech() {
//            showTip(" 开始录音 ");
//        }
//
//        //volume 音量值0~30， data音频数据
//        @Override
//        public void onVolumeChanged(int volume, byte[] data) {
//            showTip(" 声音改变了 ");
//        }
//
//        // 结束录音
//        @Override
//        public void onEndOfSpeech() {
//            // 通知结束说话
//            Log.e(TAG, "结束说话");
//            //判断语音唤醒是否处于监听状态，不处于则开始监听
//            if (!mIat.isListening()) {
//                mIat.startListening(mWakeuperListener);
//            }
//        }
//
//        // 扩展用接口
//        @Override
//        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
//        }
//
//
//
//
//
//
//    }

}