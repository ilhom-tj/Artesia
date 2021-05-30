package tj.livo.artesia.ui.fragments.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import tj.livo.artesia.R
import tj.livo.artesia.SessionManager
import tj.livo.artesia.ui.MainActivity
import tj.livo.artesia.databinding.ActivityLoginBinding
import tj.livo.artesia.models.Alerts.Alerts

class LoginActivity : AppCompatActivity() {
    private lateinit var binidng : ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sessionManager = SessionManager(this)
        binidng = DataBindingUtil.setContentView(this,R.layout.activity_login)
        navigateToMain()
        val loginViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(LoginViewModel::class.java)

        binidng.signIn.setOnClickListener {
            val phone = binidng.phone.text.toString()
            val password = binidng.passwd.text.toString()
            loginViewModel.login(phone,password).observe(this,){
                it.let {
                    if (it.api_token != null){
                        sessionManager.setToken(it.api_token)
                        sessionManager.setAutoId(it.auto_id)
                        sessionManager.setFullName(it.fullname)
                        navigateToMain()
                    }else{
                        Alerts(this).customAlert("Ошибка",it.message)
                    }
                }
            }
        }
    }
    fun navigateToMain(){
        if (!sessionManager.getToken().equals("error")){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}