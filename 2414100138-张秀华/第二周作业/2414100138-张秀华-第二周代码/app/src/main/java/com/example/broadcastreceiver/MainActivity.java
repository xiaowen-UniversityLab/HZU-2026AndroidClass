package com.example.broadcastreceiver;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private TextView tvBattery;
    private static final int REQUEST_PERMISSION = 100;
    private MyBroadcastReceiver batteryReceiver = new MyBroadcastReceiver();
    private ImageView ivpicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvBattery = findViewById(R.id.tv_battery);
        ivpicture = findViewById(R.id.iv_picture);

        batteryReceiver.setTextView(tvBattery);

        //动态注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            registerReceiver(batteryReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(batteryReceiver, intentFilter);
        }

        checkPermission();
    }

    // 检查图片权限
    private void checkPermission() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = android.Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSION);
        } else {
            getLatestPhoto();
        }
    }

    // 权限申请结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限授予成功，加载照片
                getLatestPhoto();
            } else {
                // 权限拒绝，提示用户
                Toast.makeText(this, "权限被拒绝，无法显示照片", Toast.LENGTH_SHORT).show();
                tvBattery.setText("当前电量：获取中...");
            }
        }
    }

    //获取照片
    private void getLatestPhoto() {
        // 子线程执行查询，避免主线程阻塞导致黑屏
        new Thread(() -> {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media._ID};
            Cursor cursor = null;
            cursor = getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");

            //相册为空时，显示占位图片
            if (cursor == null || !cursor.moveToFirst()) {
                runOnUiThread(() -> {
                    ivpicture.setImageResource(R.drawable.ic_launcher_background);
                    Toast.makeText(MainActivity.this, "相册为空，无照片可显示", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            //相册不为空时，正常获取照片
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            Uri finalContentUri = contentUri;
            runOnUiThread(() -> ivpicture.setImageURI(finalContentUri));


        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }


}