package com.example.downloadpicture.model

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.example.downloadpicture.network.DownloadRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

class ImageRepository {

    private val MyRetrofit: DownloadRetrofit = ProvideRetrofit.downloadApi

    //图片地址
    val imageUrlList = listOf(
        "https://picsum.photos/id/12/600/600",
        "https://picsum.photos/id/10/600/600",
        "https://picsum.photos/id/11/600/600"
    )

    suspend fun downloadSinglePic(url: String, context: Context): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val body: ResponseBody = MyRetrofit.downloadFile(url).body() ?: return@withContext null
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DownloadImg")
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return@withContext null

            val inputStream = body.byteStream()
            val outputStream = resolver.openOutputStream(uri)!!
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            uri.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}