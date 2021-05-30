package tj.livo.artesia.services

import android.R
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import tj.livo.artesia.network.Repository.OrderRepository
import tj.livo.artesia.ui.MainActivity
import java.util.*


class GetOrderService : Service() {
    private val timer = Timer()
    private lateinit var orderRepository : OrderRepository


    private val ARTESIA_CHANNEL_ID = "ARTESIA_CHANNEL_ID"
    private val ARTESIA_CHANNEL_NAME = "ARTESIA_FOREGROUND_CHANNEL"

    val ACTION_SEND_STATUS = "ORDER_STATUS"
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        orderRepository = OrderRepository(baseContext)
        timer.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                orderRepository.getOrders()
            }
        },0,2 * 60 * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
    }



}