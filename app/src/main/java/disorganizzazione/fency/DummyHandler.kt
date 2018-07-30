package disorganizzazione.fency

import android.os.Handler
import android.widget.Toast

class DummyHandler(context: PracticeModeActivity, player: Player) : FencyHandler(context as FencyModeActivity, player) {

    private var waitingTime: Long = 2500 //millisec
    private var state: Int = 0
    private val handler: Handler = Handler()
    private var runnable: Runnable? = null
    var combo: Int = 0
        private set

    fun step(success: Boolean) {

        if (success) {
            combo++
            state = (state + 1) % 4

            if (combo % 4 == 0 && waitingTime >= 700) {
                waitingTime -= 200
                Toast.makeText(context, "speed up!", Toast.LENGTH_SHORT).show()
            }
        } else {
            combo = 0
            waitingTime = 2500
        }

        (context as PracticeModeActivity).impera(toImperium())

        // Allow opponent state change only after delay
        handler.postDelayed({player.state= toAction()}, waitingTime)

    }

    fun toAction(): Int {
        var action = -1
        when (this.state) {
            0 -> action = R.integer.HIGH_ATTACK
            1 -> action = R.integer.LOW_ATTACK
            2 -> action = R.integer.HIGH_STAND
            3 -> action = R.integer.LOW_STAND
        }
        return action
    }

    fun toImperium(): Int {
        var action = -1
        when (this.state) {
            0 -> action = R.integer.HIGH_STAND
            1 -> action = R.integer.LOW_STAND
            2 -> action = R.integer.LOW_ATTACK
            3 -> action = R.integer.HIGH_ATTACK
        }
        return action
    }

    fun onExit() {
        handler.removeCallbacks(runnable)
    }

}
