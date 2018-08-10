package disorganizzazione.fency

class Game(activity : FencyModeActivity, private val playerOne : Player, private val playerTwo : Player) : FencyModel(activity,R.integer.GAME_DRAW) {

    var score1 = 0
    var score2 = 0

    override var state: Int = R.integer.GAME_DRAW
        get() = super.state
        set(value) {
            when (value){
                R.integer.GAME_DRAW -> {
                    field = R.integer.GAME_DRAW
                }
                R.integer.GAME_P1 -> {
                    score1++
                    field = R.integer.GAME_P1
                }
                R.integer.GAME_P2 -> {
                    score2++
                    field = R.integer.GAME_P2
                }
            }
            activity.updateGameView()
        }

    fun score1(): Boolean {
        when (playerOne.state) {
            R.integer.LOW_ATTACK ->
                when (playerTwo.state) {
                    R.integer.HIGH_ATTACK, R.integer.HIGH_STAND, R.integer.INVALID -> {
                        state = R.integer.GAME_P1
                        return true
                    }
                }
            R.integer.HIGH_ATTACK ->
                when (playerTwo.state) {
                    R.integer.LOW_ATTACK, R.integer.LOW_STAND, R.integer.INVALID -> {
                        state = R.integer.GAME_P1
                        return true
                    }
                }
        }
        state = R.integer.GAME_DRAW
        return false
    }

    fun score2(): Boolean {
        when (playerTwo.state) {
            R.integer.LOW_ATTACK ->
                when (playerOne.state) {
                    R.integer.HIGH_ATTACK, R.integer.HIGH_STAND, R.integer.INVALID -> {
                        state = R.integer.GAME_P2
                        return true
                    }
                }
            R.integer.HIGH_ATTACK ->
                when (playerOne.state) {
                    R.integer.LOW_ATTACK, R.integer.LOW_STAND, R.integer.INVALID -> {
                        state = R.integer.GAME_P2
                        return true
                    }
                }
        }
        state = R.integer.GAME_DRAW
        return false
    }

}