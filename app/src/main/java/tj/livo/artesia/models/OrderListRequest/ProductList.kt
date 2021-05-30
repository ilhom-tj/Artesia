package tj.livo.artesia.models.OrderListRequest

import com.google.gson.annotations.SerializedName

data class ProductList (
    @SerializedName("order_id") val order_id : Int,
    @SerializedName("product_id") val product_id : Int,
    @SerializedName("quantity") val quantity : Int
)