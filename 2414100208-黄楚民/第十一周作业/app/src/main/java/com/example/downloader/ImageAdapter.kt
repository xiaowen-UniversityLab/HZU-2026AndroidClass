package com.example.downloader

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.downloader.databinding.ItemImageBinding
import java.io.File

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val files = mutableListOf<File>()

    fun addImage(file: File) {
        files.add(file)
        notifyItemInserted(files.size - 1)
    }

    fun clear() {
        files.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        holder.binding.tvFilename.text = file.name
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        holder.binding.ivThumbnail.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int = files.size

    class ViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)
}
