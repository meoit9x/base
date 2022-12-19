package com.example.base.data

import android.content.Context
import android.content.SharedPreferences
import com.example.base.App
import com.example.base.model.Language
import com.google.gson.GsonBuilder
import java.util.*

object AppPreferences {
    var preferences: SharedPreferences
    private var editor: SharedPreferences.Editor


    private const val REFERENCES_NAME = "AppPreferences"
    private const val KEY_SHOWN_ON_BOARD = "key_shown_on_board"
    private const val KEY_CURRENT_LANGUAGE = "key_current_language"

    init{
        preferences = App.getInstance().getSharedPreferences(REFERENCES_NAME, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }
    /**
     * set shown onboard
     */
    fun setShowOnBoard(isShown: Boolean){
        editor.also {
            it.putBoolean(KEY_SHOWN_ON_BOARD, isShown)
            it.commit()
        }
    }

    /**
     * is user already view onboard screen or not
     */
    fun isShownOnBoard():Boolean{
        return preferences.getBoolean(KEY_SHOWN_ON_BOARD,true)
    }

    fun saveCurrentLanguage(language: Language){
        putObject(language, KEY_CURRENT_LANGUAGE)
    }

    fun getCurrentLanguage(): Language? {
        return getObject<Language>(KEY_CURRENT_LANGUAGE)
    }


    /**
     * Saves object into the Preferences.
     *
     * @param `object` Object of model class (of type [T]) to save
     * @param key Key with which Shared preferences to
     **/
    fun putObject(`object`: Any?, key: String) {
        //Convert object to JSON String.
        try {
            val jsonString = GsonBuilder().create().toJson(`object`)
            //Save that String in SharedPreferences
            preferences.edit().putString(key, jsonString).apply()
        }catch (e:Throwable){}
    }

    fun clearPreference(key:String){
        preferences.edit().remove(key).apply()
    }

    /**
     * Used to retrieve object from the Preferences.
     *
     * @param key Shared Preference key with which object was saved.
     **/
    inline fun <reified T> getObject(key: String): T? {
        //We read JSON String which was saved.
        val value = preferences.getString(key, null)
        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of
        //type Class < T >" is used to cast.
        return try {
            GsonBuilder().create().fromJson(value, T::class.java)
        }catch (e:Throwable){
            null
        }
    }

}