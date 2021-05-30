package tj.livo.artesia.ui.fragments.OrdersHistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tj.livo.artesia.models.Order
import tj.livo.artesia.network.Repository.OrderRepository

class OrdersHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val orderRepo = OrderRepository(application)
    fun getOrders() : LiveData<tj.livo.artesia.models.Orders>{
        return orderRepo.getOrders()
    }

    fun orders( ): LiveData<List<Order>>{
        return orderRepo.getOrdersDB()
    }

    fun addOrderToQueue(order: Order){
        order.inOrder = true
        orderRepo.changeOrder(order)
    }
    fun removeOrderFromQueue(order: Order){
        order.inOrder = false
        orderRepo.changeOrder(order)
    }

    fun getQueue():LiveData<List<Order>>{
        return orderRepo.getQueue()
    }

    fun getActiveOrders() : LiveData<List<Order>>{
        return orderRepo.getActiveOrders()
    }

    fun getInProccesOrder() : LiveData<List<Order>>{
        return orderRepo.getInProccesOrder()
    }

    fun setActiveOrder(order: Order){
        orderRepo.changeOrder(order)
    }


    fun setDisActiveOrder(order: Order){
        orderRepo.changeOrder(order)
    }

    fun cancelOrder(order: Order,cancelReason : String) : LiveData<Boolean>{
        return orderRepo.cancelOrder(order,cancelReason)
    }

    fun finishOrder(order: Order,bottlesTaken : Int,
                    bottlesLoaned: Int,total : Double,
                    totalTaken : Double
    ) : LiveData<Boolean>{
        return orderRepo.finishOrder(order,bottlesTaken,bottlesLoaned,total,totalTaken)
    }

}