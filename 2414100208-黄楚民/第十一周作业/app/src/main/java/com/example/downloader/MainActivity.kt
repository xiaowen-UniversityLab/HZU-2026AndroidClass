package com.example.downloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.downloader.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: DownloadViewModel
    private lateinit var adapter: ImageAdapter
    private val addedFiles = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DownloadViewModel::class.java]

        // 初始化 RecyclerView
        adapter = ImageAdapter()
        binding.rvImages.layoutManager = LinearLayoutManager(this)
        binding.rvImages.adapter = adapter

        // 观察下载状态
        viewModel.state.observe(this) { state ->
            binding.tvStatus.text = state.status
            binding.btnStart.isEnabled = !state.isDownloading
            binding.btnCancel.isEnabled = state.isDownloading

            if (state.total > 0) {
                val percent = (state.progress * 100) / state.total
                binding.progressBar.progress = percent
                binding.tvProgress.text = "${state.progress}/${state.total} ($percent%)"
            }

            // 新图片下载完成时添加到列表（避免重复）
            state.completedFile?.let { file ->
                if (file.exists() && addedFiles.add(file.absolutePath)) {
                    adapter.addImage(file)
                }
            }
        }

        // 开始下载
        binding.btnStart.setOnClickListener {
            adapter.clear()
            addedFiles.clear()
            viewModel.startDownload()
        }

        // 取消下载
        binding.btnCancel.setOnClickListener {
            viewModel.cancelDownload()
        }
    }
}
