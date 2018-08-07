package disorganizzazione.fency

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_practice_mode.*


class PracticeModeActivity: FencyModeActivity() {

    private var arbiter: DummyHandler? = null
    private var discipuli: ImageView? = null
    private var magistri: ImageView? = null
    private var imperium: TextView? = null
    private var approbatio: TextView? = null
    private var sequentia: TextView? = null

    private var runnable: Runnable? = null
    private val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_practice_mode) // do not change order
        cntFullScreen = fullscreen_content
        super.onCreate(savedInstanceState)

        runnable = Runnable { approbatio!!.text = "" }
        audioPlayerMusic = MediaPlayer.create(this, R.raw.approve_01) // TODO("Should be audioPlayerEffects2 or array of MediaPlayer")
        discipuli = ivPlayerState
        magistri = ivOpponentState
        imperium = tvCommand
        approbatio = tvCheck
        sequentia = tvCombo
        arbiter = DummyHandler(this, adversator!!)
        imperium!!.text = ""
        approbatio!!.text = ""

        //start tutorial
        arbiter!!.step(false)

    }

    override fun onStop() {
        handler.removeCallbacks(runnable)
        arbiter!!.onExit()
        super.onStop()
    }

    override fun updatePlayerView(caller: Player) {

        val state = caller.state
        var icon: ImageView? = null
        var attackOn = true

        if (caller == usor) {
            icon = discipuli
            attackOn = userAttacking
        } else if (caller == adversator) {
            icon = magistri
            attackOn = opponentAttacking
        }

        icon!!.imageAlpha = 255

        if (!attackOn) {
            //change player img
            when (state) {
                R.integer.HIGH_STAND -> icon.setImageResource(R.mipmap.fency_high_stand)
                R.integer.LOW_STAND -> icon.setImageResource(R.mipmap.fency_low_stand)
                R.integer.HIGH_ATTACK -> icon.setImageResource(R.mipmap.fency_high_attack)
                R.integer.LOW_ATTACK -> icon.setImageResource(R.mipmap.fency_low_attack)
                R.integer.INVALID -> icon.imageAlpha = 80
            }
            if (state == R.integer.HIGH_ATTACK || state == R.integer.LOW_ATTACK) {

                if (caller == usor) {
                    userAttacking = true
                    handler.postDelayed({
                        // Allow user image change only after delay
                        userAttacking = false
                    }, ATTACK_ANIMATION_DELAY)
                } else if (caller == adversator) {
                    opponentAttacking = true
                    Handler().postDelayed({
                        // Allow opponent image and state change only after delay
                        opponentAttacking = false
                        adversator!!.state = R.integer.HIGH_STAND
                    }, ATTACK_ANIMATION_DELAY)
                }
            }
        }
    }

     override fun updateGameView(gameState: Int) {
        super.updateGameView(gameState)

        if (usor!!.state == arbiter!!.toImperium()) {
            approbatio!!.setText(R.string.success)
            audioPlayerMusic?.start()
            arbiter!!.step(true)
        } else {
            approbatio!!.setText(R.string.failure)
            arbiter!!.step(false)
        }
        //update combo TextView
        sequentia!!.text = arbiter!!.combo.toString()
        sequentia!!.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in))

        //clear the approbatio label with a delay
        handler.postDelayed(runnable, SERVICE_TIME)
    }

    fun impera(actum: Int) {

        when (actum) {
            R.integer.HIGH_ATTACK -> imperium!!.setText(R.string.attackUp)
            R.integer.HIGH_STAND -> imperium!!.setText(R.string.parryUp)
            R.integer.LOW_ATTACK -> imperium!!.setText(R.string.attackDown)
            R.integer.LOW_STAND -> imperium!!.setText(R.string.parryDown)
        }
        imperium!!.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left))

    }

    companion object {

        private const val ATTACK_ANIMATION_DELAY: Long = 500 //milliseconds
        private const val SERVICE_TIME: Long = 1500
    }

}