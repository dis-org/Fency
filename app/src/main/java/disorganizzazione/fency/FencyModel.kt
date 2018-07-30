package disorganizzazione.fency

abstract class FencyModel(protected var activity: FencyModeActivity, state: Int){
    open var state : Int = state
}