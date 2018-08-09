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

class DuelModeActivity: FencyModeActivity(){

    val signum = TriaNomina().toString()
    private var adversatorSigna: String? = null

    private var connectionsClient: ConnectionsClient? = null
    private var opponentEndpointId: String? = null

    // Callbacks for receiving payloads
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val newState = payload.toString().toInt() //TODO : probabilmente non ha senso
            Toast.makeText(applicationContext, "stato = $newState",Toast.LENGTH_SHORT).show()
            adversator!!.state = newState
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) ludum!!.update()
        }
    }

    // Callbacks for finding other devices
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient!!.requestConnection(signum, endpointId, connectionLifecycleCallback)
            Toast.makeText(applicationContext, "onEndpointFound", Toast.LENGTH_SHORT).show()//TODO: remove
        }

        override fun onEndpointLost(endpointId: String) {}
    }

    // Callbacks for finding other devices
    private val connectionLifecycleCallback = object :  ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            connectionsClient!!.acceptConnection(endpointId, payloadCallback)
            adversatorSigna = connectionInfo.endpointName

            Toast.makeText(applicationContext, "onConnectionInitiated", Toast.LENGTH_SHORT).show()//TODO: remove
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            connectionsClient!!.stopDiscovery()
            connectionsClient!!.stopAdvertising()

            opponentEndpointId = endpointId
            statusText.text = getString(R.string.connected)
            adveText.text = adversatorSigna

            Toast.makeText(applicationContext, "onConnectionResult", Toast.LENGTH_SHORT).show()//TODO: remove
        }

        override fun onDisconnected(endpointId: String) {
            resetGame()

            Toast.makeText(applicationContext, "onDisconnected", Toast.LENGTH_SHORT).show()//TODO: remove
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

    override fun onStart() {
        super.onStart()

        if (android.os.Build.VERSION.SDK_INT >= 23)
            if (!hasPermissions(this, *REQUIRED_PERMISSIONS)) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS)
            }
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
        //TODO
        if(opponentEndpointId != null && caller == usor) {
            if (caller.state == R.integer.HIGH_ATTACK || caller.state == R.integer.LOW_ATTACK) {
                connectionsClient!!.sendPayload(
                    opponentEndpointId!!, Payload.fromBytes(ByteArray(caller.state)))
            }
        }

    }

    override fun updateGameView(gameState: Int) {
        super.updateGameView(gameState)
        statusText.text = ludum!!.state.toString()
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