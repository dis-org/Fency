package disorganizzazione.fency

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.view.View

abstract class FencyActivity : AppCompatActivity(){

    protected abstract var cntFullScreen: View?
    protected abstract var audioPlayerMusic: MediaPlayer?
    protected abstract var audioPlayerEffects: MediaPlayer?

    protected fun goFullScreen(){
        cntFullScreen!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onResume() {
        super.onResume()
        goFullScreen()
    }

    override fun onPause() {
        super.onPause()
        audioPlayerMusic?.release()
        audioPlayerEffects?.release()
    }

}