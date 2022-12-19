package com.example.base.customView.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.base.R;
import com.example.base.customView.ExtTextView;

public class DialogSearchError extends Dialog {
    private boolean isDetailErr;
    private String text;
    private int icon;

    public DialogSearchError(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public DialogSearchError(Context context, int materialDialogSheet, boolean isDetailErr, String text, int ic) {
        super(context, materialDialogSheet);
        this.isDetailErr = isDetailErr;
        this.text = text;
        this.icon = ic;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.dialog_search_error);
        setCanceledOnTouchOutside(true);
        findViewById(R.id.ext_ok).setOnClickListener(v -> dismiss());
        if (isDetailErr) {
            ExtTextView ext = findViewById(R.id.ext_text_view);
            ImageView iv = findViewById(R.id.iv_error);
            if (ext != null && iv != null) {
                ext.setText(text);
                ext.setTextColor(getContext().getResources().getColor(R.color.color_404040));
                iv.setImageResource(icon);
            }
        }
    }
//
//    public void setTextAndIcon(int ic_detail_err, String string) {
//
//    }
}
