package com.example.base.ui.onboard.language

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.base.databinding.ItemLanguageBinding
import com.example.base.model.Language

class LanguageAdapter(
    var listLanguage: MutableList<Language>,
    var onClickItem: (Int) -> Unit,
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = listLanguage.size
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemBinding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val item = listLanguage[position]
        binding.tvEnglish.text = item.language
        Glide.with(binding.ivImage.context).load(item.flags).into(binding.ivImage)
        binding.rb.isChecked = item.isSelected
        binding.rootView.setOnClickListener {
            onClickItem.invoke(position)
        }
    }
}
