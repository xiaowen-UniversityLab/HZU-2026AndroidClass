package com.example.music_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSION = 1001
    private lateinit var bt_startMusic: Button
    private lateinit var bt_stopMusic: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bt_startMusic = findViewById(R.id.bt_startMusic)
        bt_stopMusic = findViewById(R.id.bt_stopMusic)

        // 播放音乐
        bt_startMusic.setOnClickListener {
            val intent = Intent(this@MainActivity, MusicService::class.java).apply {
                action = "START"
                putExtra("content", "我的音乐正在后台播放")
            }
            startService(intent)
            Toast.makeText(this@MainActivity, "开始播放音乐啦~", Toast.LENGTH_SHORT).show()
        }

        // 停止音乐
        bt_stopMusic.setOnClickListener {
            val intent = Intent(this@MainActivity, MusicService::class.java).apply {
                action = "STOP"
            }
            stopService(intent)
            Toast.makeText(this@MainActivity, "音乐暂停啦~", Toast.LENGTH_SHORT).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
    }

    // 检查通知权限
    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "请在设置中开启通知权限，否则服务无法运行", Toast.LENGTH_LONG).show()
            }
        }
    }
}