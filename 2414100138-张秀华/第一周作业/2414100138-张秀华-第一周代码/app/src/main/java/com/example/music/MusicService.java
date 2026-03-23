package com.example.music;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MusicService extends Service {
    private static final String CHANNERLID = "startMusic";
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private static final String ACTION_START = "START";
    private static final String ACTION_STOP = "STOP";

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
    }

    //初始化MediaPlayer
    private void initMediaPlayer() {
        try {
            AssetFileDescriptor fd = getAssets().openFd("music.mp3");
            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //管理音乐播放
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_START:
                    if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                    }
                    break;
                case ACTION_STOP:
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        initMediaPlayer();
                    }
                    stopForeground(STOP_FOREGROUND_REMOVE);
                    stopSelf();
                    break;
            }
        }

        //创建通知+前台服务
        String content = intent.getStringExtra("content");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNERLID, "后台播放音乐", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Intent intentNotification = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNERLID)
                .setContentTitle("音乐播放器")
                .setContentText(content)
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContentIntent(pi)
                .build();

        manager.notify(1, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
