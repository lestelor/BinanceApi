package lestelabs.binanceapi.binance

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import com.jjoe64.graphview.GraphView
import lestelabs.binanceapi.MainActivity
import lestelabs.binanceapi.R
import lestelabs.binanceapi.binance.api.client.BinanceApiAsyncRestClient
import lestelabs.binanceapi.binance.api.client.BinanceApiClientFactory
import lestelabs.binanceapi.binance.api.client.BinanceApiRestClient
import lestelabs.binanceapi.binance.api.client.BinanceApiWebSocketClient
import lestelabs.binanceapi.binance.api.client.domain.market.CandlestickInterval
import lestelabs.binanceapi.charts.Charts
import lestelabs.binanceapi.charts.Indicators
import java.lang.Exception

class Binance() {

    val factory: BinanceApiClientFactory = initFactory()
    val syncClient: BinanceApiRestClient = initRestClient(factory)
    val asyncClient: BinanceApiAsyncRestClient = initAsyncClient(factory)
    val webSocketClient: BinanceApiWebSocketClient = initWebSocketClient()
    val offset = 50
    val sticks = arrayOf("ADAEUR", "BTCEUR", "ETHEUR", "DOGEEUR")
    val interval = CandlestickInterval.HOURLY


    private fun initFactory(): BinanceApiClientFactory {
        return BinanceApiClientFactory.newInstance("O6TtsJzwkJr2QsecVQZQNcM1KWjMKeSe6YqIFBCupGEDdP5OrwUDbQJJ3bQPDssO", "clZG1nQ5FDIcLuK0KsspwFUTzlg56Gsw6F4maYrxO8yJDcfxVUndHQfF5mPtfTBq")
    }
    private fun initRestClient(factory: BinanceApiClientFactory):BinanceApiRestClient {
        return factory.newRestClient()
    }
    private fun initAsyncClient(factory: BinanceApiClientFactory): BinanceApiAsyncRestClient {
        return factory.newAsyncRestClient()
    }
    private fun initWebSocketClient(): BinanceApiWebSocketClient {
        return factory.newWebSocketClient()
    }


    fun getCandleSticksSync(symbol:String): Pair<List<Long>,Pair<MutableList<DoubleArray>, MutableList<DoubleArray>>> {
        try {
            val client = syncClient
            //val candleStickBars = DoubleArray(500)
            val candleStickBars = client.getCandlestickBars(symbol.toUpperCase(), interval)

            val candlesticksClosePrice = DoubleArray(candleStickBars.size)
            val rsiMin= DoubleArray(candleStickBars.size - offset)
            val rsiMax= DoubleArray(candleStickBars.size- offset)
            val candlesticksDate =  LongArray(candleStickBars.size)

            for (i in 0 .. candleStickBars.size-1) {
                candlesticksClosePrice[i] = candleStickBars[i].eClose.toDouble()
                candlesticksDate[i] = candleStickBars[i].gCloseTime
                //candlesticksClosePrice[i] = i.toDouble()
                //candlesticksDate[i] = i.toDouble()
            }
            for (i in 0 .. candleStickBars.size-1- offset) {
                rsiMin[i] = 30.0
                rsiMax[i] = 70.0
            }
            val xAxis: List<Long> = candlesticksDate.drop(offset)
            val yAxisSma: MutableList<DoubleArray> = mutableListOf()
            val yAxisRsi: MutableList<DoubleArray> = mutableListOf()

            yAxisSma.add(candlesticksClosePrice.toMutableList().drop(offset).toDoubleArray())
            yAxisSma.add (Indicators.movingAverage(candlesticksClosePrice, offset))
            yAxisRsi.add(Indicators.rsi(candlesticksClosePrice, offset))
            yAxisRsi.add(rsiMin)
            yAxisRsi.add(rsiMax)
            return Pair(xAxis, Pair(yAxisSma,yAxisRsi))
            //Charts().setBarChart(findViewById(R.id.idBarChart))
        } catch (e: Exception) {
            Log.d(MainActivity.TAG, "binance candlestick print charts error $e")
            return Pair(listOf(), Pair(mutableListOf(), mutableListOf()))
        }
    }




}