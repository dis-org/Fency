package disorganizzazione.fency

class Game(activity : FencyModeActivity, private val playerOne : Player, private val playerTwo : Player) : FencyModel(activity,R.integer.GAME_DRAW) {

    var score1 = 0
        private set
    var score2 = 0
        private set
    var maxScore = Int.MAX_VALUE

    override var state: Int = R.integer.GAME_DRAW
        get() = super.state
        set(value) {
            when (value){
                R.integer.GAME_DRAW -> {
                    field = R.integer.GAME_DRAW
                }
                R.integer.GAME_P1 -> {
                    field = R.integer.GAME_P1
                    score1++
                    if (score1 >= maxScore) {
                        activity.updateGameView(field)
                        field = R.integer.GAME_W1
                    }
                }
                R.integer.GAME_P2 -> {
                    field = R.integer.GAME_P2
                    score2++
                    if (score2 >= maxScore) {
                        activity.updateGameView(field)
                        field = R.integer.GAME_W2
                    }
                }
                R.integer.GAME_W1 -> {
                    field = R.integer.GAME_W1
                }
                R.integer.GAME_W2 -> {
                    field = R.integer.GAME_W2
                }
                else -> throw Exception("Invalid game state.")
            }
            activity.updateGameView(field)
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

    fun reset(){
        score1 = 0
        score2 = 0
        state = R.integer.GAME_DRAW
    }

    override fun toString(): String {
        return "$score1:$score2"
    }

}