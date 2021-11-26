package lestelabs.binanceapi

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import lestelabs.binanceapi.databinding.ActivityMainBinding

import lestelabs.binanceapi.binance.api.client.BinanceApiClientFactory
import lestelabs.binanceapi.binance.api.client.domain.account.Account
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner

import lestelabs.binanceapi.binance.api.client.domain.market.CandlestickInterval
import lestelabs.binanceapi.charts.Charts
import lestelabs.binanceapi.charts.Indicators
import java.lang.Exception
import android.widget.AdapterView
import android.widget.TextView
import com.github.mikephil.charting.components.YAxis
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.tools.Tools


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var spinner:Spinner
    private lateinit var textView1: TextView
    private val interval = CandlestickInterval.HOURLY

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

        init_spinner()

        textView1 = findViewById(R.id.textHome)
    }


    fun onSpinnerSelection(symbol: String) {

        val binance = Binance(this)
        val client = binance.initRestClient()
        val charts = Charts(this)
        val account: Account = client.account

        //Log.d(TAG,"binance balances " + account.balances)
        Log.d(TAG,"binance balance ADA " + account.getAssetBalance("ADA").free)
        //println(account.balances)
        //println(account.getAssetBalance("ADA").free)

        //val openOrders = client.getOpenOrders(OrderRequest("ADAEUR"))
        //Log.d(TAG, "binance open orders $openOrders")

        //val candlesticksCacheExample = CandlesticksCacheExample("ETHBTC", CandlestickInterval.ONE_MINUTE);
        //Log.d(TAG, "binance candle ADA $candlesticksCacheExample")

        val candleSticks = binance.getCandleSticks(client, symbol, interval)
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

    companion object {
        const val TAG = "MainActivity"
        const val OFFSET = 50
    }


}