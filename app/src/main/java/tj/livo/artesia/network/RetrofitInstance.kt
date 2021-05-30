package tj.livo.artesia.network

import android.app.Application
import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import tj.livo.artesia.Const
import tj.livo.artesia.SessionManager

class RetrofitInstance(context: Context){
    private val client = OkHttpClient.Builder()
        .addInterceptor{ chain ->
            val original = chain.request()
            val requestBuilderForToken = original.newBuilder()
                .addHeader("Authorization","Bearer "+SessionManager(context).getToken())
                .addHeader("Accept","application/json")
                .method(original.method(),original.body())
            val requestToken = requestBuilderForToken.build()
            chain.proceed(requestToken)
        }.build()

    private fun retrofitInstance(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(Const.base_url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
    fun api() : Api {
        return retrofitInstance().create(Api::class.java)
    }
}