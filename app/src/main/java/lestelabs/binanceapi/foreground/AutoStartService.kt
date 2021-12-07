package lestelabs.binanceapi.foreground

import android.R
import android.app.Service
import android.content.Intent
import android.os.Build

import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import java.util.*


class AutoStartService: Service() {
    var counter = 0
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: Service is destroyed :( ")
        val broadcastIntent = Intent(this, RestartBroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
        stoptimertask()
    }

    private fun startTimer() {
        timer = Timer()

        //initialize the TimerTask's job
        initialiseTimerTask()

        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, 1000, 1000) //
    }

    private fun initialiseTimerTask() {
        timerTask = object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                Log.i(TAG, "Timer is running " + counter++)
            }
        }
    }

    private fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    companion object {
        private const val TAG = "AutoService"
    }

    init {
        Log.i(TAG, "AutoStartService: Here we go.....")
    }
}