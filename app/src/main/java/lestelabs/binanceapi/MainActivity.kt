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
import lestelabs.binanceapi.binance.api.client.domain.account.request.OrderRequest

import lestelabs.binanceapi.binance.api.client.domain.account.Order
import lestelabs.binanceapi.binance.api.client.domain.market.Candlestick
import lestelabs.binanceapi.binance.api.client.domain.market.CandlestickInterval
import lestelabs.binanceapi.binance.examples.CandlesticksCacheExample
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
    }

    fun init_binance() {
        val factory = BinanceApiClientFactory.newInstance("O6TtsJzwkJr2QsecVQZQNcM1KWjMKeSe6YqIFBCupGEDdP5OrwUDbQJJ3bQPDssO", "clZG1nQ5FDIcLuK0KsspwFUTzlg56Gsw6F4maYrxO8yJDcfxVUndHQfF5mPtfTBq")
        val client = factory.newRestClient()

        val account: Account = client.account
        //Log.d(TAG,"binance balances " + account.balances)
        Log.d(TAG,"binance balance ADA" + account.getAssetBalance("ADA").free)
        //println(account.balances)
        //println(account.getAssetBalance("ADA").free)

        //val openOrders = client.getOpenOrders(OrderRequest("ADAEUR"))
        //Log.d(TAG, "binance open orders $openOrders")

        //val candlesticksCacheExample = CandlesticksCacheExample("ETHBTC", CandlestickInterval.ONE_MINUTE);
        //Log.d(TAG, "binance candle ADA $candlesticksCacheExample")

        val symbol = "ADAEUR"
        val interval = CandlestickInterval.HOURLY
        val candlestickBars = client.getCandlestickBars(symbol.toUpperCase(), interval)

        Log.d(TAG, "binance candle ADA " + candlestickBars[0])
        val candlesticksCache = TreeMap<Long, Candlestick>()
        for (candlestickBar in candlestickBars) {
            candlesticksCache[candlestickBar.aOpenTime] = candlestickBar
        }

    }

    companion object {
        const val TAG = "MainActivity"
    }
}