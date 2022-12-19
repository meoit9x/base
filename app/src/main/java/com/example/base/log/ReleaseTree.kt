package com.example.base.log

import androidx.annotation.Nullable
import org.jetbrains.annotations.NotNull
import timber.log.Timber


class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, @Nullable tag: String?,
                     @NotNull message: String, @Nullable t: Throwable?) {
        // Do nothing here
    }
}