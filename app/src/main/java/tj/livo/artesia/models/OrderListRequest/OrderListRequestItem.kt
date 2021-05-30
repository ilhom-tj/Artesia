package tj.livo.artesia.models.OrderListRequest

data class OrderListRequestItem(
    val order_id: Int,
    val product_id: Int,
    val quantity: Int
)