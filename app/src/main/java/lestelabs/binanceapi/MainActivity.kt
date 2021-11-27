package lestelabs.binanceapi


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.binance.api.client.BinanceApiClientFactory
import lestelabs.binanceapi.binance.api.client.BinanceApiRestClient
import lestelabs.binanceapi.binance.api.client.BinanceApiWebSocketClient
import lestelabs.binanceapi.binance.api.client.domain.account.Account
import lestelabs.binanceapi.binance.api.client.domain.event.AccountUpdateEvent
import lestelabs.binanceapi.binance.api.client.domain.event.OrderTradeUpdateEvent
import lestelabs.binanceapi.binance.api.client.domain.event.UserDataUpdateEvent
import lestelabs.binanceapi.binance.api.client.domain.market.CandlestickInterval
import lestelabs.binanceapi.charts.Charts
import lestelabs.binanceapi.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var spinner:Spinner
    private lateinit var textView1: TextView
    private val interval = CandlestickInterval.HOURLY
    private lateinit var binance: Binance
    private lateinit var restClient: BinanceApiRestClient
    private lateinit var webSocketClient: BinanceApiWebSocketClient
    // declaring variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

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


        init_binance()
        init_notification()
        init_spinner()
        init_listener_user_binance_updates()
        textView1 = findViewById(R.id.textHome)
    }

    fun init_binance() {
        binance = Binance(this)
        restClient = binance.initRestClient()
        val factory = BinanceApiClientFactory.newInstance("O6TtsJzwkJr2QsecVQZQNcM1KWjMKeSe6YqIFBCupGEDdP5OrwUDbQJJ3bQPDssO", "clZG1nQ5FDIcLuK0KsspwFUTzlg56Gsw6F4maYrxO8yJDcfxVUndHQfF5mPtfTBq")
        webSocketClient = factory.newWebSocketClient()
    }

    fun init_notification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun onSpinnerSelection(symbol: String) {



        val charts = Charts(this)
        val account: Account = restClient.account

        //Log.d(TAG,"binance balances " + account.balances)
        Log.d(TAG,"binance balance ADA " + account.getAssetBalance("ADA").free)
        //println(account.balances)
        //println(account.getAssetBalance("ADA").free)

        //val openOrders = client.getOpenOrders(OrderRequest("ADAEUR"))
        //Log.d(TAG, "binance open orders $openOrders")

        //val candlesticksCacheExample = CandlesticksCacheExample("ETHBTC", CandlestickInterval.ONE_MINUTE);
        //Log.d(TAG, "binance candle ADA $candlesticksCacheExample")

        val candleSticks = binance.getCandleSticks(restClient, symbol, interval)
        charts.printLinearGraph(findViewById(R.id.graphView1), candleSticks.first,candleSticks.second.first)
        charts.printLinearGraph(findViewById(R.id.graphView2), candleSticks.first,candleSticks.second.second)
        updateTextView(textView1, candleSticks.second)
    }

    fun init_spinner() {
        spinner = findViewById(R.id.spinner1)
        val items = arrayOf("ADAEUR", "BTCEUR", "ETHEUR", "DOGEEUR")
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        //set the spinners adapter to the previously created one.
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                onSpinnerSelection(items[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // vacio
            }
        }
    }

    fun updateTextView(textView: TextView, input: Pair<List<DoubleArray>, List<DoubleArray>>) {
        val value = input.first[0]
        val sma = input.first[1]
        val rsi = input.second[0]

        textView.text = "endPrice: " + value[value.size-1].toString() + " sma: " + String.format("%.5f", sma[sma.size-1]) + " rsi: " + String.format("%.5f", rsi[rsi.size-1])
    }


    fun init_listener_user_binance_updates() {

        // First, we obtain a listenKey which is required to interact with the user data stream

        // First, we obtain a listenKey which is required to interact with the user data stream
        val listenKey = restClient.startUserDataStream()

        webSocketClient.onUserDataUpdateEvent(listenKey) { response ->
            if (response.eventType === UserDataUpdateEvent.UserDataUpdateEventType.ACCOUNT_POSITION_UPDATE) {
                val accountUpdateEvent: AccountUpdateEvent = response.outboundAccountPositionUpdateEvent

                // Print new balances of every available asset
                println(accountUpdateEvent.balances)
            } else {
                val orderTradeUpdateEvent: OrderTradeUpdateEvent =
                    response.orderTradeUpdateEvent


                send_notification()

                // Print details about an order/trade
                Log.d(TAG, "binance orderTradeUpdateEvent $orderTradeUpdateEvent")

                // Print original quantity
                Log.d(TAG, "binance orderTradeUpdateEvent quantity " + orderTradeUpdateEvent.originalQuantity)

                // Or price
                Log.d(TAG, "binance orderTradeUpdateEvent price " + orderTradeUpdateEvent.price)
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
        //val contentView = RemoteViews(packageName, R.layout.activity_after_notification)

        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                //.setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                //.setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                //.setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                //.setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }
    companion object {
        const val TAG = "MainActivity"
        const val OFFSET = 50
    }


}
