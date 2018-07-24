package disorganizzazione.fency

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_duel_mode.*

class DuelModeActivity: FencyActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duel_mode)
        cntFullScreen = fullscreen_content
        goFullScreen()
    }

}