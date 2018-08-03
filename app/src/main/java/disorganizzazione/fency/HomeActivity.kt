package disorganizzazione.fency

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.CheckBox
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : FencyActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        cntFullScreen = fullscreen_content

        btnTutorial.setOnClickListener {
            playAudioEffect()
            startActivity(Intent(this, TutorialModeActivity::class.java))
        }

        btnPractice.setOnClickListener {
            playAudioEffect()
            startActivity(Intent(this, PracticeModeActivity::class.java))
        }

        btnDuel.setOnClickListener {
            playAudioEffect()
            startActivity(Intent(this, DuelModeActivity::class.java))
        }

        audioBox.setOnClickListener {
            if (audioBox.isChecked) {
                audioPlayerMusic?.start()
            } else {
                audioPlayerMusic?.pause()
            }
        }

    }

    private fun playAudioEffect() {
        audioPlayerEffects!!.start()
        audioPlayerEffects!!.setOnCompletionListener {
            audioPlayerEffects!!.setOnCompletionListener(null)
            audioPlayerEffects!!.release()
        }
    }

    override fun onResume() {
        super.onResume()

        audioPlayerEffects = MediaPlayer.create(this, R.raw.turn_page)
        audioPlayerMusic = MediaPlayer.create(this, R.raw.menu_theme)
        audioPlayerMusic!!.isLooping = true
        if (audioBox.isChecked)
            audioPlayerMusic!!.start()
    }

}
