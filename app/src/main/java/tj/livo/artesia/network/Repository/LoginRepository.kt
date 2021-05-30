package tj.livo.artesia.network.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tj.livo.artesia.SessionManager
import tj.livo.artesia.models.Alerts.Alerts
import tj.livo.artesia.models.ApiResponse.LoginResponse
import tj.livo.artesia.network.RetrofitInstance

class LoginRepository(private val context: Context) {
    private val api = RetrofitInstance(context).api()

    fun login( phone: String,  password : String) : LiveData<LoginResponse>{
        val liveData = MutableLiveData<LoginResponse>()
        api.login(phone,password).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    liveData.value = response.body()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

            }

        })
        return liveData
    }
    fun logout() : LiveData<Boolean>{
        val liveData = MutableLiveData<Boolean>()
        val sessionManager = SessionManager(context)
        api.logout(sessionManager.getToken().toString()).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                liveData.value = response.isSuccessful
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

            }

        })
        return liveData
    }
}