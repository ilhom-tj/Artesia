package tj.livo.artesia.ui.fragments.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import tj.livo.artesia.models.ApiResponse.LoginResponse
import tj.livo.artesia.network.Repository.LoginRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val loginRepository = LoginRepository(application.applicationContext);

    fun login(phone: String,password : String) : LiveData<LoginResponse>{
        return loginRepository.login(phone,password)
    }
    fun logout():LiveData<Boolean>{
        return loginRepository.logout()
    }
}