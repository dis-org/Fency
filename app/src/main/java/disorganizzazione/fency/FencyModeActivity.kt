package disorganizzazione.fency

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator

abstract class FencyModeActivity: FencyActivity() {

    protected var usor : Player? = null
    protected var adversator : Player? = null
    protected var ludum : Game? = null

    protected var vibrator : Vibrator? = null
    private val VIBRATION_LENGTH : Long = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usor = Player(this)
        adversator = Player(this)
        ludum = Game(this, usor!!, adversator!!)

        vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator

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