package hr.corvuspay.demoshop

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class DemoShopApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}