package com.example.music_kotlin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.io.IOException


class MusicService : Service() {

    // Kotlin 推荐常量名用大写下划线或小驼峰，这里按规范改为小驼峰
    private val channelId = "startMusic"
    private var mMediaPlayer: MediaPlayer? = null
    private val actionStart = "START"
    private val actionStop = "STOP"

    override fun onCreate() {
        super.onCreate()
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        try {
            val fd: AssetFileDescriptor = assets.openFd("music.mp3")
            mMediaPlayer = MediaPlayer().apply {
                setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                prepare()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action != null) {
            when (action) {
                actionStart -> {
                    mMediaPlayer?.let { player ->
                        if (!player.isPlaying) {
                            player.start()
                        }
                    }
                }
                actionStop -> {
                    mMediaPlayer?.let { player ->
                        player.stop()
                        player.release()
                    }
                    mMediaPlayer = null
                    initMediaPlayer()

                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }

        // 通知创建
        val content = intent?.getStringExtra("content") ?: ""
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. 创建通知渠道（API 26+ 必须）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "后台播放音乐",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        // 2. 创建 PendingIntent
        val intentNotification = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            this,
            0,
            intentNotification,
            PendingIntent.FLAG_IMMUTABLE
        )

        // 3. 构建通知
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("音乐播放器")
            .setContentText(content)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentIntent(pi)
            .build()

        // 4. 发送通知
        manager.notify(1, notification)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }
}