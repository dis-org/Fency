package disorganizzazione.fency

class Player(activity : FencyModeActivity) : FencyModel(activity, R.integer.HIGH_STAND){

    fun changeState(to: Int){
        if(to == state) return
        state = to
        activity.updatePlayerView(this)
    }

}