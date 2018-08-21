package disorganizzazione.fency

import android.os.Handler

class DummyHandler(context: PracticeModeActivity, player: Player) : FencyHandler(context as FencyModeActivity, player) {

    private val ANIMATION_TIME = 700L
    private val NUMBER_OF_STATES = 2
    private var state: Int = 0
    private val handler: Handler = Handler()
    private var runnable: Runnable? = null
    var combo: Int = 0
        private set

    fun step(success: Boolean) {

        if (success) {
            combo++
            state = (state + 1) % NUMBER_OF_STATES
        } else {
            combo = 0
        }

        (context as PracticeModeActivity).impera(toImperium())

        // Allow opponent state change only after delay
        handler.postDelayed({player.state= toAction()}, ANIMATION_TIME)

    }

    fun toAction(): Int {
        var action = -1
        when (this.state) {
            0 -> action = R.integer.HIGH_STAND
            1 -> action = R.integer.LOW_STAND
        }
        return action
    }

    fun toImperium(): Int {
        var action = -1
        when (this.state) {
            0 -> action = R.integer.LOW_ATTACK
            1 -> action = R.integer.HIGH_ATTACK
        }
        return action
    }

    fun onExit() {
        handler.removeCallbacks(runnable)
    }

}
