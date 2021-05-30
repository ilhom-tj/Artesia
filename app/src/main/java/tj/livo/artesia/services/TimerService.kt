package tj.livo.artesia.services

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.os.PowerManager.WakeLock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import tj.livo.artesia.Utils.formatTime
import tj.livo.artesia.ui.MainActivity
import java.sql.Time
import java.util.*


class TimerService : Service() {
    private  var timer: Timer = Timer()
    private var orderPassedTime: Int = 0
    private var remainingTime: Int = 0
    private var orderId: Int = 0
    private var newOrderFetchTime = 0
    private val orderAddress: String? = null



companion object{
    private var ARTESIA_CHANNEL_ID = "ARTESIA_CHANNEL_ID"
    private var ARTESIA_CHANNEL_NAME = "ARTESIA_FOREGROUND_CHANNEL"
    var ACTION_SEND_STATUS = "ORDER_STATUS"
}
    private var wakeLock: WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        try {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "artesia::wake_lock")
        } catch (e: Exception) {
        }
        orderId = 0
        newOrderFetchTime = 0
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (timer!=null){
            timer.cancel()
        }
        timer = Timer()

        // turn off CPU sleeping mode
        wakeLock!!.acquire()

        createNotificationChannel()
        val orderNotification: Notification = createArtesiaForegroundNotification()

        startForeground(1, orderNotification)

        // start timer
       return super.onStartCommand(intent, flags, startId)
    }

    private fun notifyNewNotification(notification: Notification, id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.notify(id, notification)
        } else {
            val managerCompat = NotificationManagerCompat.from(applicationContext)
            managerCompat.notify(id, notification)
        }
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                ARTESIA_CHANNEL_ID,
                ARTESIA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createArtesiaForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, ARTESIA_CHANNEL_ID)
            .setContentTitle("Артезия")
            .setContentText("Отслеживаем новые заказы")
            .setSmallIcon(R.drawable.arrow_down_float)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    1,
                    Intent(applicationContext, MainActivity::class.java),
                    0
                )
            )
            .build()
    }

    private fun createOrderStatusNotification(): Notification? {
        return NotificationCompat.Builder(this, ARTESIA_CHANNEL_ID)
            .setContentTitle("Доставка заказ #$orderId")
            .setContentText("Осталось: " + formatTime(remainingTime).toString() + " " + orderAddress)
            .setSmallIcon(R.drawable.arrow_down_float)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    1,
                    Intent(applicationContext, MainActivity::class.java),
                    0
                )
            )
            .build()
    }


    private fun makeSoundAndVibrate() {
        val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(1000)
        }
        // play sound
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        wakeLock!!.release()
        timer.cancel()
    }
}