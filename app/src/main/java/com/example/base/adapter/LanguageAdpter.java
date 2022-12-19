package com.example.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.base.databinding.ItemLanguageBinding;
import com.example.base.utils.PreferenceUtil;

import java.util.ArrayList;

public class LanguageAdpter extends RecyclerView.Adapter<LanguageAdpter.ListWordViewHolder> {
    private Context context;
    private ArrayList<Language> listLanguage;
    private Consumer<Language> consumer;

    public LanguageAdpter(Context context, ArrayList<Language> listLanguage, Consumer<Language> consumer) {
        this.context = context;
        this.listLanguage = listLanguage;
        this.consumer = consumer;
    }

    @NonNull
    @Override
    public ListWordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLanguageBinding itemBinding =
                ItemLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LanguageAdpter.ListWordViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListWordViewHolder holder, int position) {
        ItemLanguageBinding binding = holder.binding;
        Language item = listLanguage.get(position);
        if (item.language == null)
            return;

        binding.tvEnglish.setText(item.language);
        Glide.with(context).load(item.flags).into(binding.ivImage);
        String english = PreferenceUtil.getString(context, PreferenceUtil.SETTING_ENGLISH, "");

        binding.rb.setChecked(item.values.equals(english));
        binding.rootView.setOnClickListener(v -> {
            consumer.accept(item);
        });
    }

    @Override
    public int getItemCount() {
        return listLanguage == null ? 0 : listLanguage.size();
    }

    public class ListWordViewHolder extends RecyclerView.ViewHolder {
        private final ItemLanguageBinding binding;

        public ListWordViewHolder(@NonNull ItemLanguageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class Language {
        private String language;
        private String values;
        private int flags;

        public Language(String language, String values, int flags) {
            this.language = language;
            this.values = values;
            this.flags = flags;
        }

        public String getLanguage() {
            return language;
        }

        public String getValues() {
            return values;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
