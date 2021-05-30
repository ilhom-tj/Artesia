package tj.livo.artesia.models.Alerts

import android.app.AlertDialog
import android.content.Context


class Alerts(val context: Context) {
    fun loginSuccesful() {
        val alert = AlertDialog.Builder(context)
            .setTitle("Ура!")
            .setMessage("Вы успешно авторизованы.")
            .create()
        alert.show()
    }
    fun customAlert( title : String, body : String) {
        val alert = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(body)
            .create()
        alert.show()
    }
}