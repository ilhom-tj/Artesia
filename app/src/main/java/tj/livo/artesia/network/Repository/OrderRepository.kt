package tj.livo.artesia.network.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tj.livo.artesia.SessionManager
import tj.livo.artesia.Utils.getCurrentDateTime
import tj.livo.artesia.Utils.toStringDate
import tj.livo.artesia.db.OrdersQueue.OrdersDB
import tj.livo.artesia.models.Order
import tj.livo.artesia.models.OrderListRequest.FinishOrderBody
import tj.livo.artesia.models.OrderListRequest.OrderListRequestItem
import tj.livo.artesia.models.Orders
import tj.livo.artesia.network.RetrofitInstance

class OrderRepository(context: Context){

    private val sessionManager = SessionManager(context.applicationContext)
    private val api = RetrofitInstance(context.applicationContext).api()
    private val ordersDB = OrdersDB.getInstance(context.applicationContext)
    private val ordersDAO = ordersDB.ordersDAO
    fun getOrders() : LiveData<Orders>{
        val date = getCurrentDateTime()
        val liveData = MutableLiveData<Orders>()
        val dateInString = date.toStringDate("yyyy.MM.dd") //TODO CHANGE PLACE TO PROPERTY
        Log.e("Date",dateInString)
        api.orders(
            sessionManager.getToken().toString(),
            sessionManager.getAutoId().toString(),
        dateInString.replace("/",".")) // If you see string like "2021.02.11" replace it with dateInString
            .enqueue(object : Callback<tj.livo.artesia.models.Orders>{
                override fun onResponse(call: Call<Orders>, response: Response<Orders>) {
                    if (response.isSuccessful){
                        liveData.value = response.body()
                        if (response.body()!!.orders.isNotEmpty()){
                            saveOrders(response.body()!!.orders)
                        }
                    }
                }

                override fun onFailure(call: Call<Orders>, t: Throwable) {

                }

            })
        return liveData
    }
    fun saveOrders(list : List<Order>) = GlobalScope.launch {
        ordersDAO.addOrderList(list)
    }

    fun getOrdersDB(): LiveData<List<Order>> {
        return ordersDAO.getAllOrders()
    }
    fun getQueue(): LiveData<List<Order>>{
        return ordersDAO.getQueue()
    }
    fun getActiveOrders() : LiveData<List<Order>>{
        return ordersDAO.getActiveOrders()
    }
    fun getInProccesOrder(): LiveData<List<Order>>{
        return ordersDAO.getInProccesOrder()
    }
    fun changeOrder(order: Order) = GlobalScope.launch {
        ordersDAO.addOrders(order)
    }

    fun cancelOrder(order: Order,cancelReason : String) : LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        val token = sessionManager.getToken()
        api.cancelOrder(
            token.toString(),
            order.id,
            0,
            0,
            0.0,
            0.0,
            4,
            cancelReason
        ).enqueue( object  : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200){
                    liveData.value = true
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

        })
        return liveData
    }

    fun finishOrder(order: Order,bottlesTaken : Int,bottlesLoaned: Int,total : Double,totalTaken : Double) : LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        val token = sessionManager.getToken()
        val list : ArrayList<OrderListRequestItem> = arrayListOf()
        order.products.forEach {
            val orderListRequestItem = OrderListRequestItem(order.id,it.id,it.pivot.quantity)
            list.add(orderListRequestItem)
        }
        val gson = Gson()
        Log.e("GSON",gson.toJson(order.products))
        api.finishOrder(
            token.toString(),
            order.id,
            bottlesTaken,
            bottlesLoaned,
            total,
            totalTaken,
            3,
            "",
            gson.toJson(list)).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200){
                    liveData.value = true
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

        })
        return liveData
    }
}