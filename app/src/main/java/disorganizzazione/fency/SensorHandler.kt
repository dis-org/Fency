package disorganizzazione.fency

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class SensorHandler(context: FencyModeActivity, player: Player) : FencyHandler(context, player), SensorEventListener {

    fun registerListeners() {

    }

    fun unregisterListeners() {

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int){}

    override fun onSensorChanged(p0: SensorEvent?) {
        TODO("Not done.")
    }

}