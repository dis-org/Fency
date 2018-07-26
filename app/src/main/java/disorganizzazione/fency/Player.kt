package disorganizzazione.fency

class Player(activity : FencyModeActivity) : FencyModel(activity, R.integer.HIGH_STAND){

    override var state: Int = super.state
        set(to) {
            if(to == state) return
            state = to
            activity.updatePlayerView(this)
    }

}