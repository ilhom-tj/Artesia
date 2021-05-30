package tj.livo.artesia

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.livo.artesia.models.Client
import tj.livo.artesia.models.Product
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun formatTime(seconds: Int): String? {
        val mins = if (seconds / 60 < 10) "0" + seconds / 60 else (seconds / 60).toString()
        val secs = if (seconds % 60 < 10) "0" + seconds % 60 else (seconds % 60).toString()
        return "$mins:$secs мин"
    }

     fun callTo(fragment: Fragment,phone: String) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(),
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(),
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(fragment.requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42)
            }
        } else {
            // Permission has already been granted
            callPhone(fragment.requireActivity(),phone)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 42) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
            } else {
                // permission denied, boo! Disable the
                // functionality
            }
            return
        }
    }
    fun callPhone(activity: Activity, phone : String){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))
        activity.startActivity(intent)
    }
    fun Date.toStringDate(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
     class OrderConverter{
        val gson = Gson()
        @TypeConverter
        fun stringToList(data : String) : List<Product>{
            if (data==null){
                return Collections.emptyList()
            }
            val listType = object : TypeToken<List<Product>>(){}.type

            return gson.fromJson(data,listType)
        }
        @TypeConverter
        fun listToString( list : List<Product>) : String{
            return gson.toJson(list)
        }
         @TypeConverter
         fun stringToClinet(data : String) : Client{
             val listType = object : TypeToken<Client>(){}.type
             return gson.fromJson(data,listType)
         }
         @TypeConverter
         fun clientToString( client: Client) : String{
             return gson.toJson(client)
         }
    }

}