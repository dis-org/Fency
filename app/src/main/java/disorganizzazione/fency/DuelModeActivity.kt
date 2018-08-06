package disorganizzazione.fency

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_duel_mode.*
import com.google.android.gms.nearby.connection.Strategy

class DuelModeActivity: FencyModeActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_duel_mode)
        cntFullScreen = fullscreen_content
        super.onCreate(savedInstanceState)

    }

    override fun updatePlayerView(caller: Player) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private val STRATEGY = Strategy.P2P_STAR
    }
}