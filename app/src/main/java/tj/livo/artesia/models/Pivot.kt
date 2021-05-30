package tj.livo.artesia.models

data class Pivot(
    val order_id: Int,
    val price: String,
    val product_id: Int,
    var quantity: Int
)