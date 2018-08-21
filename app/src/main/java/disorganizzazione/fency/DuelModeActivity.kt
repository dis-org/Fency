package disorganizzazione.fency

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

import kotlinx.android.synthetic.main.fragment_go.*
import kotlinx.android.synthetic.main.fragment_log.*
import kotlinx.android.synthetic.main.fragment_ready.*
import kotlinx.android.synthetic.main.fragment_buttons.*

import kotlinx.android.synthetic.main.activity_duel_mode.*

class DuelModeActivity: FencyModeActivity(){

    companion object {
        private const val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
        private val REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION)

        private val STRATEGY = Strategy.P2P_STAR

        private const val H_A_BYTE = 0.toByte()
        private const val L_A_BYTE = 1.toByte()
        private const val DRAW_BYTE = 2.toByte()
        private const val SCORE_BYTE = 3.toByte()

        /** Returns true if the app was granted all the permissions. Otherwise, returns false.  */
        private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }

    override fun onStart() {
        super.onStart()

        if (android.os.Build.VERSION.SDK_INT >= 23)
            if (!hasPermissions(this, *REQUIRED_PERMISSIONS)) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS)
            }
    }

    /** Handles user acceptance (or denial) of our permission request.  */
    @CallSuper
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return
        }

        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, R.string.error_missing_permissions, Toast.LENGTH_LONG).show()
                finish()
                return
            }
        }
        recreate()
    }

    private val signum = TriaNomina().toString()
    private var adversatorSigna: String? = null

    private var connectionsClient: ConnectionsClient? = null
    private var opponentEndpointId: String? = null

    private var log = LogFragment()
    private var buttons = ButtonsFragment()

    private var ready = ReadyFragment()
    private var go = GoFragment()

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_duel_mode) // do not change order
        cntFullScreen = fullscreen_content
        super.onCreate(savedInstanceState)

        connectionsClient = Nearby.getConnectionsClient(this)

        addFragmentsThenFindSomeone()
    }

    private fun addFragmentsThenFindSomeone(){
        fragmentManager.beginTransaction()
                .add(R.id.bottom_container,buttons)
                .add(R.id.bottom_container,log)
                .add(R.id.main_container,ready)
                .add(R.id.main_container,go)
                .runOnCommit {findSomeone()}
                .commit()
    }

    private fun showLog(){ //TODO: slide animation
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(log)
                .hide(buttons)
                .commitAllowingStateLoss() // cannot guarantee execution before onPause()
    }

    private fun showButtons() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(buttons)
                .hide(log)
                .commitAllowingStateLoss()
    }

    private fun showReady() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(ready)
                .hide(go)
                .commitAllowingStateLoss()
    }

    private fun showGo() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(go)
                .hide(ready)
                .commitAllowingStateLoss()
    }

    private fun findSomeone(){
        showReady()
        signaText.text = signum
        adveText.setText(R.string.dots)
        log(R.string.scanning)

        connectionsClient!!.startAdvertising(
                signum, packageName, connectionLifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(STRATEGY).build())
        connectionsClient!!.startDiscovery(
                packageName, endpointDiscoveryCallback,
                DiscoveryOptions.Builder().setStrategy(STRATEGY).build())
    }

    @SuppressLint("SetTextI18n")
    private fun log(tag: Int) {
        logView.text = "${logView.text}${getText(tag)}\n"
        displayLog()
    }

    @SuppressLint("SetTextI18n")
    private fun debug(msg: String) {
        logView.text = "${logView.text}$msg\n"
        displayLog()
    }

    private fun displayLog() {
        logView.post{logContainer.smoothScrollTo(0, logView.bottom)}
        showLog()
    }

    // Callbacks for finding other devices
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            log(R.string.endpoint_found)
            connectionsClient!!.requestConnection(signum, endpointId, connectionLifecycleCallback)

        }

        override fun onEndpointLost(endpointId: String) {
            log(R.string.endpoint_lost)
        }
    }

    // Callbacks for finding other devices
    private val connectionLifecycleCallback = object :  ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            connectionsClient!!.stopDiscovery()
            connectionsClient!!.stopAdvertising()
            opponentEndpointId = endpointId
            adversatorSigna = connectionInfo.endpointName
            adveText.text = adversatorSigna
            enableButtons("connect")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode){
                ConnectionsStatusCodes.STATUS_OK -> {
                    log(R.string.connected)
                    gameStart()
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    log(R.string.connection_rejected)
                    findSomeone()
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    log(R.string.connection_failed)
                    findSomeone()
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            log(R.string.disconnected)
            reset()
            findSomeone()
        }
    }

    private fun reset() {
        opponentEndpointId = null
        adversatorSigna = null
        ludum!!.reset()
    }

    private fun enableButtons(state: String) {
        cancelBtn.visibility = View.VISIBLE
        acceptBtn.visibility = View.VISIBLE
        when (state) {
            "connect" -> {
                cancelBtn.setOnClickListener {
                    connectionsClient!!.rejectConnection(opponentEndpointId!!)
                    disableButtons()
                }
                acceptBtn.setOnClickListener {
                    connectionsClient!!.acceptConnection(opponentEndpointId!!, payloadCallback)
                    disableButtons()
                    log(R.string.connection_pending)
                }
            }
            "replay" -> {
                cancelBtn.setOnClickListener {
                    reset()
                    findSomeone()
                }
                acceptBtn.setOnClickListener {
                    gameRestart()
                }
            }
        }
        showButtons()
    }

    private fun disableButtons(){
        cancelBtn.setOnClickListener(null)
        acceptBtn.setOnClickListener(null)
        cancelBtn.visibility = View.INVISIBLE
        acceptBtn.visibility = View.INVISIBLE
        displayLog()
    }

    fun gameStart() {
        disableButtons()

        resultText.setText(R.string.upto5)
        ludum!!.maxScore = 3
        scoreText.text = "0:0"

        showGo()

        sensorHandler!!.registerListeners()
    }

    fun gameRestart() {
        ludum!!.reset()
        gameStart()
    }

    override fun updatePlayerView(caller: Player) {
        val status: Int = caller.state
        if(caller == usor) {
            if (status == R.integer.HIGH_ATTACK ){
                sendPayload(H_A_BYTE)
                debug("out: h_a")
            } else if (status == R.integer.LOW_ATTACK) {
                sendPayload(L_A_BYTE)
                debug("out: l_a")
            }
        }
    }

    private fun sendPayload(byte : Byte) {
        connectionsClient!!.sendPayload(opponentEndpointId!!,Payload.fromBytes(
                ByteArray(1) {return@ByteArray byte}
        ))
    }

    // Callbacks for receiving payloads
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val payloadByte = payload.asBytes()!![0]
            when (payloadByte){
                H_A_BYTE -> {
                    debug("in: h_a")
                    adversator!!.state = R.integer.HIGH_ATTACK
                    gameSync()
                }
                L_A_BYTE -> {
                    debug("in: l_a")
                    adversator!!.state = R.integer.LOW_ATTACK
                    gameSync()
                }
                DRAW_BYTE ->{
                    debug("in: draw")
                    ludum!!.state = R.integer.GAME_DRAW
                }
                SCORE_BYTE ->{
                    debug("in: score")
                    ludum!!.state = R.integer.GAME_P1
                }
            }
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
           when(update.status) {
                PayloadTransferUpdate.Status.CANCELED, PayloadTransferUpdate.Status.FAILURE -> {
                    log(R.string.payload_lost)
                    gameRestart()
                }
            }
        }
    }

    fun gameSync() {
        if(ludum!!.score2()) {
            sendPayload(SCORE_BYTE)
            debug("out: score")
        }
        else {
            sendPayload(DRAW_BYTE)
            debug("out: draw")
        }
    }

    override fun updateGameView(state: Int) { // do not call before onGo

        super.updateGameView(state)

        when(state){
            R.integer.GAME_DRAW -> {
                debug("game: draw")
            }
            R.integer.GAME_P1 -> {
                scoreText.text = ludum!!.toString()
                debug("game: p1")
            }
            R.integer.GAME_P2 -> {
                scoreText.text = ludum!!.toString()
                debug("game: p2")
            }
            R.integer.GAME_W1 -> {
                debug("game: w1")
                resultText.setText(R.string.won)
                gameEnd()
            }
            R.integer.GAME_W2 -> {
                debug("game: w1")
                resultText.setText(R.string.lost)
                gameEnd()
            }
        }
    }

    private fun gameEnd(){
        sensorHandler!!.unregisterListeners()
        enableButtons("replay")
    }


    override fun onPause() {
        connectionsClient?.stopAllEndpoints()
        connectionsClient?.stopAdvertising()
        connectionsClient?.stopDiscovery()
        super.onPause()
    }

}