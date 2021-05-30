package tj.livo.artesia.models.ApiResponse

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message") val message : String,
    @SerializedName("fullname") val fullname : String,
    @SerializedName("api_token") val api_token : String,
    @SerializedName("auto_id") val auto_id : String
)