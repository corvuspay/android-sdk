package hr.corvuspay.demoshop.helpers

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.FrameLayout
import android.widget.ProgressBar
import hr.corvuspay.demoshop.activities.ShopActivity
import hr.corvuspay.demoshop.events.LoaderEvent
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


object Helper {

    fun showSuccess(context: Context, activity: Activity): AlertDialog =
        AlertDialog.Builder(context)
            .setTitle("Success")
            .setMessage("Order placed successfully!")
            .setNeutralButton("Close") { dialog, _ ->
                CartSingleton.instance.emptyCart()
                dialog.dismiss()
                context.startActivity(Intent(context, ShopActivity::class.java))
            }
            .show()

    fun showError(message: String, context: Context,activity: Activity): AlertDialog =
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(message)
            .setNeutralButton("Close") { dialog, _ ->
                //dialog.dismiss()
                EventBus.getDefault().post(LoaderEvent(""))

            }
            .show()

    fun getFutureTimestamp(): Long {
        val millis =
            System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Constants.BEST_BEFORE_SECONDS)
        return TimeUnit.MILLISECONDS.toSeconds(millis)
    }
}