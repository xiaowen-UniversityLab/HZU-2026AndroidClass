package com.example.musickt

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)

        // 启动音乐服务
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startMusicService()
        }

        // 停止音乐服务
        findViewById<Button>(R.id.btnStop).setOnClickListener {
            val intent = Intent(this, MusicService::class.java)
            stopService(intent)
            tvStatus.text = "音乐服务已停止"
            Toast.makeText(this, "音乐服务已停止", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startMusicService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
            tvStatus.text = "请先允许通知权限"
            Toast.makeText(this, "请允许通知权限后再启动音乐服务", Toast.LENGTH_SHORT).show()
            return
        }

        ContextCompat.startForegroundService(this, Intent(this, MusicService::class.java))
        tvStatus.text = "音乐服务运行中，通知栏已显示"
        Toast.makeText(this, "音乐服务已启动", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                startMusicService()
            } else {
                tvStatus.text = "通知权限被拒绝，无法显示播放通知"
                Toast.makeText(this, "通知权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1
    }
}
