package tj.livo.artesia.db.OrdersQueue

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.w3c.dom.ls.LSException
import tj.livo.artesia.models.Order

@Dao
interface OrdersDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrders(order : Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrderList(order: List<Order>)

    @Query("SELECT * FROM orders_table1")
    fun getAllOrders() : LiveData<List<Order>>

    @Query("SELECT * FROM orders_table1 where in_order=1")
    fun getQueue() : LiveData<List<Order>>

    @Query("SELECT * FROM ORDERS_TABLE1 where inProcces=1")
    fun getInProccesOrder() : LiveData<List<Order>>

    @Query("SELECT * FROM ORDERS_TABLE1 where isActive=1 AND isCompleated=0")
    fun getActiveOrders() : LiveData<List<Order>>
}