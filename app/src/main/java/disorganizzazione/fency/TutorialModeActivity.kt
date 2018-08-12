package disorganizzazione.fency

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tutorial_mode.*

class TutorialModeActivity: FencyModeActivity() {

    private val ATTACK_ANIMATION_DELAY: Long = 1000 //milliseconds

    private val usor_active_alpha: Float = 1f
    private val usor_inactive_alpha: Float = 0.5f
    private val adve_active_alpha: Float = 0.8f
    private val adve_inactive_alpha: Float = 0.3f

    private var usor_mid : ImageView? = null

    private var usor_h_g : ImageView? = null
    private var usor_l_g : ImageView? = null
    private var usor_h_a : ImageView? = null
    private var usor_l_a : ImageView? = null

    private var adve_h_a : ImageView? = null
    private var adve_l_a : ImageView? = null
    private var adve_l_g : ImageView? = null
    private var adve_h_g : ImageView? = null

    private var tv_h_g: TextView? = null
    private var tv_l_g: TextView? = null
    private var tv_h_a: TextView? = null
    private var tv_l_a: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_tutorial_mode) // do not change order
        cntFullScreen = fullscreen_content
        super.onCreate(savedInstanceState)

        usor_mid = iv_tutorial_invalid

        usor_h_g = iv_tutorial_player_high_stand
        usor_l_g = iv_tutorial_player_low_stand
        usor_h_a = iv_tutorial_player_high_attack
        usor_l_a = iv_tutorial_player_low_attack

        adve_h_a = iv_tutorial_opponent_high_attack
        adve_l_a = iv_tutorial_opponent_low_attack
        adve_l_g = iv_tutorial_opponent_low_stand
        adve_h_g = iv_tutorial_opponent_high_stand

        tv_h_g = underline_high_guard
        tv_l_g = underline_low_guard
        tv_h_a = underline_high_attack
        tv_l_a = underline_low_attack

        audioPlayerMusic = MediaPlayer.create(this, R.raw.approve_01)
        audioPlayerEffects = MediaPlayer.create(this, R.raw.sword_clash)

        sensorHandler!!.registerListeners()
    }

    override fun updatePlayerView(caller: Player) {
        super.updatePlayerView(caller)
        if (caller == usor){

            // Reset every ImageView and TextView
            usor_h_g!!.alpha = usor_inactive_alpha
            usor_l_g!!.alpha = usor_inactive_alpha
            usor_h_a!!.alpha = usor_inactive_alpha
            usor_l_a!!.alpha = usor_inactive_alpha
            adve_h_a!!.alpha = adve_inactive_alpha
            adve_l_a!!.alpha = adve_inactive_alpha
            adve_l_g!!.alpha = adve_inactive_alpha
            adve_h_g!!.alpha = adve_inactive_alpha
            tv_h_g!!.alpha = usor_inactive_alpha
            tv_l_g!!.alpha = usor_inactive_alpha
            tv_h_a!!.alpha = usor_inactive_alpha
            tv_l_a!!.alpha = usor_inactive_alpha

            var usor_icon: ImageView? = null
            var adve_icon: ImageView? = null
            var state_tv: TextView? = null

            var new_adve_state = -1
            val state = caller.state

            when (state) {
                R.integer.INVALID -> {
                    usor_mid!!.setImageResource(R.mipmap.img_fency_tutorial_invalid)
                    return
                }
                R.integer.HIGH_STAND -> {
                    usor_icon = usor_h_g
                    adve_icon = adve_h_a
                    state_tv = tv_h_g
                    new_adve_state = R.integer.HIGH_ATTACK
                }
                R.integer.LOW_STAND -> {
                    usor_icon = usor_l_g
                    adve_icon = adve_l_a
                    state_tv = tv_l_g
                    new_adve_state = R.integer.LOW_ATTACK
                }
                R.integer.HIGH_ATTACK -> {
                    usor_icon = usor_h_a
                    adve_icon = adve_l_g
                    state_tv = tv_h_a
                    new_adve_state = R.integer.LOW_STAND
                }
                R.integer.LOW_ATTACK -> {
                    usor_icon = usor_l_a
                    adve_icon = adve_h_g
                    state_tv = tv_l_a
                    new_adve_state = R.integer.HIGH_STAND
                }
            }
            usor_mid!!.setImageResource(R.mipmap.img_fency_tutorial_middle)
            usor_icon!!.alpha = usor_active_alpha
            adve_icon!!.alpha = adve_active_alpha
            state_tv!!.alpha = usor_active_alpha

            // Allow adversator change state only if necessary
            // This makes the device vibrate just once
            adversator!!.state = new_adve_state
        }
    }
}