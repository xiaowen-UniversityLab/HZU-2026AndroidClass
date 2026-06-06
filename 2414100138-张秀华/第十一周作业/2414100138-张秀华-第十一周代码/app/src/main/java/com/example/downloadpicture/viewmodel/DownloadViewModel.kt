package com.example.downloadpicture.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.downloadpicture.model.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadViewModel : ViewModel() {

    private val repo = ImageRepository()
    val progressLiveData = MutableLiveData<Int>()
    val errorLiveData = MutableLiveData<String>()

    fun startDownload(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val total = repo.imageUrlList.size
            var finishCount = 0
            try {
                for (url in repo.imageUrlList) {
                    val path = repo.downloadSinglePic(url, context)
                    path?.let {
                        finishCount++
                        val progress = (finishCount * 100f / total).toInt()
                        progressLiveData.postValue(progress)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "下载成功！图片保存在：$it", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        errorLiveData.postValue("$url 下载失败")
                    }
                }
            } catch (e: Exception) {
                errorLiveData.postValue("异常：${e.message}")
            }
        }
    }
}