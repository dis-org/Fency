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
    protected var vibrationEnabled:Boolean = true

    protected var sensorHandler: SensorHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        usor = Player(this)
        adversator = Player(this)
        ludum = Game(this, usor!!, adversator!!)

        vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator

        sensorHandler = SensorHandler(this, usor!!)

    }

     override fun onPause() {
        super.onPause()
        sensorHandler?.unregisterListeners()
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

    open fun updateGameView(state: Int) {
        if (state == R.integer.GAME_DRAW) {
            vibrate()
        }
    }

    protected fun vibrate() {
        if (vibrationEnabled) {
            var pattern = longArrayOf(1, VIBRATION_LENGTH)

            when(ludum!!.state) {
                R.integer.GAME_DRAW -> pattern = longArrayOf(1, VIBRATION_LENGTH)
                R.integer.GAME_P1 -> pattern = longArrayOf(1, 80, 40, 80)
                R.integer.GAME_P2 -> pattern = longArrayOf(1, 200)
                R.integer.GAME_W1,R.integer.GAME_W2 -> pattern = longArrayOf(1, 50, 20, 50, 20, 300)
            }
            if (android.os.Build.VERSION.SDK_INT >= 26)
                vibrator!!.vibrate(VibrationEffect.createWaveform(pattern, -1))
            else
                @Suppress("DEPRECATION") vibrator!!.vibrate(pattern, -1)
        }
    }
}