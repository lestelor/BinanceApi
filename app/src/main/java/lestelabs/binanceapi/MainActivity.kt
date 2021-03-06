package lestelabs.binanceapi


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.binance.api.client.domain.event.AccountUpdateEvent
import lestelabs.binanceapi.binance.api.client.domain.event.OrderTradeUpdateEvent
import lestelabs.binanceapi.binance.api.client.domain.event.UserDataUpdateEvent
import lestelabs.binanceapi.databinding.ActivityMainBinding

import android.content.Intent
import android.app.ActivityManager
import android.net.Uri
import android.os.Build
import lestelabs.binanceapi.foreground.RestartBroadcastReceiver
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.fragment_notifications.*
import lestelabs.binanceapi.ui.home.HomeFragment
import lestelabs.binanceapi.ui.notifications.NotificationsFragment


interface RetrieveDataInterface {
    fun retrieveDataInterface():Binance
}

private var repetition = 0

class MainActivity : AppCompatActivity(), RetrieveDataInterface {

    private lateinit var binding: ActivityMainBinding


    // declaring variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"




    private lateinit var binance: Binance
    lateinit var mainHandler: Handler
    //lateinit var binanceKeepAlive: Runnable

    //private lateinit var mServiceIntent: Intent


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        mainHandler = Handler(Looper.getMainLooper())
        init_binance()
        //init_notification()
        //init_listener_user_binance_updates()
        //init_battery_settings()
        //init_broadcast_service()

    }


    fun init_binance() {
        binance = Binance()
    }

/*    fun init_notification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }*/


    fun init_listener_user_binance_updates() {

        // First, we obtain a listenKey which is required to interact with the user data stream

        // First, we obtain a listenKey which is required to interact with the user data stream
        val listenKey = binance.syncClient.startUserDataStream()


        binance.webSocketClient.onUserDataUpdateEvent(listenKey) { response ->
            if (response.eventType === UserDataUpdateEvent.UserDataUpdateEventType.ACCOUNT_POSITION_UPDATE) {
                val accountUpdateEvent: AccountUpdateEvent =
                    response.outboundAccountPositionUpdateEvent

                // Print new balances of every available asset
                println(accountUpdateEvent.balances)
            } else {
                val orderTradeUpdateEvent: OrderTradeUpdateEvent =
                    response.orderTradeUpdateEvent

                send_notification()

                // Print details about an order/trade
                Log.d(TAG, "binance orderTradeUpdateEvent $orderTradeUpdateEvent")

                // Print original quantity
                Log.d(
                    TAG,
                    "binance orderTradeUpdateEvent quantity " + orderTradeUpdateEvent.originalQuantity
                )

                // Or price
                Log.d(TAG, "binance orderTradeUpdateEvent price " + orderTradeUpdateEvent.price)
            }
        }

        // First keep alive
        binance.syncClient.keepAliveUserDataStream(listenKey);

        /*binanceKeepAlive = object : Runnable {
            override fun run() {
                binance.syncClient.keepAliveUserDataStream(listenKey);
                mainHandler.postDelayed(this, binance.keepAlive)
                Log.d(TAG, "binance keep alive")
            }
        }*/

        // call every 15min
        //client.keepAliveUserDataStream(listenKey);
        // call onclose
        //client.closeUserDataStream(listenKey);

        /*client2.onAggTradeEvent(symbol.toLowerCase(), object : BinanceApiCallback<AggTradeEvent?> {
            override fun onFailure(cause: Throwable) {
                System.err.println("Web socket failed")
                cause.printStackTrace(System.err)
            }
            override fun onResponse(response: AggTradeEvent?) {
            Log.d(TAG, "binance websocket aggtradeevents $response")
            }
        })*/

    }

    fun send_notification() {
        // pendingIntent is an intent for future use i.e after
        // the notification is clicked, this intent will come into action

        //val intent = Intent(this, afterNotification::class.java)

        // FLAG_UPDATE_CURRENT specifies that if a previous
        // PendingIntent already exists, then the current one
        // will update it with the latest intent
        // 0 is the request code, using it later with the
        // same method again will get back the same pending
        // intent for future reference
        // intent passed here is to our afterNotification class
        //val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // RemoteViews are used to use the content of
        // some different layout apart from the current activity layout
        val contentView = RemoteViews(packageName, R.layout.activity_after_notification)
        contentView.setTextViewText(R.id.tvNotification, "cadcsddrgethrthrtr")

        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)



            builder = Notification.Builder(this, channelId)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
            //.setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
            //.setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun init_battery_settings() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    fun init_broadcast_service() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RestartBroadcastReceiver().scheduleJob(applicationContext)
        } else {
            val bck = ProcessMainClass()
            bck.launchService(applicationContext)
        }
    }


    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.i("isMyServiceRunning?", false.toString() + "")
        return false
    }



    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Timer Activity OnPause")
        //mainHandler.removeCallbacks(binanceKeepAlive)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Timer Activity OnResume repetition $repetition")
/*        if (repetition !=0) {
            NotificationsFragment().init_list(view = binding.root)
        }*/
        repetition += 1
        //mainHandler.post(binanceKeepAlive)

    }

    override fun onDestroy() {
        super.onDestroy()
        //stopService(mServiceIntent)
    }

    companion object {
        const val TAG = "MainActivity"
    }

    override fun retrieveDataInterface(): Binance {
        return binance
    }


}