package com.example.base.utils

import android.app.Activity
import android.view.WindowManager

fun Activity.setupSystemWindowInset() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}
