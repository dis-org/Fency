package disorganizzazione.fency

import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager

abstract class FencyModeActivity: FencyActivity() {

    var usor : Player? = null
        protected set
    var adversator : Player? = null
        protected set
    var ludum : Game? = null
        protected set

    private val VIBRATION_LENGTH : Long = 100
    protected var vibrator : Vibrator? = null

    protected var sensorHandler: SensorHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        usor = Player(this)
        adversator = Player(this)
        ludum = Game(this, usor!!, adversator!!)

        vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator

        sensorHandler = SensorHandler(this, usor!!)

        //sensorHandler!!.registerListeners() TODO: fix in mode activities subclasses

        audioPlayerEffects = MediaPlayer.create(this, R.raw.sword_clash)

    }

    override fun onResume() {
        super.onResume()
        //sensorHandler!!.registerListeners() TODO: fix in mode activities subclasses
        //audioPlayerMusic = null
        audioPlayerEffects = MediaPlayer.create(this, R.raw.sword_clash)
    }

     override fun onPause() {
        super.onPause()
        sensorHandler?.unregisterListeners()
        audioPlayerMusic?.release()
        audioPlayerEffects?.release()
    }

    override fun onStop() {
        audioPlayerMusic = null
        audioPlayerEffects = null
        finish()
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onStop()
    }

    open fun updatePlayerView(caller: Player) {
        if (caller == usor) vibrate()
    }

    open fun updateGameView() {

    }

    protected fun vibrate(){
        @Suppress("DEPRECATION")
        if(android.os.Build.VERSION.SDK_INT >= 26)
            vibrator!!.vibrate(VibrationEffect.createOneShot(VIBRATION_LENGTH, VibrationEffect.DEFAULT_AMPLITUDE))
        else
            vibrator!!.vibrate(VIBRATION_LENGTH)
    }
}