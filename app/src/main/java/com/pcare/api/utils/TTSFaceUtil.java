package com.pcare.api.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.IdentityListener;
import com.iflytek.cloud.IdentityResult;
import com.iflytek.cloud.IdentityVerifier;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author: gl
 * @CreateDate: 2019/10/29
 * @Description:
 */
public class TTSFaceUtil {
    private IdentityVerifier mIdVerifier;
    //上下文
    private Context mContext;

    private volatile static TTSFaceUtil instance;
    private static final String TAG = "TTSUtil";
    // 模型操作
    private int mModelCmd;
    // 删除模型
    private final static int MODEL_DEL = 1;
    private String mAuthid = null;
    // 进度对话框
    private ProgressDialog mProDialog;
    private Bitmap mImage = null;
    private byte[] mImageData = null;
    /**
     * 人脸注册监听器
     */
    private IdentityListener mEnrollListener = new IdentityListener() {

        @Override
        public void onResult(IdentityResult result, boolean islast) {
            Log.d(TAG, result.getResultString());

            if (null != mProDialog) {
                mProDialog.dismiss();
            }

            try {
                JSONObject object = new JSONObject(result.getResultString());
                int ret = object.getInt("ret");

                if (ErrorCode.SUCCESS == ret) {
                    showTip("注册成功");
                } else {
                    showTip(new SpeechError(ret).getPlainDescription(true));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        @Override
        public void onError(SpeechError error) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }

            showTip(error.getPlainDescription(true));
        }

    };

    /**
     * 人脸验证监听器
     */
    private IdentityListener mVerifyListener = new IdentityListener() {

        @Override
        public void onResult(IdentityResult result, boolean islast) {
            Log.d(TAG, result.getResultString());

            if (null != mProDialog) {
                mProDialog.dismiss();
            }

            try {
                JSONObject object = new JSONObject(result.getResultString());
                Log.d(TAG, "object is: " + object.toString());
                String decision = object.getString("decision");

                if ("accepted".equalsIgnoreCase(decision)) {
                    showTip("通过验证");
                } else {
                    showTip("验证失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        @Override
        public void onError(SpeechError error) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }

            showTip(error.getPlainDescription(true));
        }

    };

    /**
     * 人脸模型操作监听器
     */
    private IdentityListener mModelListener = new IdentityListener() {

        @Override
        public void onResult(IdentityResult result, boolean islast) {
            Log.d(TAG, result.getResultString());

            JSONObject jsonResult = null;
            int ret = ErrorCode.SUCCESS;
            try {
                jsonResult = new JSONObject(result.getResultString());
                ret = jsonResult.getInt("ret");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 根据操作类型判断结果类型
            switch (mModelCmd) {
                case MODEL_DEL:
                    if (ErrorCode.SUCCESS == ret) {
//                        online_authid.setEnabled(true);
                        showTip("删除成功");
                    } else {
                        showTip("删除失败");
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        @Override
        public void onError(SpeechError error) {
            // 弹出错误信息
            showTip(error.getPlainDescription(true));
        }

    };

    public TTSFaceUtil(Context context) {
        mContext = context;
        mIdVerifier = IdentityVerifier.createVerifier(context, new InitListener() {
            @Override
            public void onInit(int errorCode) {
                if (ErrorCode.SUCCESS == errorCode) {
                    showTip("引擎初始化成功");
                } else {
                    showTip("引擎初始化失败，错误码：" + errorCode + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                }
            }
        });
        mProDialog = new ProgressDialog(context);
        mProDialog.setCancelable(true);
        mProDialog.setTitle("请稍后");

        mProDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // cancel进度框时,取消正在进行的操作
                if (null != mIdVerifier) {
                    mIdVerifier.cancel();
                }
            }
        });
    }

    public static TTSFaceUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (TTSFaceUtil.class) {
                if (instance == null) {
                    instance = new TTSFaceUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 根据mEnrollListener的onResult回调方法中得到注册结果
     */
    private void setRegisterParam(String authid) {
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr");
// 设置会话类型
        mIdVerifier.setParameter(SpeechConstant.MFV_SST, "enroll");
// 设置用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, authid);
// 注册监听器（IdentityListener）mEnrollListener，开始会话。
// 并通过mEnrollListener中onResult回调中获得json格式结果
        mIdVerifier.startWorking(mEnrollListener);
// 子业务执行参数，若无可以传空字符传
        StringBuffer params = new StringBuffer();
// 写入数据，mImageData为图片的二进制数据
        mIdVerifier.writeData("ifr", params.toString(), mImageData, 0, mImageData.length);
// 停止写入
        mIdVerifier.stopWrite("ifr");
    }

    /**
     * 通过mVerifyListener中onResult回调中获得json格式结果
     */
    private void setVerifyParam(String authid) {
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr");
// 设置会话类型
        mIdVerifier.setParameter(SpeechConstant.MFV_SST, "verify");
// 设置验证模式，单一验证模式：sin
        mIdVerifier.setParameter(SpeechConstant.MFV_VCM, "sin");
// 用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, authid);
// 注册监听器（IdentityListener）mVerifyListener，开始会话。
        mIdVerifier.startWorking(mVerifyListener);
// 子业务执行参数，若无可以传空字符传
        StringBuffer params = new StringBuffer();
// 写入数据，mImageData为图片的二进制数据
        mIdVerifier.writeData("ifr", params.toString(), mImageData, 0, mImageData.length);
// 停止写入
        mIdVerifier.stopWrite("ifr");
    }

    /**
     * 人脸注册成功后，在语音云端上会生成一个对应的模型来存储人脸信息，人脸模型的操 作即对模型进行删除，暂时尚未支持查询"query"操作
     */
//    private void deleteFace(String authid ){
//        // 设置会话场景
//        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr");
//// 用户id
//        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, authid);
//// 设置模型参数，若无可以传空字符传
//        StringBuffer params = new StringBuffer();
//// 执行模型操作，cmd取值"delete" 删除
//        mIdVerifier.execute("ifr", cmd, params.toString(), mModelListener);
//    }

    /**
     * 鉴别与验证的过程相似，不过鉴别需要设置组 ID，以指定要鉴别的组。 其参数设置如下：
     */
//    private void identifyFace(String groudID){
//        // 设置业务场景
//        mIdVerifier.setParameter( SpeechConstant.MFV_SCENES, "ifr" );
//// 设置业务类型：鉴别（identify）
//        mIdVerifier.setParameter( SpeechConstant.MFV_SST, "identify" );
//// 设置监听器，开始会话
//        mIdVerifier.startWorking( mVerifyListener );
//// 指定组id，最相似结果数
//        String params = "group_id="+groudID;
//        while( !isDataFinished ){
//            // 写入数据
//            mIdVerifier.writeData( scence, params, data, offset, length );
//        }
//        mIdVerifier.stopWrite( scence );
//    }
    private void showTip(String info) {
        Log.i(TAG, info);
    }

    private void register(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            showTip("注册失败");
            return;
        }
        if ("success".equals(obj.get("rst"))) {
            showTip("注册成功");
        } else {
            showTip("注册失败");
        }
    }

    private void verify(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            showTip("验证失败");
            return;
        }
        if ("success".equals(obj.get("rst"))) {
            if (obj.getBoolean("verf")) {
                showTip("通过验证，欢迎回来！");
            } else {
                showTip("验证不通过");
            }
        } else {
            showTip("验证失败");
        }
    }

    private void executeModelCommand(String cmd) {
        // 设置模型参数，若无可以传空字符传
        StringBuffer params = new StringBuffer();
        // 执行模型操作
        mIdVerifier.execute("ifr", cmd, params.toString(), mModelListener);
    }
}
