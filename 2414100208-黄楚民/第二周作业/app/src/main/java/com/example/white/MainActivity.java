package com.example.white;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.UrlRequest;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ImageView Photo;
    private TextView Status;
    private BatteryReceiver batteryReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Photo = findViewById(R.id.photo);
        Status = findViewById(R.id.status);
        requestStoragePermissions();
    }

    private void requestStoragePermissions() {
        String permissions;
        permissions = Manifest.permission.READ_MEDIA_IMAGES;
        if (ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permissions}, 1);
        }
    }
    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                // 获取当前电量
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                Status.setText("当前电量: " + level + "%");
                Toast.makeText(context, "电量发生变化！触发读取照片", Toast.LENGTH_SHORT).show();

                // 去读取最近一张照片
                loadLatestPhoto();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        BatteryReceiver batteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter); // 注册广播
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver); // 注销广播
        }
    }
    private void loadLatestPhoto() {
        // 定义URI
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // 照片ID
        String[] projection = new String[]{MediaStore.Images.Media._ID};

        // 排序方式
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        // 按时间倒序
        Cursor cursor = getContentResolver().query(collection, projection, null, null, sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            // 找到了最近的一张照片
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            long id = cursor.getLong(idColumn);

            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            
            Photo.setImageURI(contentUri);

            cursor.close();
        } else {
            Toast.makeText(this, "相册里好像没有照片",Toast.LENGTH_LONG).show();
        }
    }
}

