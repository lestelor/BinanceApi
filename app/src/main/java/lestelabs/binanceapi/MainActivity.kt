package lestelabs.binanceapi


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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


interface RetrieveDataInterface {
    fun retrieveDataInterface():Binance
}


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
    lateinit var binanceKeepAlive: Runnable


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
        init_notification()
        init_listener_user_binance_updates()

    }


    fun init_binance() {
        binance = Binance()
    }

    fun init_notification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


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

        binanceKeepAlive = object : Runnable {
            override fun run() {
                binance.syncClient.keepAliveUserDataStream(listenKey);
                mainHandler.postDelayed(this, binance.keepAlive)
                Log.d(TAG, "binance keep alive")
            }
        }

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





override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(binanceKeepAlive)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(binanceKeepAlive)
    }

    companion object {
        const val TAG = "MainActivity"
    }

    override fun retrieveDataInterface(): Binance {
        return binance
    }


}