package com.lelestacia.dicodingstoryapp.ui.detail_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lelestacia.dicodingstoryapp.data.model.Story
import com.lelestacia.dicodingstoryapp.databinding.ActivityDetailBinding
import com.lelestacia.dicodingstoryapp.utility.Utility

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<Story>(Utility.STORY)
        val newTimeStamp = story!!.createdAt
            .removeRange(16 until story.createdAt.length)
            .replace("T", " - ")
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .fitCenter()
                .into(ivPhoto)
            tvTitle.text = story.name
            tvTimestamp.text = newTimeStamp
            tvDeskripsi.text = story.description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}