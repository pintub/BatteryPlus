package com.p2.android.batteryplus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.BroadcastReceiver
import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import android.os.BatteryManager
import android.content.Intent
import android.content.IntentFilter
import android.util.Log


class MainActivity : AppCompatActivity() {
    /**
     * exitBatteryPect - Min % till which BroadcastReceiver listens for ACTION_BATTERY_CHANGED
     */
    private val exitBatteryPect = 1

    /**
     * percentageRange - The Range of Battery Percentage for Charging and Discharging
     * Objective- To find the Min / Max End of this range , which would increase Battery Full life cycle
     */
    private val percentageRange = 40

    private var mBatteryLevelText: TextView? = null
    private var mBatteryLevelProgress: ProgressBar? = null
    private var mReceiver: BroadcastReceiver? = null

    /*timeStampArrayList - stores Timestamp for Battery % from 100 till 10 ,e.g. : timeStampArrayList[0] = TimeStamp when 100% reached
    timeStampArrayList[1] = TimeStamp when 99% reached
    timeStampArrayList[2] = TimeStamp when 98% reached
    timeStampArrayList[89] = TimeStamp when 11% reached
    timeStampArrayList[90] = TimeStamp when 10% reached*/
    private var timeStampArrayList = ArrayList<Long>(100)

    private var currentBatteryPect = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBatteryLevelText = findViewById(R.id.batteryLevel);
        mBatteryLevelProgress = findViewById(R.id.batteryLevelBar);

        mReceiver = BatteryBroadcastReceiver()
    }

    override fun onStart() {
        registerReceiver(mReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        super.onStart()
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)

        //Process timeStampArrayList i.e. Process (100 - percentageRange) Ranges example - (100-60) , (99-59) .... (50-10)
        var shortestTimeForChargeCycle = 0L
        var shortestTimeForChargeCycleMaxEnd = 100
        var shortestTimeForChargeCycleMinEnd = 100 - percentageRange

        var i = 0
        while(i < (100 - percentageRange - exitBatteryPect)){
            var timeForChargeCycle = timeStampArrayList.get(i + percentageRange - 1) - timeStampArrayList.get(i)
            if(shortestTimeForChargeCycle > timeForChargeCycle) {
                shortestTimeForChargeCycle = timeForChargeCycle
                shortestTimeForChargeCycleMaxEnd = i + percentageRange
                shortestTimeForChargeCycleMinEnd = i + 1
            }
            i++
        }

        Log.i("FinalLog", "Optimised Battery Discharge Cycle Time for " + percentageRange + "% : " + shortestTimeForChargeCycle % 60 + " minutes")
        Log.i("FinalLog", "Max End for Optimised Battery Discharge Cycle Time for " + percentageRange + "% : "+ shortestTimeForChargeCycleMaxEnd )
        Log.i("FinalLog", "Min End for Optimised Battery Discharge Cycle Time for " + percentageRange + "% : "+ shortestTimeForChargeCycleMinEnd )

        timeStampArrayList.clear()
        super.onDestroy()
    }

    private inner class BatteryBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            Log.i("BroadcastReceiver", "Battery Change Event Received")

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
            //Log.i("Battery Level", "Level: " + level + "," + "Scale: " + scale)

            val batteryPct = (level / scale.toFloat()) * 100
            val batteryPctInt = batteryPct.toInt()

            Log.i("BroadcastReceiver", "Battery Percentage: " + batteryPctInt)

            mBatteryLevelText?.setText(getString(R.string.battery_level) + " " + batteryPctInt + "%")
            mBatteryLevelProgress?.setProgress(batteryPctInt)

            if(batteryPctInt == currentBatteryPect - 1) {
                timeStampArrayList.add(System.currentTimeMillis())
                currentBatteryPect = batteryPctInt
                Log.i("BroadcastReceiver", "Array List Items: " + timeStampArrayList)
            }
            
            if(currentBatteryPect == exitBatteryPect){
                this@MainActivity.finish()
            }
        }
    }
}
