package com.example.camerax;

import android.Manifest;
import android.content.ContentValues;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private SensorManager sensorManager;
    private boolean isCoolingDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFinder = findViewById(R.id.viewFinder);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
        startCamera();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder().build();
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);
            } catch (Exception ignored) {}
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isCoolingDown || imageCapture == null) return;
        float x = event.values[0], y = event.values[1], z = event.values[2];

        // 逻辑 A：摇一摇判定
        double speed = Math.sqrt(x*x + y*y + z*z) - SensorManager.GRAVITY_EARTH;
        if (speed > 10) {
            triggerCapture("检测到摇一摇");
        }

        // 逻辑 B：完全水平判定
        if (Math.abs(x) < 0.5 && Math.abs(y) < 0.5 && z > 9.4) {
            triggerCapture("检测到手机已放平");
        }
    }

    private void triggerCapture(String reason) {
        isCoolingDown = true;
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
        takePhoto();
        viewFinder.postDelayed(() -> isCoolingDown = false, 3000);
    }

    private void takePhoto() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
        cv.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        cv.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Lab");

        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv).build();

        imageCapture.takePicture(options, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override public void onImageSaved(@androidx.annotation.NonNull ImageCapture.OutputFileResults res) {
                Toast.makeText(MainActivity.this, "拍照成功并已存入相册", Toast.LENGTH_SHORT).show();
            }
            @Override public void onError(@androidx.annotation.NonNull ImageCaptureException e) { isCoolingDown = false; }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override public void onAccuracyChanged(Sensor s, int a) {}
}