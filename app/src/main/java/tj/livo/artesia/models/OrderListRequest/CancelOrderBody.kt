package tj.livo.artesia.models.OrderListRequest

import retrofit2.http.Query
import tj.livo.artesia.models.Product

data class FinishOrderBody(
    val api_token: String,
    val orderId : Int,
    val bottleTaken : Int,
    val bottleLoaned : Int,
    val total : Double,
    val totalTaken : Double,
    val orderStatus : Int,
    val comment : String,
    val products : List<Product>?=null

)