package tj.livo.artesia.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bottle_price")
data class Bottle(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val price: String
)
