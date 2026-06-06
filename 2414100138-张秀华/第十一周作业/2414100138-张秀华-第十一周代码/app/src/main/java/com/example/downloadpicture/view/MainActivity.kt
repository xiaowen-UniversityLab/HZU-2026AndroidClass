package com.example.downloadpicture.view

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.downloadpicture.R
import com.example.downloadpicture.viewmodel.DownloadViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var vm: DownloadViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var tvText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progress_bar)
        tvText = findViewById(R.id.tv_text)
        progressBar.progressDrawable.colorFilter = android.graphics.PorterDuffColorFilter(
            android.graphics.Color.parseColor("#2196F3"),
            PorterDuff.Mode.SRC_IN
        )
        vm = ViewModelProvider(this)[DownloadViewModel::class.java]

        //下载按钮
        findViewById<Button>(R.id.btn_start).setOnClickListener {
            vm.startDownload(this@MainActivity)
        }

        //下载进度
        vm.progressLiveData.observe(this) { progress ->
            tvText.text = "当前下载进度：$progress %"
            progressBar.progress = progress
        }

        //错误提示
        vm.errorLiveData.observe(this){errMsg->
            tvText.append("\n$errMsg")
        }
    }
}