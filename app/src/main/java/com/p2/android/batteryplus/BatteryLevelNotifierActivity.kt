package com.p2.android.batteryplus

import android.app.Notification
import android.app.NotificationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.BroadcastReceiver
import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import android.os.BatteryManager
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.app.NotificationCompat
import android.util.Log

/**
 * Periodically Check Battery Status
 * On Reaching the Desired Battery state , notify DONE
 */
class BatteryLevelNotifierActivity : AppCompatActivity() {

    private var mBatteryLevelText: TextView? = null
    private var mBatteryLevelProgress: ProgressBar? = null
    private var mReceiver: BroadcastReceiver? = null
    private var mNotification: Notification? = null
    private var mNotificationManager: NotificationManager? = null

    private val optimisedChargeOrMinEnd = 40
    private val optimisedChargeOrMaxEnd = 80

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBatteryLevelText = findViewById(R.id.batteryLevel)
        mBatteryLevelProgress = findViewById(R.id.batteryLevelBar)

        mReceiver = BatteryBroadcastReceiver()

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStart() {
        registerReceiver(mReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        super.onStart()
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    private inner class BatteryBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            Log.i("BroadcastReceiver", "Battery Change Event Received")

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)

            val batteryPct = (level / scale.toFloat()) * 100
            val batteryPctInt = batteryPct.toInt()

            Log.i("BroadcastReceiver", "Battery Percentage: " + batteryPctInt)

            mBatteryLevelText?.setText(getString(R.string.battery_level) + " " + batteryPctInt + "%")
            mBatteryLevelProgress?.setProgress(batteryPctInt)

            if(batteryPctInt == optimisedChargeOrMinEnd) {
                Log.i("BroadcastReceiver", "Time to Plug in the charger")
                initializeNotifier(optimisedChargeOrMinEnd, "Time to Plug in the charger")
                mNotificationManager?.notify(0, mNotification)
                //TODO: Vibrate and Notify
            }
            
            if(batteryPctInt == optimisedChargeOrMaxEnd){
                Log.i("BroadcastReceiver", "Time to Unplug the charger")
                initializeNotifier(optimisedChargeOrMaxEnd, "Time to Unplug in the charger")
                mNotificationManager?.notify(1, mNotification)
                //TODO: Vibrate and Notify
            }
        }
    }

    private fun initializeNotifier(chargePct: Int, tickerMsg: String){
        mNotification = NotificationCompat.Builder(this)
                .setVibrate(longArrayOf(0L, 2000L, 1000L, 2000L))
                .setTicker(chargePct.toString() + " Percentage Reached, " + tickerMsg)
                .build()
    }
}
