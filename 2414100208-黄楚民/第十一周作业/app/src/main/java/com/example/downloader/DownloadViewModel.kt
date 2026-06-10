package com.example.downloader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

data class DownloadState(
    val progress: Int = 0,
    val total: Int = 0,
    val status: String = "等待开始下载...",
    val isDownloading: Boolean = false,
    val completedFile: File? = null
)

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableLiveData(DownloadState())
    val state: LiveData<DownloadState> = _state

    private val client = OkHttpClient()
    private var downloadJob: Job? = null

    private val imageUrls = listOf(
        "https://picsum.photos/400/300?random=1",
        "https://picsum.photos/400/300?random=2",
        "https://picsum.photos/400/300?random=3",
        "https://picsum.photos/400/300?random=4",
        "https://picsum.photos/400/300?random=5"
    )

    fun startDownload() {
        if (downloadJob?.isActive == true) return

        val dir = File(getApplication<Application>().filesDir, "downloaded_images")
        if (!dir.exists()) dir.mkdirs()

        downloadJob = viewModelScope.launch {
            val total = imageUrls.size
            _state.value = DownloadState(
                progress = 0,
                total = total,
                status = "开始下载 $total 张图片...",
                isDownloading = true
            )

            for ((index, url) in imageUrls.withIndex()) {
                val file = File(dir, "image_${index + 1}.jpg")

                _state.value = _state.value?.copy(
                    status = "正在下载第 ${index + 1}/$total 张..."
                )

                val success = withContext(Dispatchers.IO) {
                    downloadImage(url, file)
                }

                if (success) {
                    _state.value = _state.value?.copy(
                        progress = index + 1,
                        completedFile = file
                    )
                } else {
                    _state.value = _state.value?.copy(
                        status = "第 ${index + 1} 张下载失败，继续下一张..."
                    )
                }
            }

            _state.value = _state.value?.copy(
                status = "全部下载完成！",
                isDownloading = false
            )
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        _state.value = _state.value?.copy(
            status = "下载已取消",
            isDownloading = false
        )
    }

    private fun downloadImage(url: String, file: File): Boolean {
        return try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.byteStream()?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
