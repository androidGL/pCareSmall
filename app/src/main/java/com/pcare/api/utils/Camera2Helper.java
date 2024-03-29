package com.pcare.api.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.pcare.api.net.Api;
import com.pcare.api.net.HttpClient;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.Response;

/**
 * @Author: gl
 * @CreateDate: 2019/10/29
 * @Description:
 */
public class Camera2Helper implements TextureView.SurfaceTextureListener {
    private final String TAG = "Camera2Helper";
    private Context mContext;
    private CameraDevice mCamera;
    private String mCameraID = "1";
    private Size mPreviewSize;
    private CaptureRequest.Builder mPreviewBuilder;
    private ImageReader mImageReader;
    private TextureView mPreviewView;
    private Handler mHandler;
    private HandlerThread mThreadHandler;
    private String imgBase64;
    private Handler timerHandler = new Handler();//执行循环定时的任务
    private Runnable timerRunnable;
    private  HashMap hashMapParam = new HashMap();
//    private int requestNum=0;

    // 这里定义的是ImageReader回调的图片的大小
    private int mImageWidth = 1920;
    private int mImageHeight = 1080;

    public Camera2Helper(Context context,TextureView previewView) {
        this.mContext = context;
        this.mPreviewView = previewView;
        mPreviewView.setSurfaceTextureListener(this);
        initLooper();
        initRequest();

    }
    // 很多过程都变成了异步的了，所以这里需要一个子线程的looper
    private void initLooper() {
        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());

    }

    private void initRequest(){
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(imgBase64)) {
                    timerHandler.postDelayed(this,2000);
//                    if(requestNum>=5){
//                        timerHandler.removeCallbacks(timerRunnable);
//                    }
//                    requestNum++;
                    return;
                }
                hashMapParam.clear();
                hashMapParam.put("usr_id","103");
                hashMapParam.put("image_base64",imgBase64);

                HttpClient.getInstance().post(Api.BASEURL+"search/base64", hashMapParam,new HttpClient.OKCallback() {
                    @Override
                    public void success(Response res) throws IOException {
                        showLog("Response:"+res.body().string());
                        timerHandler.removeCallbacks(timerRunnable);

                    }

                    @Override
                    public void failed(IOException e) {

                    }
                });
                timerHandler.postDelayed(this,2000);
            }
        };
        timerHandler.postDelayed(timerRunnable,2000);
    }
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            try {
                mCamera = camera;
                startPreview(mCamera);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };
    private void updatePreview(CameraCaptureSession session)
            throws CameraAccessException {
        session.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
    }
    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                updatePreview(session);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    };
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {

        /**
         * 当有一张图片可用时会回调此方法，但有一点一定要注意： 一定要调用
         * reader.acquireNextImage()和close()方法，否则画面就会卡住！！！！！我被这个坑坑了好久！！！
         **/
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image img = reader.acquireNextImage();
            /**
             * 因为Camera2并没有Camera1的Priview回调！！！所以该怎么能到预览图像的byte[]呢？就是在这里了！！！
             **/
            ByteBuffer buffer = img.getPlanes()[0].getBuffer();
            // 这里就是图片的byte数组了
            byte[] bytes = new byte[buffer.remaining()];

            imgBase64 = Base64.encodeToString(bytes,Base64.NO_WRAP);
            showLog("获取到图片啦...   图片大小：" + bytes.length+"  imgBase64:"+imgBase64);

            img.close();
        }
    };

    private void showLog(String info){
        Log.i(TAG, info);
    }
    // 开始预览，主要是camera.createCaptureSession这段代码很重要，创建会话
    private void startPreview(CameraDevice camera) throws CameraAccessException {
        SurfaceTexture texture = mPreviewView.getSurfaceTexture();

        // 这里设置的就是预览大小
        texture.setDefaultBufferSize(mPreviewSize.getWidth(),mPreviewSize.getHeight());
        Surface surface = new Surface(texture);
        try {
            // 设置捕获请求为预览，这里还有拍照啊，录像等
            mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // 就是在这里，通过这个set(key,value)方法，设置曝光啊，自动聚焦等参数！！ 如下举例：
        // mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

        mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight,
                ImageFormat.JPEG/* 此处还有很多格式，比如我所用到YUV等 */, 2/*
                 * 最大的图片数，
                 * mImageReader里能获取到图片数
                 * ，
                 * 但是实际中是2+1张图片，就是多一张
                 */);

        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,mHandler);
        // 这里一定分别add两个surface，一个Textureview的，一个ImageReader的，如果没add，会造成没摄像头预览，或者没有ImageReader的那个回调
        mPreviewBuilder.addTarget(surface);
        mPreviewBuilder.addTarget(mImageReader.getSurface());
        camera.createCaptureSession(
                Arrays.asList(surface, mImageReader.getSurface()),
                mSessionStateCallback, mHandler);
    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            // 获得所有摄像头的管理者CameraManager
            CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            // 获得某个摄像头的特征，支持的参数
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraID);
            // 支持的STREAM CONFIGURATION
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            // 摄像头支持的预览Size数组
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            // 打开相机
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            cameraManager.openCamera(mCameraID, mCameraDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void close(){
        if (null != mCamera) {
            mCamera.close();
            mCamera = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
        mThreadHandler = null;
        timerHandler = null;
        mHandler = null;
    }
}
