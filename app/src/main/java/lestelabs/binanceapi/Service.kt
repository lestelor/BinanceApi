package lestelabs.binanceapi


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.data.streams.datasource.Candlestick
import lestelabs.binanceapi.tools.Globals
import lestelabs.binanceapi.ui.notifications.Notification
import lestelabs.binanceapi.ui.notifications.Notifications
import java.util.*


private val TAG = "Service"
private var mCurrentService: Service? = null
private var counter: Long = 0
val binance = Binance()


open class Service: android.app.Service() {

    private val NOTIFICATION_ID = 1337

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground()
        }
        mCurrentService = this
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("sharedpreferences", Context.MODE_PRIVATE)
        counter  = sharedPreferences.getLong("counter", 0)

        Log.d(TAG, "restarting Service timer counter: $counter !!")
        //counter = 0

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            val bck = ProcessMainClass()
            bck.launchService(this)
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground()
        }
        startTimer()

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY
    }


    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground")
            try {
                val notification = Notification()
                startForeground(
                    NOTIFICATION_ID,
                    notification.setNotification(
                        this,
                        "Service notification",
                        "This is the service's notification",
                        R.drawable.ic_eye
                    )
                )
                Log.i(TAG, "restarting foreground successful")
                startTimer()
            } catch (e: Exception) {
                Log.e(TAG, "Error in notification " + e.message)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy called")
        saveCounter()
        // restart the never ending service
        val broadcastIntent = Intent(Globals().RESTART_INTENT)
        sendBroadcast(broadcastIntent)
        stoptimertask()
    }


    /**
     * this is called when the process is killed by Android
     *
     * @param rootIntent
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.i(TAG, "onTaskRemoved called")
        saveCounter()
        // restart the never ending service
        val broadcastIntent = Intent(Globals().RESTART_INTENT)
        sendBroadcast(broadcastIntent)
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }


    /**
     * static to avoid multiple timers to be created when the service is called several times
     */
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    var oldTime: Long = 0

    @DelicateCoroutinesApi
    fun startTimer() {
        Log.i(TAG, "Starting timer")

        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask()
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask(this)
        Log.i(TAG, "Scheduling timer... ${binance.intervalms}")
        //schedule the timer, to wake up every 1 minute
        timer!!.schedule(timerTask, 60000, 60000) //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    @DelicateCoroutinesApi
    fun initializeTimerTask(service: Service) {
        Log.i("in timer", "timer init notifications")
        val notifications: Notifications = Notifications(service)
        var candlesticks: List<Candlestick> = listOf()
        val zeroLong: Long = 0
        notifications.initNotifications()
        Log.i(TAG, "initialising TimerTask")
        timerTask = object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                Log.i("in timer", "in timer ++++  " + counter++)
                //notifications.sendNotification("in timer $counter")
                if (counter*60000 % binance.intervalms  == zeroLong) {
                    Log.i("in timer", "timer send notification")
                    GlobalScope.launch(Dispatchers.IO) {
                        candlesticks = binance.getCandlesticks()
                        notifications.checkIfSendBuySellNotification(candlesticks)
                    }

                }
            //
            }
        }
    }

    /**
     * not needed
     */
    fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun saveCounter() {
        if (counter > Long.MAX_VALUE/2) counter = 0
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("sharedpreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong("counter", counter)
        editor.commit()
    }

}