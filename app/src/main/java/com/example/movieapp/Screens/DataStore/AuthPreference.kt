package com.example.movieapp.Screens.DataStore

import android.content.Context

class AuthPreference(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean("logged_in" , false)
    }

    fun setUserLoggedIn(value: Boolean) {
        return prefs.edit().putBoolean("logged_in" , value).apply()
    }

    fun savedLoggedInEmail(email: String){
        prefs.edit().putString("user_email",email).apply()
    }
    fun getLoggedInEmail():String?{
        return prefs.getString("user_email",null)
    }


}