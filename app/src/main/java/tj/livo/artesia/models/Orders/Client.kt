package tj.livo.artesia.models

data class Client(
    val active: Int,
    val api_token: String,
    val balance: String,
    val bottle: Int,
    val comment: Any,
    val created_at: String,
    val discount_id: String,
    val fullname: String,
    val id: Int,
    val loan_bottle: Int,
    val markup: String,
    val phone: String,
    val updated_at: String
)