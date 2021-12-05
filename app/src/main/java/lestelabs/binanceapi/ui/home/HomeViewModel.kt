package lestelabs.binanceapi.ui.home

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lestelabs.binanceapi.MainActivity
import lestelabs.binanceapi.R
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.data.streams.datasource.Candlestick
import lestelabs.binanceapi.data.network.UnauthorizedException

class HomeViewModel : ViewModel() {

    private var cursor: Int = 0
    val binance = Binance()


    private val _text = MutableLiveData<String>().apply {
        val balances = binance.getBalance("EUR")
        value = "free: " + balances[0] + " locked: " + balances[1]
    }
    val text: LiveData<String> = _text
    val streams = MutableLiveData<List<Candlestick?>>()
    val isLoading = MutableLiveData<Boolean>(false)
    val isReload = MutableLiveData<Boolean>(false)
    val isLoggedOut = MutableLiveData<Boolean>(false)



    /// Gets Streams
    fun getStreams(refresh: Boolean, puntero:Int, punteroSizeOffset: Int) {

        viewModelScope.launch(Dispatchers.Main) {
            isLoading.postValue(true)
            // Get Streams
            try {
                var candlesticks = listOf<Candlestick?>()
                cursor = puntero
                for (i in cursor .. cursor + punteroSizeOffset -1) {
                    if (i < binance.sticks.size) {
                        val candlestick = withContext(Dispatchers.IO)  {
                            binance.getCandleStickComplete(binance.sticks[i]).lastOrNull()
                        }
                        candlesticks = candlesticks.plus(candlestick)
                        streams.postValue(candlesticks)
                    }
                }
                //send_notification(context)
                // Set Streams Value
/*                if (refresh) {
                    // Set new list
                    cursor = 0
                    streams.postValue(candlesticks)
                } else {
                    // Append to current list
                    val currentStreams = streams.value.orEmpty()
                    val totalStreams = currentStreams.plus(candlesticks)
                    streams.postValue(totalStreams)
                }*/

            } catch (e: UnauthorizedException) {
                isLoggedOut.postValue(true)
            }
            // Set Loading to false
            isLoading.postValue(false)
        }
    }

    /// Expose if more streams are available for pagination listener
    fun areMoreStreamsAvailable(punteroSizeOffset: Int): Boolean = (cursor + punteroSizeOffset) < binance.sticks.size

}