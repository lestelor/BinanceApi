package lestelabs.binanceapi.binance

import android.app.Dialog
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.item_place_order.*
import kotlinx.android.synthetic.main.item_place_order.view.*
import lestelabs.binanceapi.MainActivity
import lestelabs.binanceapi.R
import lestelabs.binanceapi.binance.api.client.*
import lestelabs.binanceapi.binance.api.client.domain.account.NewOrder.marketBuy
import lestelabs.binanceapi.binance.api.client.domain.account.NewOrder.marketSell
import lestelabs.binanceapi.binance.api.client.domain.account.Order
import lestelabs.binanceapi.binance.api.client.domain.account.request.CancelOrderRequest
import lestelabs.binanceapi.binance.api.client.domain.account.request.OrderRequest
import lestelabs.binanceapi.binance.api.client.domain.market.CandlestickInterval
import lestelabs.binanceapi.charts.Indicators
import lestelabs.binanceapi.data.streams.datasource.Candlestick
import lestelabs.binanceapi.ui.home.DialogFragment
import java.lang.Exception
import lestelabs.binanceapi.binance.api.client.domain.account.Trade

import lestelabs.binanceapi.binance.api.client.domain.account.NewOrderResponse
import lestelabs.binanceapi.binance.api.client.domain.account.NewOrderResponseType
import lestelabs.binanceapi.binance.api.client.domain.TimeInForce

import lestelabs.binanceapi.binance.api.client.domain.OrderType

import lestelabs.binanceapi.binance.api.client.domain.OrderSide

import lestelabs.binanceapi.binance.api.client.domain.account.NewOrder


import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import android.widget.AdapterView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Binance {

    val factory: BinanceApiClientFactory = initFactory()
    val syncClient: BinanceApiRestClient = initRestClient(factory)
    val asyncClient: BinanceApiAsyncRestClient = initAsyncClient(factory)
    val webSocketClient: BinanceApiWebSocketClient = initWebSocketClient()
    val offset = 50
    val sticks = arrayOf("ADAEUR", "BTCEUR", "ETHEUR", "SOLEUR", "BNBEUR", "IOTXBTC", "DOGEEUR", "SHIBEUR", "LUNABTC", "SANDBTC", "MANABTC", "XRPEUR", "MATICEUR" )
    val interval = CandlestickInterval.HOURLY
/*    val intervalms: Long = when(interval.intervalId) {
        "1h" -> 60*60*1000
        else -> 60*60*1000
    }*/
    val intervalms: Long = 60*60*1000
    val rsiLowerLimit = 40.0
    val rsiUpperLimit = 60.0

    val TAG="Binance"
    val keepAlive: Long = 15*60*1000
    // Number of candlesticks to be shown
    //val cursorSizeOffset = sticks.size
    val cursorSizeOffset = 1


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

   fun setOrderBinance(view: View, position: Int) {


       val symbol1 = sticks[position].take(sticks[position].length - 3)
       val symbol2 = sticks[position].takeLast(3)

       val dialog = Dialog(view.context)
       dialog.setCancelable(true)
       dialog.setContentView(R.layout.item_place_order)


       dialog.dialog_symbol1.text = symbol1
       when(symbol2) {
           "EUR" -> dialog.dialog_spinner2.setSelection(0)
           "BTC" -> dialog.dialog_spinner2.setSelection(1)
       }

       dialog.dialog_available1.text = "%.5f".format(getBalance(symbol1)[0].toDouble())
       dialog.dialog_available2.text = "%.5f".format(getBalance(symbol2)[0].toDouble())


       val values: Array<String> = arrayOf("EUR", "BTC")
       val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, values)
       dialog.dialog_spinner2.adapter = adapter
       dialog.dialog_spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
           override fun onItemSelected(
               adapterView: AdapterView<*>, view: View?,
               position: Int, id: Long
           ) {
                dialog.dialog_available2.text =
               getBalance(dialog.dialog_spinner2.selectedItem.toString())[0]
           }
           override fun onNothingSelected(p0: AdapterView<*>?) {
           }
       }

       // buttons buy/sell #symbol at a limit price (EUR/BTC)
       dialog.dialog_button_buy.setOnClickListener() {
           val symbol = dialog.dialog_symbol1.text.toString() + dialog.dialog_spinner2.selectedItem
           Log.d(TAG,"Binance dialog symbol $symbol")
           val price = dialog.dialog_price.text.toString()
           val quantity = dialog.dialog_buy_sell_quantity.text.toString()
           val type = OrderSide.BUY
           val orderId = executeOrder(view, type, symbol, quantity, price)
            dialog.hide()
       }
       dialog.dialog_button_sell.setOnClickListener {
           val symbol = dialog.dialog_symbol1.text.toString() + dialog.dialog_spinner2.selectedItem
           Log.d(TAG,"Binance dialog symbol $symbol")
           val price = dialog.dialog_price.text.toString()
           val quantity = dialog.dialog_buy_sell_quantity.text.toString()
           val type = OrderSide.SELL
           val orderId = executeOrder(view, type, symbol, quantity, price)
           dialog.hide()
       }
       dialog.show()
    }

    private fun executeOrder(view: View, type:OrderSide, symbol: String, quantity:String, price: String ) {
        try {
            val order = NewOrder(
                symbol,
                type,
                OrderType.LIMIT,
                TimeInForce.GTC,
                quantity,
                price
            )
            val newOrderResponse =
                syncClient.newOrder(
                    order.newOrderRespType(NewOrderResponseType.FULL)
                )
            val orderId = newOrderResponse.orderId
            Toast.makeText(view.context, "Order $orderId executed", Toast.LENGTH_LONG).show()
        } catch(e:Exception) {
            Toast.makeText(view.context, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    suspend fun getCandlesticks(): List<Candlestick> {
        var candlesticks: List<Candlestick> = listOf()
        for (i in 0 .. sticks.size -1) {
            val candlestick = withContext(Dispatchers.IO)  {
                getCandleStickComplete(sticks[i]).lastOrNull()
            }
            candlestick?.let{ candlesticks = candlesticks.plus(candlestick)}
        }
        return candlesticks
    }


}