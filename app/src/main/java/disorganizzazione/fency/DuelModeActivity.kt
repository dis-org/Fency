package disorganizzazione.fency

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.CallSuper
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

import kotlinx.android.synthetic.main.activity_duel_mode.*

class DuelModeActivity: FencyModeActivity(){

    val signum = TriaNomina().toString()
    private var adversatorSigna: String? = null

    private var connectionsClient: ConnectionsClient? = null
    private var opponentEndpointId: String? = null

    // Callbacks for receiving payloads
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            //opponentChoice = GameChoice.valueOf(String(payload.asBytes()!!, UTF_8))
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            //if (update.status == Status.SUCCESS && myChoice != null && opponentChoice != null)  finishRound()
        }
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
            connectionsClient!!.acceptConnection(endpointId, payloadCallback)
            adversatorSigna = connectionInfo.endpointName
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            connectionsClient!!.stopDiscovery()
            connectionsClient!!.stopAdvertising()

            opponentEndpointId = endpointId
            statusText.text = getString(R.string.connected)
            adveText.text = adversatorSigna
        }

        override fun onDisconnected(endpointId: String) {
            resetGame()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_duel_mode) // do not change order
        cntFullScreen = fullscreen_content
        super.onCreate(savedInstanceState)

        //PROVA FRAGMENT
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = ConnectionFragment()
        fragmentTransaction.add(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
        ////

        signaText.text = signum
        connectionsClient = Nearby.getConnectionsClient(this)
        connectionsClient!!.startAdvertising(
                signum, packageName, connectionLifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(STRATEGY).build())
        connectionsClient!!.startDiscovery(
                packageName, endpointDiscoveryCallback,
                DiscoveryOptions.Builder().setStrategy(STRATEGY).build())

    }

    override fun onStop() {
        connectionsClient!!.stopAllEndpoints()
        resetGame()

        super.onStop()
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

        statusText.text = getString(R.string.disconnected)
        adveText.text = ""
    }

    override fun updatePlayerView(caller: Player) {
    }

    companion object {
        private val STRATEGY = Strategy.P2P_STAR
        private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1

    }
}