package lestelabs.binanceapi.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.data.streams.datasource.Candlestick
import lestelabs.binanceapi.data.network.UnauthorizedException

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    // Observables
    //val streams = Pair(MutableLiveData<String>(), MutableLiveData<Candlestick?>())
    val streams = MutableLiveData<List<Candlestick?>>()
    val isLoading = MutableLiveData<Boolean>(false)
    val isLoggedOut = MutableLiveData<Boolean>(false)

    private var cursor: Int = 0
    val binance = Binance()

    /// Gets Streams
    fun getStreams(refresh: Boolean, puntero:Int, punteroSizeOffset: Int){
        viewModelScope.launch {
            // Set Loading to true
            isLoading.postValue(true)
            // Get Streams
            try {
                var candlesticks = listOf<Candlestick?>()
                cursor = puntero
                for (i in cursor .. cursor + punteroSizeOffset -1) {
                    if (i < binance.sticks.size) {
                        val candlestick = binance.getCandleStickComplete(binance.sticks[i]).lastOrNull()
                        candlesticks = candlesticks.plus(candlestick)
                    }
                }


                // Set Streams Value
                if (refresh) {
                    // Set new list
                    streams.postValue(candlesticks)
                } else {
                    // Append to current list
                    val currentStreams = streams.value.orEmpty()
                    val totalStreams = currentStreams.plus(candlesticks)
                    streams.postValue(totalStreams)

                }

            } catch (e: UnauthorizedException) {
                isLoggedOut.postValue(true)
            }
            // Set Loading to false
            isLoading.postValue(false)
        }
    }

    /// Expose if more streams are available for pagination listener
    fun areMoreStreamsAvailable(): Boolean = cursor < Binance().sticks.size

}