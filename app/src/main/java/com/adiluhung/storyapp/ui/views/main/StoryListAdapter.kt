package com.adiluhung.storyapp.ui.views.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adiluhung.storyapp.data.remote.responses.StoryItem
import com.adiluhung.storyapp.databinding.ItemStoryBinding
import com.adiluhung.storyapp.ui.views.detailStory.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryListAdapter :
    PagingDataAdapter<StoryItem, StoryListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class ViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryItem) {
            binding.tvName.text = story.name
            binding.tvDescription.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivStory)
            itemView.setOnClickListener { view ->
                val intent = Intent(view.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, story.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(
                oldItem: StoryItem,
                newItem: StoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryItem,
                newItem: StoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}