package tj.livo.artesia.services.shitCode;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import tj.livo.artesia.R;
import tj.livo.artesia.models.Order;
import tj.livo.artesia.network.Repository.OrderRepository;
import tj.livo.artesia.ui.MainActivity;

import java.util.*;

public class TimeService extends Service {

    private Timer timer;
    private PowerManager.WakeLock wakeLock;
    private int orderPassedTime;
    private int remainingTime;
    private int orderId;
    private int newOrderFetchTime; // used to know when to fetch new orders
    private String orderAddress;

    private static final String ARTESIA_CHANNEL_ID = "ARTESIA_CHANNEL_ID";
    private static final String ARTESIA_CHANNEL_NAME = "ARTESIA_FOREGROUND_CHANNEL";

    public static final String ACTION_SEND_STATUS = "ORDER_STATUS";

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        try {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "artesia::wake_lock");
        } catch (Exception e){}
        LiveData<List<Order>> order = new OrderRepository(getApplicationContext()).getInProccesOrder();
        order.observeForever(new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                if (orders.isEmpty()){
                    onDestroy();
                }else {
                    current = orders.get(0);
                    orderId = current.getId();
                    orderAddress = current.getStreet();
                }

            }
        });
        timer = null;
        newOrderFetchTime = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        // turn off CPU sleeping mode
        wakeLock.acquire(10*60*1000L /*10 minutes*/);

        createNotificationChannel();
        Notification orderNotification = createArtesiaForegroundNotification();

        startForeground(1, orderNotification);

        // start timer
        timer.schedule(new OrderQueueTimerTask(), 0, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    Order current = null;
    private class OrderQueueTimerTask extends TimerTask {
        @Override
        public void run() {
                orderPassedTime++;
                remainingTime = 900 - orderPassedTime >= 0 ? 900 - orderPassedTime : 0; // avoid negative
                // update order delivery notification
                notifyNewNotification(createOrderStatusNotification(), 2);
                // warn the user about remaining time
                if((900 - orderPassedTime) == 600
                        || (900 - orderPassedTime) == 300
                        || (900 - orderPassedTime) == 120) { // vibrate phone
                    makeSoundAndVibrate();
                }


                // update the UI with broadcasting data to it
                sendBroadcast(new Intent()
                        .putExtra("new_order", 0)
                        .putExtra("order_id", orderId)
                        .putExtra("remaining_time", remainingTime)
                        .setAction(ACTION_SEND_STATUS)
                        .addCategory(Intent.CATEGORY_DEFAULT)
                );

            newOrderFetchTime++;
        }
    }


    // show new notification
    private void notifyNewNotification(Notification notification, int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.notify(id, notification);
        } else {
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
            managerCompat.notify(id, notification);
        }
    }

    private void makeSoundAndVibrate(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(1000);
        }
        // play sound
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    ARTESIA_CHANNEL_ID,
                    ARTESIA_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createArtesiaForegroundNotification(){
        return new NotificationCompat.Builder(this, ARTESIA_CHANNEL_ID)
                .setContentTitle("Артезия")
                .setContentText("Отслеживаем новые заказы")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                1,
                                new Intent(getApplicationContext(), MainActivity.class),
                                0)
                )
                .build();
    }

    public static String formatTime(int seconds){
        String mins = (seconds / 60 < 10) ? "0" + seconds / 60 : String.valueOf(seconds / 60);
        String secs = (seconds % 60 < 10) ? "0" + seconds % 60 : String.valueOf(seconds % 60);
        return mins + ":" + secs + " мин";
    }
    private Notification createOrderStatusNotification(){
        return new NotificationCompat.Builder(this, ARTESIA_CHANNEL_ID)
                .setContentTitle("Доставка заказ #" + orderId)
                .setContentText("Осталось: " + formatTime(remainingTime) + " " + orderAddress)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                1,
                                new Intent(getApplicationContext(), MainActivity.class),
                                0)
                )
                .build();
    }

    private Notification createNewOrdersNotification(){
        return new NotificationCompat.Builder(this, ARTESIA_CHANNEL_ID)
                .setContentTitle("Есть новые заказы!")
                .setContentText("Пожалуйста, проверьте заказы")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentIntent(
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                1,
                                new Intent(getApplicationContext(), MainActivity.class),
                                0)
                )
                .build();
    }

    @Override
    public void onDestroy() {
        wakeLock.release();
        timer.cancel();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
