package disorganizzazione.fency

class Game(activity : FencyModeActivity, private val playerOne : Player, private val playerTwo : Player) : FencyModel(activity,R.integer.GAME_DRAW) {

    fun update(){

        val s1 = playerOne.state
        val s2 = playerTwo.state

        state = if (s1 == R.integer.HIGH_ATTACK && (s2 == R.integer.LOW_STAND || s2 == R.integer.INVALID) || s1 == R.integer.LOW_ATTACK && (s2 == R.integer.HIGH_STAND || s2 == R.integer.INVALID))
            R.integer.GAME_P1
        else if (s2 == R.integer.HIGH_ATTACK && (s1 == R.integer.LOW_STAND || s1 == R.integer.INVALID) || s2 == R.integer.LOW_ATTACK && (s1 == R.integer.HIGH_STAND || s1 == R.integer.INVALID))
            R.integer.GAME_P2
        else
            R.integer.GAME_DRAW

        activity.updateGameView(state)
    }

}