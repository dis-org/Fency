package disorganizzazione.fency

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

import kotlinx.android.synthetic.main.activity_duel_mode.*
import kotlinx.android.synthetic.main.fragment_ready.*

class DuelModeActivity: FencyModeActivity(){

    val signum = TriaNomina().toString()
    private var adversatorSigna: String? = null

    private var connectionsClient: ConnectionsClient? = null
    private var opponentEndpointId: String? = null

    // Callbacks for receiving payloads
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val payloadByte = payload.asBytes()!![0]
            when (payloadByte){
                H_A_BYTE -> {
                    adversator!!.state = R.integer.HIGH_ATTACK
                    gameSync()
                }
                L_A_BYTE -> {
                    adversator!!.state = R.integer.LOW_ATTACK
                    gameSync()
                }
                DRAW_BYTE -> ludum!!.state = R.integer.GAME_DRAW
                SCORE_BYTE -> ludum!!.state = R.integer.GAME_P1
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    fun gameSync() {
        if(ludum!!.score2())
            sendPayload(SCORE_BYTE)
        else
            sendPayload(DRAW_BYTE)
    }

    private fun sendPayload(byte : Byte) {
        connectionsClient!!.sendPayload(opponentEndpointId!!,Payload.fromBytes(
                ByteArray(1) {return@ByteArray byte}
        ))

    }

    // Callbacks for finding other devices
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient!!.requestConnection(signum, endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {}
    }

    // Callbacks for finding other devices
    private val connectionLifecycleCallback = object :  ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {

            opponentEndpointId = endpointId
            adversatorSigna = connectionInfo.endpointName
            adveText.text = adversatorSigna

            onSomeoneFound()
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {

            when (result.status.statusCode){
                ConnectionsStatusCodes.STATUS_OK -> {
                    connectionsClient!!.stopDiscovery()
                    connectionsClient!!.stopAdvertising()

                    sensorHandler!!.registerListeners()

                    makeToast("Connected to opponent")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    //TODO
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    //TODO
                    makeToast("Connection attempt failed")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            resetGame()
        }
    }

    private fun onSomeoneFound(){
        cancelBtn.setOnClickListener {
            connectionsClient!!.rejectConnection(opponentEndpointId!!)
            disableButtons()
        }
        acceptBtn.setOnClickListener {
            connectionsClient!!.acceptConnection(opponentEndpointId!!, payloadCallback)
        }
    }
    private fun disableButtons(){
        cancelBtn.setOnClickListener{null}
        acceptBtn.setOnClickListener(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_duel_mode) // do not change order
        cntFullScreen = fullscreen_content
        super.onCreate(savedInstanceState)

        // Fragments handling
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = ReadyFragment()
        fragmentTransaction.add(R.id.fragment_container, fragment)
        fragmentTransaction.commit()

        connectionsClient = Nearby.getConnectionsClient(this)
        connectionsClient!!.startAdvertising(
                signum, packageName, connectionLifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(STRATEGY).build())
        connectionsClient!!.startDiscovery(
                packageName, endpointDiscoveryCallback,
                DiscoveryOptions.Builder().setStrategy(STRATEGY).build())

    }

    fun onReady() {
        // TODO
        signaText.text = signum
    }

    override fun onStart() {
        super.onStart()

        if (android.os.Build.VERSION.SDK_INT >= 23)
            if (!hasPermissions(this, *REQUIRED_PERMISSIONS)) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS)
            }
    }

    override fun onPause() {
        connectionsClient?.stopAllEndpoints()
        resetGame()

        super.onPause()
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

    private fun resetGame() {
        opponentEndpointId = null
        adversatorSigna = null

        adveText.text = "..." //TODO
    }

    override fun updatePlayerView(caller: Player) {
        super.updatePlayerView(caller)
        val status: Int = caller.state
        if(caller == usor) {
            if (status == R.integer.HIGH_ATTACK ){
                sendPayload(H_A_BYTE)
            } else if (status == R.integer.LOW_ATTACK) {
                sendPayload(L_A_BYTE)
            }
        }
    }

    override fun updateGameView() {
        super.updateGameView()
        val scoreString = "${ludum!!.score1}:${ludum!!.score2}"

        //TODO scoreText.text = scoreString
    }

    companion object {
        private val STRATEGY = Strategy.P2P_STAR
        private const val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
        private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION)

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
}