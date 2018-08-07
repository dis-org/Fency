package disorganizzazione.fency

import android.os.Bundle
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

import kotlinx.android.synthetic.main.activity_duel_mode.*


class DuelModeActivity: FencyModeActivity(){

    val signum = TriaNomina().toString()
    private var adversatorSigna: String? = null

    private var connectionsClient: ConnectionsClient? = null
    private var opponentEndpointId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_duel_mode) // do not change order
        cntFullScreen = fullscreen_content
        super.onCreate(savedInstanceState)


        signaText.text = signum
        connectionsClient = Nearby.getConnectionsClient(this)
        connectionsClient!!.startAdvertising(
                signum, packageName, connectionLifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(STRATEGY).build())
        connectionsClient!!.startDiscovery(
                packageName, endpointDiscoveryCallback,
                DiscoveryOptions.Builder().setStrategy(STRATEGY).build())

    }

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
    }
}