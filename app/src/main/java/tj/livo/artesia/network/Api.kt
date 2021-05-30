package tj.livo.artesia.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import tj.livo.artesia.models.ApiResponse.LoginResponse
import tj.livo.artesia.models.OrderListRequest.FinishOrderBody
import tj.livo.artesia.models.OrderListRequest.OrderListRequest
import tj.livo.artesia.models.OrderListRequest.OrderListRequestItem

interface Api {

    //LOGIN
    @POST("logout")
    fun logout(
        @Query("api_token") api_token : String
    ) : Call<LoginResponse>

    @POST("login")
    fun login(
        @Query("phone") phone : String,
        @Query("password") password : String
    ) : Call<LoginResponse>
    //!-> Login

    //Orders
    @POST("orders")
    fun orders(
        @Query(  "api_token")  token : String,
        @Query("auto_id") auto_id : String,
        @Query("date") date : String
    ) : Call<tj.livo.artesia.models.Orders>

    @POST("orders/delivered")
    fun cancelOrder(
        @Query("api_token") api_token: String,
        @Query("id")  orderId : Int,
        @Query("bottle_taken")  bottleTaken : Int,
        @Query("bottles_loaned")  bottleLoaned : Int,
        @Query("total") total : Double,
        @Query("total_taken") totalTaken : Double,
        @Query("order_status_id") orderStatus : Int,
        @Query("comment") comment : String,
        @Query("products") products : String = "{}"
    ): Call<ResponseBody>

    @POST("orders/delivered")
    fun finishOrder(
        @Query("api_token") api_token: String,
        @Query("id")  orderId : Int,
        @Query("bottle_taken")  bottleTaken : Int,
        @Query("bottles_loaned")  bottleLoaned : Int,
        @Query("total") total : Double,
        @Query("total_taken") totalTaken : Double,
        @Query("order_status_id") orderStatus : Int,
        @Query("comment") comment : String,
        @Query("products") products : String = "{}"
    ): Call<ResponseBody>
}