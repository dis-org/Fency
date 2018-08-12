package disorganizzazione.fency

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler

class SensorHandler(context: FencyModeActivity, player: Player) : FencyHandler(context, player), SensorEventListener {

    private val attackMinLength: Long = 350000000 //nanoseconds
    private val y_bound = 5.0f
    private val roll_bound = 55
    private val pitch_upbound = -60
    private val pitch_midbound_up = -20
    private val pitch_midbound_low = 15
    private val pitch_lowbound = 60

    private val sensorFusion: SensorFusion = SensorFusion()
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorLinearAcceleration: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private var state: Int = 0
    private var yPrevious: Float = 0.toFloat()
    private var yDelta: Float = 0.toFloat()
    private var peak: Float = 0.toFloat()
    private var start: Boolean = false
    private var startingAttackTime: Long = 0
    private val SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME

    private val ATTACK_DURATION: Long = 500
    private var attacking = false

    init {
        sensorFusion.setMode(SensorFusion.Mode.GYRO)
    }

    fun registerListeners() {
        // Register 3 sensors for SensorFusion
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SENSOR_DELAY)

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SENSOR_DELAY)

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SENSOR_DELAY)

        // Register sensorLinearAcc
        sensorManager.registerListener(this, sensorLinearAcceleration, SENSOR_DELAY)

        //
        start = true
    }

    fun unregisterListeners() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {

        if (!attacking) {
            val id = event.sensor.type
            //handle SensorFusion cases
            when (id) {
                Sensor.TYPE_ACCELEROMETER -> {
                    sensorFusion.setAccel(event.values)
                    sensorFusion.calculateAccMagOrientation()
                }

                Sensor.TYPE_GYROSCOPE -> sensorFusion.gyroFunction(event)

                Sensor.TYPE_MAGNETIC_FIELD -> sensorFusion.setMagnet(event.values)
            }

            if (id == Sensor.TYPE_LINEAR_ACCELERATION) {

                val yCurrent = event.values[1]
                val pitch = sensorFusion.pitch
                val roll = sensorFusion.roll

                if (roll > roll_bound || roll < -roll_bound || pitch < pitch_upbound || pitch > pitch_lowbound) {
                    //("Invalid")
                    player.state = R.integer.INVALID
                } else if (pitch < pitch_midbound_up || player.state == R.integer.HIGH_ATTACK) {
                    //("High Guard")
                    player.state = R.integer.HIGH_STAND
                } else if (pitch > pitch_midbound_low || player.state == R.integer.LOW_ATTACK) {
                    //("Low Guard")
                    player.state = R.integer.LOW_STAND
                }

                if (start) {
                    // Initialize last y
                    yPrevious = yCurrent
                    start = false
                } else {
                    yDelta = yCurrent - yPrevious

                    when (state) {
                        0 -> if (yCurrent > y_bound && yDelta > 0
                                && player.state != R.integer.INVALID) {
                            //save the starting time of the (possible) attack
                            startingAttackTime = event.timestamp
                            //go to state 1
                            state = 1
                        } else if (yCurrent < -y_bound && yDelta < 0) {
                            //go to state -1
                            state = -1
                        }
                        1 ->
                            //waiting for positive spike
                            if (yDelta < 0) {
                                peak = yCurrent
                                //go to state 2
                                state = 2
                            }
                        2 ->
                            //waiting for negative spike
                            if (yDelta > 0) {
                                if (player.state != R.integer.INVALID &&
                                     peak + yCurrent < 0 &&
                                      event.timestamp - startingAttackTime > attackMinLength) {
                                    //ATTACK!
                                    attacking = true
                                    Handler().postDelayed({
                                        // Allow user to change state only after delay
                                        attacking = false
                                        onSensorChanged(event)
                                    }, ATTACK_DURATION)

                                    if (player.state == R.integer.LOW_STAND)
                                        player.state = R.integer.LOW_ATTACK
                                    else if (player.state == R.integer.HIGH_STAND)
                                        player.state = R.integer.HIGH_ATTACK

                                }
                                startingAttackTime = 0
                                peak = 0f
                                //go to state 0
                                state = 0
                            }
                        -1 ->
                            //waiting for negative spike
                            if (yDelta > 0) {
                                //go to state -2
                                state = -2
                            }
                        -2 ->
                            //waiting for positive spike
                            if (yDelta < 0) {
                                //back to state 0 (idle)
                                state = 0
                            }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}


}
