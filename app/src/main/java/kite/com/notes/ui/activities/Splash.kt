package kite.com.notes.ui.activities

import android.annotation.TargetApi
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.os.Build
import android.support.annotation.RequiresApi
import java.util.*






@Suppress("DEPRECATION")
class Splash : AppCompatActivity() {

    private val PREF_LANGUAGE = "prefLang"

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(kite.com.notes.R.layout.activity_splash)
        supportActionBar!!.hide()
        hideNavigation()


        getIdioma()
        getLanguageShared()


        val count = object : CountDownTimer(1000, 200) {

            override fun onFinish() {
                startMain()
            }

            override fun onTick(millisUntilFinished: Long) {
            }

        }
        count.start()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun getIdioma() {
        val idioma: String = Locale.getDefault().language

        if (idioma == "ca" || idioma == "gl" || idioma == "eu" || idioma == "es") {
            val locale = Locale("es")
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
            this.applicationContext.resources.updateConfiguration(config, null)

        } else {
            val locale = Locale("en")
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
            this.applicationContext.resources.updateConfiguration(config, null)
        }
    }

    private fun getLanguageShared() {
        val dataLanguage: SharedPreferences = getSharedPreferences(PREF_LANGUAGE, 0)
        val language: String = dataLanguage.getString("lang", null) ?: return
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        this.applicationContext.resources.updateConfiguration(config, null)

    }

    private fun startMain() {
        val intent = Intent(this, Logo::class.java)
        startActivity(intent)
        finish()
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun hideNavigation() {
        val decorView = window.decorView

        decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or SYSTEM_UI_FLAG_FULLSCREEN
                or SYSTEM_UI_FLAG_IMMERSIVE)
    }

}
