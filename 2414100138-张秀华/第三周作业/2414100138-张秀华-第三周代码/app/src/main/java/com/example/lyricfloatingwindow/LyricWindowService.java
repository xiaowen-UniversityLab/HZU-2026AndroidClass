package com.example.lyricfloatingwindow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class LyricWindowService extends Service {

    private LyricWindow mLyricWindow;
    private static final int NOTIFICATION_ID = 111;
    private static final String CHANNEL_ID = "LYRIC_SERVICE_CHANNEL";

    @Override
    public void onCreate() {
        super.onCreate();

        // 创建通知渠道
        createNotificationChannel();

        //启动前台Service
        startForeService();

        //初始化悬浮窗
        initFloatingWindow();

    }


    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "悬浮窗服务渠道", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("悬浮窗后台运行");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }


    private void startForeService() {

        Intent intentNotification = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("歌词悬浮窗")
                .setContentText("点击返回应用")
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContentIntent(pi)
                .build();

        startForeground(NOTIFICATION_ID, notification);

    }


    private void initFloatingWindow() {
        mLyricWindow = LyricWindow.getInstance(this);
        mLyricWindow.showFloatWindow();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLyricWindow != null) {
            mLyricWindow.remove();
        }
        Toast.makeText(this, "悬浮窗服务已关闭", Toast.LENGTH_SHORT).show();
    }

}