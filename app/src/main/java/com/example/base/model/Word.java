package com.example.base.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "word")
public class Word implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "content")
    @SerializedName("content")
    private String content;

    private int favorite = 0;

    private int history = 0;

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int isFavorite) {
        this.favorite = isFavorite;
    }

    public Word(String content) {
        this.content = content;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
