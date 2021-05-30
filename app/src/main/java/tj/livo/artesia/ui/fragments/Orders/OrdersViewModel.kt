package tj.livo.artesia.ui.fragments.Orders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.room.FtsOptions
import tj.livo.artesia.models.Order
import tj.livo.artesia.network.Repository.OrderRepository

class OrdersViewModel(application: Application) : AndroidViewModel(application)
{
    private val orderRepository = OrderRepository(application)

}