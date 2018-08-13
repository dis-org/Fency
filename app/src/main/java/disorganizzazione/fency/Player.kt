package disorganizzazione.fency

class Player(activity : FencyModeActivity) : FencyModel(activity, R.integer.INVALID){

    override var state: Int = super.state
        set(to) {
            if (to != field) {
                field = to
                activity.updatePlayerView(this)
            }
    }

    override fun toString(): String {
        return "Stato = $state"
    }

}