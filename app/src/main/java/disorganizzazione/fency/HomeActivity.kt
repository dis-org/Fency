package disorganizzazione.fency

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : FencyActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        cntFullScreen = fullscreen_content
        goFullScreen()

        btnDuel.setOnClickListener { switchActivity(DuelModeActivity::class.java) }

    }

}
