package disorganizzazione.fency

import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator

abstract class FencyModeActivity: FencyActivity() {

    protected var usor : Player? = null
    protected var adversator : Player? = null
    protected var ludum : Game? = null

    private val VIBRATION_LENGTH : Long = 100
    protected var vibrator : Vibrator? = null

    protected var sensorHandler: SensorHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usor = Player(this)
        adversator = Player(this)
        ludum = Game(this, usor!!, adversator!!)

        vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator

        sensorHandler = SensorHandler(this, usor!!)
        sensorHandler!!.registerListeners()

        audioPlayerEffects = MediaPlayer.create(this, R.raw.sword_clash)

    }

    override fun onResume() {
        super.onResume()

        sensorHandler!!.registerListeners()

        audioPlayerEffects = MediaPlayer.create(this, R.raw.sword_clash)
    }

     override fun onPause() {
        super.onPause()
        audioPlayerMusic?.release()
        audioPlayerEffects?.release()
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onStop()
    }

    abstract fun updatePlayerView(caller: Player)

    open fun updateGameView(gameState: Int) {
        if (gameState == R.integer.GAME_DRAW) vibrate()

    }

    private fun vibrate(){
        if(android.os.Build.VERSION.SDK_INT >= 26)
            vibrator!!.vibrate(VibrationEffect.createOneShot(VIBRATION_LENGTH, VibrationEffect.DEFAULT_AMPLITUDE))
        else
            vibrator!!.vibrate(VIBRATION_LENGTH)
    }
}