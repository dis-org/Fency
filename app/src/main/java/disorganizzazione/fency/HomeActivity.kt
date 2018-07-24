package disorganizzazione.fency

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : FencyActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        cntFullScreen = fullscreen_content

        btnDuel.setOnClickListener {
            audioPlayerEffects!!.start()
            audioPlayerEffects!!.setOnCompletionListener {
                audioPlayerEffects!!.setOnCompletionListener { null }
                audioPlayerEffects!!.release()
            }
            startActivity(Intent(this, DuelModeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        audioPlayerEffects = MediaPlayer.create(this, R.raw.turn_page)
        audioPlayerMusic = MediaPlayer.create(this, R.raw.menu_theme)
        audioPlayerMusic!!.isLooping = true
        audioPlayerMusic!!.start()
    }

}
