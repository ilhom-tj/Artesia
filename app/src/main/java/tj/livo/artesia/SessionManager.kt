package tj.livo.artesia

import android.app.Application

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("CommitPrefEdits")
class SessionManager(context: Context) {

    private var settings : SharedPreferences = context.getSharedPreferences("AppSettings",0)
    private var editor : SharedPreferences.Editor = settings.edit()

    //GET AND SET TOKEN
    fun getToken() : String? {
        return settings.getString("APP_TOKEN","error")
    }
    fun setToken(token : String) : Boolean{
        return editor.putString("APP_TOKEN",token).commit()
    }



    fun setAutoId(auto_id : String) : Boolean{
        return editor.putString("auto_id",auto_id).commit()
    }
    fun getAutoId() : String? {
        return settings.getString("auto_id","error")
    }

    fun setFullName(name : String) : Boolean{
        return editor.putString("fullName",name).commit()
    }
    fun getFullName() : String? {
        return settings.getString("fullName","error")
    }

    fun deleteAll() {
        editor.remove("APP_TOKEN").commit()
        editor.remove("phone").commit()
        editor.remove("password").commit()
    }



}