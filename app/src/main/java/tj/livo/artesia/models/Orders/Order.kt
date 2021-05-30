package tj.livo.artesia.models

import androidx.room.*
import tj.livo.artesia.Utils

@Entity(tableName = "orders_table1")
data class Order(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "auto_id")
    val auto_id: Int,

    @ColumnInfo(name = "isActive")
    var isActive: Boolean = false,

    @ColumnInfo(name = "in_order")
    var inOrder: Boolean = false,

    @ColumnInfo(name = "inProcces")
    var inProcces : Boolean = false,

    @ColumnInfo(name = "isCompleated")
    var isCompleated : Boolean = false,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "city_id")
    val city_id: Int,

    @TypeConverters(Utils.OrderConverter::class)
    val client: Client,

    @ColumnInfo(name = "client_id")
    val client_id: Int,

    @ColumnInfo(name = "comment")
    val comment: String? = "",

    @ColumnInfo(name = "created_at")
    val created_at: String,

    @ColumnInfo(name = "delivered_at")
    val delivered_at: String?,

    @ColumnInfo(name = "delivery_price")
    val delivery_price: String,

    @ColumnInfo(name = "delivery_time")
    val delivery_time: String,

    @ColumnInfo(name = "discount")
    val discount: String,

    @ColumnInfo(name = "house")
    val house: String,

    @ColumnInfo(name = "in_app")
    val in_app: Int,

    @ColumnInfo(name = "markup")
    val markup: String,

    @ColumnInfo(name = "order_status_id")
    var order_status_id: Int,

    @ColumnInfo(name = "owner_id")
    val owner_id: Int,

    @ColumnInfo(name = "payment_id")
    val payment_id: String?,

    @TypeConverters(Utils.OrderConverter::class)
    var products: List<Product>,

    @ColumnInfo(name = "street")
    val street: String,

    @ColumnInfo(name = "street_id")
    val street_id: Int?,

    @ColumnInfo(name = "total")
    val total: String,

    @ColumnInfo(name = "type")
    val type: Int,

    @ColumnInfo(name = "updated_at")
    val updated_at: String
)