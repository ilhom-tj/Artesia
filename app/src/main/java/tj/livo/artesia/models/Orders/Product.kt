package tj.livo.artesia.models

import androidx.room.Embedded

data class Product(
    val active: Int,
    val cost: String,
    val created_at: String,
    val id: Int,
    val name: String,
    @Embedded val pivot: Pivot,
    val price: String,
    var quantity: Double,
    val unit_id: Int,
    val updated_at: String
)