package com.p2.android.batteryplus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created by I335831 on 2/27/2018.
 * Objective - To test Activity Destroy() method
 */
class FinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        Log.i("FinsishAct: ", "Activity Started")
        killActivity()
    }

    override fun onDestroy() {
        Log.i("FinsishAct: ", "Activity Destroyed")
        super.onDestroy()
    }
    private fun killActivity(){
        this.finish()
    }

}