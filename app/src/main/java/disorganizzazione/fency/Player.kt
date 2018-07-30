package disorganizzazione.fency

class Player(activity : FencyModeActivity) : FencyModel(activity, R.integer.HIGH_STAND){

    override var state: Int = super.state
        set(to) {
            if(to == field) return

            field = to
            activity.updatePlayerView(this)

            if (field == R.integer.HIGH_ATTACK || field == R.integer.LOW_ATTACK) {
                activity.ludum!!.update()
            }

    }

    override fun toString(): String {
        return "Stato = $state"
    }

}