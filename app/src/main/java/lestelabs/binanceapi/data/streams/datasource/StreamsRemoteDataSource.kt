package lestelabs.binanceapi.data.streams.datasource

import lestelabs.binanceapi.data.network.Endpoints
import lestelabs.binanceapi.data.network.UnauthorizedException
import lestelabs.binanceapi.data.streams.model.StreamsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lestelabs.binanceapi.MainActivity
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.binance.api.client.BinanceApiAsyncRestClient
import lestelabs.binanceapi.binance.api.client.domain.market.Candlestick

import lestelabs.binanceapi.binance.api.client.BinanceApiCallback
import lestelabs.binanceapi.binance.api.client.domain.market.TickerStatistics


/**
 * Created by alex on 12/09/2020.
 */

class StreamsRemoteDataSource(private val binance: Binance) {

    suspend fun getStreams(stick: String?): MutableList<Candlestick>? = withContext(Dispatchers.IO) {
        try {
            val response = binance.syncClient.getCandlestickBars(stick, binance.interval)
            response
        } catch (t: Throwable) {
            null
        }
    }
}