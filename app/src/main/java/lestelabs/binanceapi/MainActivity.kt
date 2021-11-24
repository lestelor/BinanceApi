package lestelabs.binanceapi

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import lestelabs.binanceapi.databinding.ActivityMainBinding
import lestelabs.binanceapi.binance.api.client.BinanceApiRestClient

import lestelabs.binanceapi.binance.api.client.BinanceApiClientFactory
import lestelabs.binanceapi.binance.api.client.domain.account.Account
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.jjoe64.graphview.GraphView
import lestelabs.binanceapi.binance.api.client.domain.account.request.OrderRequest

import lestelabs.binanceapi.binance.api.client.domain.account.Order
import lestelabs.binanceapi.binance.api.client.domain.market.Candlestick
import lestelabs.binanceapi.binance.api.client.domain.market.CandlestickInterval
import lestelabs.binanceapi.binance.examples.CandlesticksCacheExample
import lestelabs.binanceapi.calculations.Charts
import lestelabs.binanceapi.calculations.Indicators
import java.lang.Exception
import java.util.*
import android.widget.AdapterView
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var spinner:Spinner
    private lateinit var textView1: TextView

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
                init_binance(items[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // vacio
            }
        }

        textView1 = findViewById(R.id.textHome)
    }


    fun init_binance(symbol: String) {
        val factory = BinanceApiClientFactory.newInstance("O6TtsJzwkJr2QsecVQZQNcM1KWjMKeSe6YqIFBCupGEDdP5OrwUDbQJJ3bQPDssO", "clZG1nQ5FDIcLuK0KsspwFUTzlg56Gsw6F4maYrxO8yJDcfxVUndHQfF5mPtfTBq")
        val client = factory.newRestClient()

        val account: Account = client.account
        //Log.d(TAG,"binance balances " + account.balances)
        Log.d(TAG,"binance balance ADA " + account.getAssetBalance("ADA").free)
        //println(account.balances)
        //println(account.getAssetBalance("ADA").free)

        //val openOrders = client.getOpenOrders(OrderRequest("ADAEUR"))
        //Log.d(TAG, "binance open orders $openOrders")

        //val candlesticksCacheExample = CandlesticksCacheExample("ETHBTC", CandlestickInterval.ONE_MINUTE);
        //Log.d(TAG, "binance candle ADA $candlesticksCacheExample")


        val interval = CandlestickInterval.HOURLY


        try {
            //val candleStickBars = DoubleArray(500)
            val candleStickBars = client.getCandlestickBars(symbol.toUpperCase(), interval)

            val candlesticksClosePrice= DoubleArray(candleStickBars.size)
            val candlesticksDate= LongArray(candleStickBars.size)

            for (i in 0 .. candleStickBars.size-1) {
                candlesticksClosePrice[i] = candleStickBars[i].eClose.toDouble()
                candlesticksDate[i] = candleStickBars[i].gCloseTime
                //candlesticksClosePrice[i] = i.toDouble()
                //candlesticksDate[i] = i.toDouble()
            }


            val xAxis: LongArray = candlesticksDate
            val yAxis: MutableList<DoubleArray> = mutableListOf()
            yAxis.add(candlesticksClosePrice)
            yAxis.add (Indicators.movingAverage(candlesticksClosePrice,20))
            Charts().linearChart(findViewById(R.id.graphView1), xAxis, yAxis, 20)

            yAxis.add(Indicators.rsi(candlesticksClosePrice,20))
            Charts().linearChart(findViewById(R.id.graphView2), xAxis, mutableListOf(yAxis[2]), 20)

            textView1.text = "sma: " + yAxis[1][yAxis[1].size-1].toString() + " rsi: " + yAxis[2][yAxis[2].size-1].toString()

            //Charts().setBarChart(findViewById(R.id.idBarChart))
        } catch (e:Exception) {
            Log.d(TAG, "binance candlestick error $e")
        }


        //Log.d(TAG, "binance candle ADA " + candlestickBars[0])
/*        val candlesticksCache = TreeMap<Long, Candlestick>()
        for (candlestickBar in candlestickBars) {
            candlesticksCache[candlestickBar.aOpenTime] = candlestickBar
        }*/





    }

    companion object {
        const val TAG = "MainActivity"
    }


}