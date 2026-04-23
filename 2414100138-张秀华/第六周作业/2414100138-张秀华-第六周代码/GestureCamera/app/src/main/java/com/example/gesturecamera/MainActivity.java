package com.example.gesturecamera;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private PreviewView viewFinder;

    private ImageCapture imageCapture = null;
    private SensorManager sensorManager;
    private Sensor mSensor;


    //上一次传感器的值
    private float lastX = 0, lastY = 0, lastZ = 0;

    // 摇一摇灵敏度
    private static final float SHAKE_THRESHOLD = 18.0f;

    // 水平灵敏度
    private static final float LEVEL_THRESHOLD = 1.2f;
    private static final float GRAVITY = 9.81f;

    //防止连续疯狂拍照
    private boolean canTakePhoto = true;
    private static final long PHOTO_COOLDOWN = 1500;


    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    //权限列表
    private static final String[] REQUIRED_PERMISSIONS;
    static {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            REQUIRED_PERMISSIONS = new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                    android.Manifest.permission.CAMERA
            };
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFinder = findViewById(R.id.viewFinder);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        //权限检查与申请
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

    }


    //权限处理
    private boolean allPermissionsGranted() {
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 权限申请结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (allPermissionsGranted()) startCamera();
        else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());//用preview和预览控件绑定，显示画面

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                provider.unbindAll();
                provider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                Log.e(TAG, "Camera failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }


    //拍照功能
    private void takePhoto() {
        ImageCapture imageCapture = this.imageCapture;
        if (imageCapture == null) return;

        canTakePhoto = false;

        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions
                .Builder(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                .build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {

            @Override
            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                String msg = "拍照成功！照片保存在" + outputFileResults.getSavedUri();
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exc) {
                Toast.makeText(MainActivity.this, "拍照失败！", Toast.LENGTH_SHORT).show();

            }
        });

        //停止时间，防止疯狂拍照，1.5秒后canTakePhoto = true才可以拍照
        new Thread(() -> {
            try {
                Thread.sleep(PHOTO_COOLDOWN);
                canTakePhoto = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (!canTakePhoto)  return;

        if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        // 计算变化量
        float dx = Math.abs(x - lastX);
        float dy = Math.abs(y - lastY);
        float dz = Math.abs(z - lastZ);
        float delta = dx + dy + dz;

        //判断是否摇晃拍照
        if (delta > SHAKE_THRESHOLD) {
            takePhoto();
        }

        //重新赋值
        lastX = x;
        lastY = y;
        lastZ = z;


        //判断手机是否水平
        boolean isLevel = Math.abs(x) < LEVEL_THRESHOLD && Math.abs(y) < LEVEL_THRESHOLD && Math.abs(z - GRAVITY) < LEVEL_THRESHOLD;

        //水平拍照
        if (isLevel) {
            takePhoto();
        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensor != null) {
            sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}