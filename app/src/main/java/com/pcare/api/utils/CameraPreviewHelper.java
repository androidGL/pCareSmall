package com.pcare.api.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
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
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;

import com.pcare.api.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/10/29
 * @Description:
 */
public class CameraPreviewHelper {
//    private String Tag = "CameraPreviewHelper";
//    private Context mContext;
//    private TextureView mTextureView;
//
//    private HandlerThread mHandlerThread;
//    private Handler childHandler;
//    private Handler mHandler;
//    private Activity activity;
//
//
//    //private TrackThread mTrackThread;
//    private LivenessTackThread mLiveTackThread;
//
//    private TrackAndSearchThread trackAndSearchThread;
//
//    private FaceDrawerView mFaceDrawerView;
//
//    private String mCameraId;
//    private Size mPreviewSize;
//    private CameraManager mCameraManager;//摄像头管理器
//    private CameraDevice mCameraDevice;
//    private SurfaceTexture mSurfaceTexture = null;
//    private CaptureRequest.Builder mCaptureRequestBuilder;
//    private CaptureRequest mCaptureRequest;
//    private CameraCaptureSession mCameraCaptureSession;
//
//    private boolean isConnect = false;
//
//    private ImageReader mImageReader;
//    private int capacity = -1;
//    private static byte[] yv12bytes;
//    private static byte[] imgData;  //1280*720 img
//    private int imgFormat;
//    private int imgHeight;
//    private int ingWidth;
//
//    public final static int picMode = 640;   //720p ,1080p 640p
//
//
//    public static final int CAPTURE_IMG_PREPARED = 1;
//    public static final int TRY_TO_CAPTURE_IMG = 2;
//
//    //  private static boolean isLiveness = false;
//
//    public static int preview_width;      //preview
//    public static int preview_higth;
//    public static int surface_width;    //surface
//    public static int surface_higth;
//
//    public static int delaytest = 1;
//
//    public CameraPreviewHelper(Activity activity, TextureView textureView) {
//        mContext = activity.getBaseContext();
//        mTextureView = textureView;
//        this.activity = activity;
//        mFaceDrawerView = new FaceDrawerView(mContext);
//        ((RelativeLayout) activity.findViewById(R.id.rootview_relativelayout_camera_verity)).addView(mFaceDrawerView);
//        mHandlerThread = new HandlerThread(Tag);
//        mHandlerThread.start();
//        childHandler = new Handler(mHandlerThread.getLooper());
//        mHandler = new Handler(activity.getMainLooper(), mHandlerCallback);
//
//    }
//
//    /**
//     * open camera and start preview
//     *
//     * @return
//     */
//
//    public boolean Connect(boolean isliveness) {
//        if (mContext == null || mTextureView == null) {
//            Log.e("asda", "Connect");
//            return false;
//        }
//
//
//        //   isLiveness = false;// isliveness;
//
//        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
//        // mSurfaceTexture = mTextureView.getSurfaceTexture();
//
//        if (mCameraDevice != null)
//            mCameraDevice.close();
//
//        isConnect = true;
//
////        if (isLiveness) {
////            mLiveTackThread = new LivenessTackThread(activity, mFaceDrawerView);
////            mLiveTackThread.start();
////        } else {
//
////            mTrackThread = new TrackThread(activity, mFaceDrawerView, mTextName);
////            mTrackThread.start();
//        trackAndSearchThread = new TrackAndSearchThread(activity, mFaceDrawerView);
//        trackAndSearchThread.start();
//        //     }
//        return true;
//
//    }
//
//    public void DisConnect() {
//        mContext = null;
////        if (mSurfaceTexture != null) {
////            mSurfaceTexture.release();
////            Log.e(Tag, "mSurfaceTexture release");
////
////
////        }
//        activity = null;
//
////            if (mTrackThread != null)
////                mTrackThread.stopTrack();
//        if (trackAndSearchThread != null)
//            trackAndSearchThread.stopTrack();
//
//
//        if (mTextureView != null) {
//            mTextureView = null;
//
//        }
//        // mPreviewed=false;
//        if (childHandler != null) {
//            childHandler.removeCallbacksAndMessages(null);
//            childHandler = null;
//        }
//        if (mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
//        mHandler = null;
//
//        if (mHandlerThread != null) {
//            mHandlerThread.quit();
//            mHandlerThread = null;
//        }
//
//
//        if (null != mCameraCaptureSession) {
//            mCameraCaptureSession.close();
//            mCameraCaptureSession = null;
//        }
//        if (null != mCameraDevice) {
//            mCameraDevice.close();
//            mCameraDevice = null;
//        }
//        if (null != mImageReader) {
//            mImageReader.close();
//            mImageReader = null;
//        }
//    }
//
//    TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
//        @Override
//        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//
//            //  mSurfaceTexture=surface;
//            try {
//                if (mCameraManager == null) {
//                    //获取摄像头管理
//                    mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
//                    String[] CameraIdList=mCameraManager.getCameraIdList();//获取可用相机列表
//                    Log.e(Tag,"可用相机的个数是:"+CameraIdList.length);
//                    mCameraId = CameraIdList[0];
//                    CameraCharacteristics cameraCharacteristics=mCameraManager.getCameraCharacteristics(mCameraId);//获取某个相机(摄像头特性)
//                    cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);//检查支持
//
//                    //打开摄像头
//                    try {
//                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                            return;
//                        }
//                        if (mCameraManager == null)
//                            return;
//                        //镜面效果
//                        surface_width = width;
//                        surface_higth = height;
//                        //获取StreamConfigurationMap，他是管理摄像头支持的所有输出格式和尺寸
//                        StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//                        //获得最接近的尺寸大小
//                        mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class),width,height);
//                        Log.d(Tag, "getOptimalPreviewSize: w" + mPreviewSize.getWidth() + "  h" + mPreviewSize.getHeight());
//                        if (picMode == 720) {
//                            mPreviewSize = null;
//                            mPreviewSize = new Size(1280, 720);
//                            preview_higth = mPreviewSize.getWidth();
//                            preview_width = mPreviewSize.getHeight();
//                        } else {
//                            preview_higth = mPreviewSize.getWidth();
//                            preview_width = mPreviewSize.getHeight();
//                        }
//                        //如果视频显示需要角度旋转 用该函数进行角度转正
//                        configureTransform(surface_width, surface_higth);
//                        /**
//                         * 实时帧数据获取类
//                         * 由于获取实时帧所以选用YV12或者YUV_420_888两个格式，暂时不采用JPEG格式
//                         * 在真机显示的过程中,不同的数据格式所设置的width和height需要注意，否侧视频会很卡顿
//                         * YV12:width 720， height 960
//                         * YUV_420_888：width 720， height 960
//                         * JPEG:获取帧数据不能用 ImageFormat.JPEG 格式，否则你会发现预览非常卡的，因为渲染 JPEG 数据量过大，导致掉帧，所以预览帧请使用其他编码格式
//                         */
//                        mImageReader = ImageReader.newInstance(720, 960, ImageFormat.YV12, 10);//YUV_420_888
//                        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);
//
//                        //打开摄像头
//                        mCameraManager.openCamera(mCameraId, stateCallback, mHandler);
//
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                if (activity != null)
//                    activity.sendBroadcast(new Intent(CameraErrorReceiver.ACTION_CAMERA_OPEN_ERROR));
//            }
//        }
//
//        @Override
//        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//            configureTransform(width, height);
//        }
//
//        @Override
//        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//            if (mCameraDevice != null) {
//                mCameraDevice.close();
//                mCameraDevice = null;
//                Log.d(Tag, "onSurfaceTextureDestroyed: stopPreview");
//                // mPreviewed = false;
//            }
//            Log.e(Tag, "onSurfaceTextureDestroyed");
//            return true;
//        }
//
//        @Override
//        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//        }
//    };
//
//    /**
//     * 摄像头创建监听
//     */
//    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
//        @Override
//        public void onOpened(CameraDevice camera) {//打开摄像头
//            mCameraDevice = camera;
//
//
//            if (mCameraDevice == null)
//                return;
//            SurfaceTexture mSurfaceTexture = mTextureView.getSurfaceTexture();
//            mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(),mPreviewSize.getHeight());
//
//            Surface previewSurface = new Surface(mSurfaceTexture);
//            try {
//
//                mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//                mCaptureRequestBuilder.addTarget(previewSurface);
//                //设置实时帧数据接收
//                mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
//                mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
//                    @Override
//                    public void onConfigured(CameraCaptureSession session) {
//
//                        try{
//                            mCameraCaptureSession = session;
//                            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                            //开始预览
//                            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, childHandler);
//                        } catch (CameraAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onConfigureFailed(CameraCaptureSession session) {
//
//                    }
//                },childHandler);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        @Override
//        public void onDisconnected(CameraDevice camera) {//关闭摄像头
//            if (null != mCameraDevice) {
//                mCameraDevice.close();
//                mCameraDevice = null;
//            }
//        }
//
//        @Override
//        public void onError(CameraDevice camera, int error) {//发生错误
//            if (null != mCameraDevice) {
//                mCameraDevice.close();
//                mCameraDevice = null;
//            }
//            Log.e(Tag, "打开摄像头失败");
//        }
//    };
//
//    /**
//     * 收到实时帧数据进行处理：
//     * 一定要使用Image image = reader.acquireLatestImage();image.close();要不然会很卡顿
//     * 由于ImageReader不兼容NV21，所以将YV12转换为NV21
//     */
//    private ImageReader.OnImageAvailableListener mOnImageAvailableListener
//            = new ImageReader.OnImageAvailableListener() {
//
//        @Override
//        public void onImageAvailable(ImageReader reader) {
//            Image image = reader.acquireLatestImage();
//            if (!trackAndSearchThread.isHandleImg) {
//                imgFormat = ImageFormat.NV21;
//                imgHeight = image.getHeight();
//                ingWidth = image.getWidth();
//                imgData = ImageUtil.getBytesFromImageAsType(image, ImageUtil.NV21);
//                if (mHandler != null)
//                    mHandler.sendEmptyMessage(CAPTURE_IMG_PREPARED);
//            }
//            image.close();
//
//        }
//    };
//
//
//    Handler.Callback mHandlerCallback = new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            // Log.e(Tag,"handleMessage");
//
//            //准备图片
//            if (msg.what == TRY_TO_CAPTURE_IMG) {
//
//
//            }
//
//            if (msg.what == CAPTURE_IMG_PREPARED) {
//                // mFaceDrawerView.drawFaces(facelist, mFaceDrawerView.getWidth(), 720, mContext.getResources().getColor(R.color.white));
//
//                //
//                synchronized (imgData) {
//                    //uiHandler.sendEmptyMessage(CAPTURE_IMG_PREPARED);
//                }
//
//                //mTrackThread.handleImgData(imgData, ingWidth, imgHeight, imgFormat);
//                trackAndSearchThread.handleImgData(imgData, ingWidth, imgHeight, imgFormat);
//            }
//
//            return false;
//        }
//    };
//
//
//    /**
//     * 解决预览变形问题
//     *
//     * @param sizeMap
//     * @param width
//     * @param height
//     * @return
//     */
//    //选择sizeMap中大于并且最接近width和height的size
//    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
//        List<Size> sizeList = new ArrayList<>();
//        for (Size option : sizeMap) {
//            if (width > height) {
//                if (option.getWidth() > width && option.getHeight() > height) {
//                    sizeList.add(option);
//                }
//            } else {
//                if (option.getWidth() > height && option.getHeight() > width) {
//                    sizeList.add(option);
//                }
//            }
//        }
//        if (sizeList.size() > 0) {
//            return Collections.min(sizeList, new Comparator<Size>() {
//                @Override
//                public int compare(Size lhs, Size rhs) {
//                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
//                }
//            });
//        }
//        return sizeMap[0];
//    }
//
//    private Size getOptimalPreviewSize(Size []sizes, int w, int h) {
//        final double ASPECT_TOLERANCE = 0.1;
//        double targetRatio = (double) w / h;
//        if (sizes == null) return null;
//
//        Size optimalSize = null;
//        double minDiff = Double.MAX_VALUE;
//
//        int targetHeight = h;
//
//        // Try to find an size match aspect ratio and size
//        Size size = null;
//        for (int i = 0; i < sizes.length; i++) {
//            size = sizes[i];
//            double ratio = (double) size.getWidth() / size.getHeight();
//            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
//            if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
//                optimalSize = size;
//                minDiff = Math.abs(size.getHeight() - targetHeight);
//            }
//        }
//
//        // Cannot find the one match the aspect ratio, ignore the requirement
//        if (optimalSize == null) {
//            minDiff = Double.MAX_VALUE;
//            for (int i = 0; i < sizes.length; i++) {
//                size = sizes[i];
//                if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
//                    optimalSize = size;
//                    minDiff = Math.abs(size.getHeight() - targetHeight);
//                }
//            }
//        }
//        return optimalSize;
//    }
//
//
//    public void updateList() {
//        if (trackAndSearchThread != null)
//            trackAndSearchThread.isUpdateFuture = true;
//    }
//
//    private void configureTransform(int viewWidth, int viewHeight) {
//
//        if (null == mTextureView || null == mPreviewSize || null == activity) {
//            return;
//        }
//        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        Matrix matrix = new Matrix();
//        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
//        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
//        float centerX = viewRect.centerX();
//        float centerY = viewRect.centerY();
//        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
//            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
//            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
//            float scale1 = Math.max(
//                    (float) viewHeight / mPreviewSize.getHeight(),
//                    (float) viewWidth / mPreviewSize.getWidth());
//            float scale2 = Math.min(
//                    (float)mPreviewSize.getHeight() / viewHeight,
//                    (float)  mPreviewSize.getWidth() / viewWidth);
//            matrix.postScale(scale1, scale2, centerX, centerY);
//            matrix.postRotate(90 * rotation, centerX, centerY);
//        }
//        mTextureView.setTransform(matrix);
//    }
}
