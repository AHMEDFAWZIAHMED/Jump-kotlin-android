package a7m3d.boardGame.jump

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class FullscreenActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var changeLan: ImageView
    lateinit var logo: ImageView
    private lateinit var buttonsID: List<Int>
    val buttons = ArrayList<Button>()
    val howToPlay = HowToPlay(this)
    private var second = false

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.addFlags(1024)
        if (Process.cpuChange) changeLanguage("ar", "SD", 0)
        setContentView(R.layout.activity_fullscreen)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true)
        {
                override fun handleOnBackPressed() {
                    if (second) recreate()
                    else finishAffinity()
                }
        })
        if (Process.cDensity == 0f) {
            Handler(Looper.getMainLooper()).postDelayed({
                Process.cDensity = resources.displayMetrics.density
                Process.cWidth = resources.displayMetrics.heightPixels/Process.cDensity
                Process.cMargin = Process.cWidth/27
                Process.imgSize = ((Process.cWidth * Process.cDensity)/8f).roundToInt()
            }, 20)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            changeLan = findViewById(R.id.lang)
            if (Process.defaultLang) changeLan.setImageResource(R.drawable.arabic192)
            else changeLan.setImageResource(R.drawable.english192)
            Process.chngImgSize(changeLan, 1, 1)
            changeLan.setOnClickListener {
                if (Process.defaultLang) changeLanguage("ar", "SD")
                else changeLanguage("en", "US")
            }
            logo = findViewById(R.id.logo)
            Process.chngImgSize(logo, 1, 2)

        }, 30)

        buttonsID = listOf(R.id.b1, R.id.b2, R.id.how, R.id.b3)
        for (d in 0..3) {
            Handler(Looper.getMainLooper()).postDelayed({
                val button = findViewById<Button>(buttonsID[d])
                button.textSize = Process.cWidth/10.5f
                val btnMarg = ((Process.cWidth * Process.cDensity)/7f).roundToInt()
                val param = button.layoutParams as ViewGroup.MarginLayoutParams
                //param.height = Process.imgSize
                param.width = ((Process.cWidth * Process.cDensity)/1.5f).roundToInt()
                param.setMargins(0, (btnMarg+(d*button.height*1.25).toInt()), 0, 0)
                button.layoutParams = param
                //button.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
                button.setOnClickListener(this)
                button.visibility = View.VISIBLE
                buttons.add(button)
            }, (d + 1) * 100.toLong())
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.b3 -> {
                if (second) recreate()
                else finishAffinity()
            }
            R.id.b2 -> {
                if (second) {
                    Process.playersNumber = 1
                    Process.difficulty = 2
                }
                else {
                    Process.playersNumber = 2
                }
                startActivity(Intent(applicationContext, StartGame::class.java))
                finish()
            }
            R.id.b1 -> {
                if (second) {
                    Process.playersNumber = 1
                    Process.difficulty = 1
                    startActivity(Intent(applicationContext, StartGame::class.java))
                    finish()
                }
                else {
                    for (b in buttons) b.visibility = View.INVISIBLE
                    changeLan.visibility = View.INVISIBLE
                    buttons[0].text = getText(R.string.s_one)
                    buttons[1].text = getText(R.string.s_two)
                    buttons[3].text = getText(R.string.s_three)
                    second = true
                    for (i in 0..3) {
                        if (i == 2) continue
                        Handler(Looper.getMainLooper()).postDelayed({
                            buttons[i].visibility = View.VISIBLE
                        }, (i + 1) * 100.toLong())
                    }
                }
            }
            R.id.how -> {
                for (b in buttons) b.visibility = View.INVISIBLE
                changeLan.visibility = View.INVISIBLE
                logo.visibility = View.INVISIBLE
                supportFragmentManager.beginTransaction().add(R.id.menu, howToPlay).commit()
            }
        }
    }

    private fun changeLanguage(lang:String, cont:String, num: Int = 1) {

        val locale = Locale(lang, cont)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            val config = baseContext.resources.configuration
            config.setLocale(locale)
            baseContext.createConfigurationContext(config).resources
            if (num == 1) baseContext.resources.configuration.setToDefaults()
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            val config1 = Configuration()
            config1.locale = locale
            baseContext.resources.updateConfiguration(config1, baseContext.resources.displayMetrics)
        }

        if (Process.defaultLang) {
            Process.defaultLang = false
            Process.cpuChange = false
        }
        else {
            Process.defaultLang = true
        }
        Process.langAndCont[0] = lang
        Process.langAndCont[1] = cont
        recreate()
    }
}
