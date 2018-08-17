package disorganizzazione.fency

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : FencyActivity() {

    private var musicOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_home) // do not change order
        cntFullScreen = fullscreen_content
        super.onCreate(savedInstanceState)

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

        btnMusic.setOnClickListener {
            musicOn = if (!musicOn) {
                audioPlayerMusic?.start()
                btnMusic.setImageResource(R.mipmap.music_button_on)
                true
            } else {
                audioPlayerMusic?.pause()
                btnMusic.setImageResource(R.mipmap.music_button_off)
                false
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
        if (musicOn)
            audioPlayerMusic!!.start()
    }

}
