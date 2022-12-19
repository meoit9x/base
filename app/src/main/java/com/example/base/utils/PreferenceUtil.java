package com.example.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    public static final String SETTING_ENGLISH = "SETTING_ENGLISH";
    private static final String MyPREFERENCES = "MyPreferences";
    public static final String OPEN_APP_FIRST_TIME = "open_app_first_time";
    public static final String GO_BACK_COUNT = "back_from_detail_view_count";
    public static final String DISPLAY_ADS_AFTER_ONBOARD = "display_ads_after_onboard";

    public static void saveBoolean(Context context, String str, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(MyPREFERENCES, 0).edit();
        edit.putBoolean(str, z);
        edit.apply();
    }

    public static boolean getBoolean(Context context, String str, boolean z) {
        return context.getSharedPreferences(MyPREFERENCES, 0).getBoolean(str, z);
    }

    public static void saveString(Context context, String str, String str2) {
        SharedPreferences.Editor edit = context.getSharedPreferences(MyPREFERENCES, 0).edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public static String getString(Context context, String str, String str2) {
        return context.getSharedPreferences(MyPREFERENCES, 0).getString(str, str2);
    }

    public static void saveInt(Context context, String key, Integer value) {
        SharedPreferences.Editor edit = context.getSharedPreferences(MyPREFERENCES, 0).edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static Integer getInt(Context context, String key) {
        return context.getSharedPreferences(MyPREFERENCES, 0).getInt(key, 0);
    }
}
