package com.pcare.api.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.pcare.api.R;
import com.pcare.api.adapter.QuestionSpeakAdapter;
import com.pcare.api.base.IPresenter;
import com.pcare.api.base.SimpleBaseActivity;
import com.pcare.api.entity.MsgEntity;
import com.pcare.api.utils.TTSUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * @Author: gl
 * @CreateDate: 2019/10/28
 * @Description:
 */
public class MajorSpeakActivity extends SimpleBaseActivity {
    private final String TAG = "MajorSpeakActivity";
    private List<String> queationTitleList = new ArrayList<>();
    private List<MsgEntity> msgEntityList = new ArrayList<>();
    private  RecyclerView.Adapter selectAdapter;
    private RecognizerListener mRecoListener;
    private String speechText;

    @BindView(R.id.question_list)
    RecyclerView QuestionListView;

    @BindView(R.id.request_bottom)
    TextView bottomSpeak;

    @BindView(R.id.request_finish)
    TextView questionFinish;
    @Override
    public int getLayoutId() {
        return R.layout.activity_speak;
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        msgEntityList.add(new MsgEntity(queationTitleList.get(0),2));
        selectAdapter = new QuestionSpeakAdapter(this,msgEntityList);
        QuestionListView.setLayoutManager(new LinearLayoutManager(this));
        QuestionListView.setAdapter(selectAdapter);
        selectAdapter.notifyDataSetChanged();
        TTSUtil.getInstance(getApplicationContext()).speaking(queationTitleList.get(0));
        queationTitleList.remove(0);
        bottomSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    speechText = "";
                    TTSUtil.getInstance(getApplicationContext()).startSpeech(mRecoListener);
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    TTSUtil.getInstance(getApplicationContext()).stopSpeech();
                }
                return true;
            }
        });


    }
    private  void init(){
        queationTitleList.addAll(Arrays.asList(getResources().getStringArray(R.array.list_question)));
        // 听写监听器
        mRecoListener = new RecognizerListener() {
            //isLast等于true 时会话结束。
            public void onResult(RecognizerResult results, boolean isLast) {
                Log.e(TAG, results.getResultString());
                speechText = speechText + TTSUtil.parseIatResult(results.getResultString());
                showTip("结果1" + speechText);
                if(isLast) {
                    showTip("结果2" + speechText);
                    msgEntityList.add(new MsgEntity(speechText, 1));
                    selectAdapter.notifyDataSetChanged();
                    if(null != queationTitleList && queationTitleList.size()>0) {
                        msgEntityList.add(new MsgEntity(queationTitleList.get(0), 2));
                        selectAdapter.notifyDataSetChanged();
                        TTSUtil.getInstance(getApplicationContext()).speaking(queationTitleList.get(0));
                        queationTitleList.remove(0);
                    }
                }
            }

            // 会话发生错误回调接口
            public void onError(SpeechError error) {
                showTip(error.getPlainDescription(true)) ;
                // 获取错误码描述
                Log. e(TAG, "error.getPlainDescription(true)==" + error.getPlainDescription(true ));
            }

            // 开始录音
            public void onBeginOfSpeech() {
                showTip(" 开始录音 ");
            }

            //volume 音量值0~30， data音频数据
            public void onVolumeChanged(int volume, byte[] data) {
                showTip(" 声音改变了 ");
            }

            // 结束录音
            public void onEndOfSpeech() {
            }

            // 扩展用接口
            public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
            }
        };
    }
    private void showTip(String info){
        Log.i(TAG,info);
    }

}
