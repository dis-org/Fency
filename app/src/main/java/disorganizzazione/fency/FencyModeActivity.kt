package disorganizzazione.fency

import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator

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

    protected var userAttacking = false
    protected var opponentAttacking = false

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
        //audioPlayerMusic = null
        audioPlayerEffects = MediaPlayer.create(this, R.raw.sword_clash)
    }

     override fun onPause() {
        super.onPause()
        sensorHandler!!.unregisterListeners()
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

    abstract fun updatePlayerView(caller: Player)

    open fun updateGameView(gameState: Int) {
        if (gameState == R.integer.GAME_DRAW) {
            vibrate()
            audioPlayerEffects?.start()
        }
    }

    private fun vibrate(){
        if(android.os.Build.VERSION.SDK_INT >= 26)
            vibrator!!.vibrate(VibrationEffect.createOneShot(VIBRATION_LENGTH, VibrationEffect.DEFAULT_AMPLITUDE))
        else
            vibrator!!.vibrate(VIBRATION_LENGTH)
    }
}