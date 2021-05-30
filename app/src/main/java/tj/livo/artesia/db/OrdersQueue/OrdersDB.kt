package tj.livo.artesia.db.OrdersQueue

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tj.livo.artesia.Utils
import tj.livo.artesia.models.Order

@Database(entities = [Order::class], version = 5)
@TypeConverters(Utils.OrderConverter::class)
abstract class OrdersDB : RoomDatabase() {

    abstract val ordersDAO: OrdersDAO

    companion object {
        @Volatile
        private var INSTANCE: OrdersDB? = null
        fun getInstance(context: Context): OrdersDB {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        OrdersDB::class.java,
                        "data_database1"
                    ).fallbackToDestructiveMigration().build()
                }
                return instance
            }
        }
    }
}