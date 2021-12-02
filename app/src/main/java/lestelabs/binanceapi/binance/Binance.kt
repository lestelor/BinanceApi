package lestelabs.binanceapi.binance

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import lestelabs.binanceapi.MainActivity
import lestelabs.binanceapi.binance.api.client.*
import lestelabs.binanceapi.binance.api.client.domain.account.Order
import lestelabs.binanceapi.binance.api.client.domain.account.request.CancelOrderRequest
import lestelabs.binanceapi.binance.api.client.domain.account.request.OrderRequest
import lestelabs.binanceapi.binance.api.client.domain.market.CandlestickInterval
import lestelabs.binanceapi.charts.Indicators
import lestelabs.binanceapi.data.streams.datasource.Candlestick
import lestelabs.binanceapi.ui.home.DialogFragment
import java.lang.Exception

class Binance() {

    val factory: BinanceApiClientFactory = initFactory()
    val syncClient: BinanceApiRestClient = initRestClient(factory)
    val asyncClient: BinanceApiAsyncRestClient = initAsyncClient(factory)
    val webSocketClient: BinanceApiWebSocketClient = initWebSocketClient()
    val offset = 50
    val sticks = arrayOf("ADAEUR", "BTCEUR", "ETHEUR", "SOLEUR", "BNBEUR", "IOTXBTC", "DOGEEUR", "SHIBEUR", "LUNABTC", "SANDBTC", "MANABTC" )
    val interval = CandlestickInterval.HOURLY
    val TAG="Binance"
    val keepAlive: Long = 15*3600*1000
    val cursorSizeOffset = 5


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


    fun getBalance(symbol:String): List<String> {
        val output: MutableList<String> = mutableListOf()
        val value = syncClient.account.getAssetBalance(symbol)
        output.add(value.free)
        output.add(value.locked)
        return output
    }

    fun getCandleStickComplete(symbol: String): MutableList<Candlestick> {
        val symbolShort = symbol.substring(0,symbol.length-1-2).toString()
        val response = syncClient.getCandlestickBars(symbol, interval)
        val balances = syncClient.account.getAssetBalance(symbolShort)
        val inputIndicators = DoubleArray (response.size)
        for (i in 0 until response.size) {
            response[i].stick = symbol
            inputIndicators[i] = response[i].close.toDouble()
        }
        val sma = Indicators.movingAverage(inputIndicators, offset)
        val rsi = Indicators.rsi(inputIndicators, offset)
        for (i in offset until response.size) {
            response[i].sma = sma[i-offset]
            response[i].rsi = rsi[i-offset]
        }
        response[response.size-1].ownFree = balances.free.toDouble()
        response[response.size-1].ownLocked = balances.locked.toDouble()
        response[response.size-1].ownValueEUR = (balances.free.toDouble() + balances.locked.toDouble())*response[response.size-1].close.toDouble()
        response[response.size-1].maxValue80 = response.maxOf {it ->  it.close.toDouble()} * 0.8
        return response
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
                candlesticksClosePrice[i] = candleStickBars[i].close.toDouble()
                candlesticksDate[i] = candleStickBars[i].closeTime
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

    fun cancelOrderBinance(view: View, position: Int): List<String>?  {
        val symbol = sticks[position]
        val symbolShort = symbol.substring(0,symbol.length-4)
        val openOrders: List<Order> = syncClient.getOpenOrders(OrderRequest(symbol))

        if (openOrders.isNotEmpty()) {
            val firstOpenOrder = openOrders[0].orderId
            syncClient.cancelOrder(CancelOrderRequest(symbol, firstOpenOrder))
            Toast.makeText(
                view.context,
                "Order $firstOpenOrder cancelled. Now reloading ...",
                Toast.LENGTH_LONG
            ).show()
            return getBalance(symbolShort)
        } else {
            return null
            Toast.makeText(view.context,"Not found!!", Toast.LENGTH_LONG).show()
        }
    }

/*    fun setOrderBinance(fm:FragmentManager) {
        val editNameDialogFragment = DialogFragment(requireFragmentManager())
        fm.let {editNameDialogFragment.show(it, "fragment_edit_name")}
    }*/


}