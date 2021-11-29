package lestelabs.binanceapi.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import lestelabs.binanceapi.data.streams.StreamsRepository
import lestelabs.binanceapi.data.streams.model.Stream
import kotlinx.coroutines.launch
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.binance.api.client.domain.market.Candlestick
import lestelabs.binanceapi.data.network.UnauthorizedException
import lestelabs.binanceapi.data.streams.datasource.StreamsRemoteDataSource
import org.apache.commons.lang3.mutable.Mutable

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    // Observables
    val streams = Pair(MutableLiveData<String>(), MutableLiveData<MutableList<Candlestick>?>())
    val isLoading = MutableLiveData<Boolean>(false)
    val isLoggedOut = MutableLiveData<Boolean>(false)

    private var cursor: String? = null

    /// Gets Streams
    fun getStreams(refresh: Boolean) {
        viewModelScope.launch {
            // Set Loading to true
            isLoading.postValue(true)
            // Get Streams
            try {
                val streamsResult = Pair("ADAEUR", StreamsRemoteDataSource(
                    Binance()).getStreams("ADAEUR"))
                // Set Streams Value
                if (refresh) {
                    // Set new list
                    streams.first.postValue(streamsResult.first)
                    streams.second.postValue(streamsResult.second)
                } else {
                    // Append to current list
/*                    val totalStreams = mutableListOf(streams.value).add(streamsResult)
                    streams.postValue(totalStreams)*/
                }
            } catch (e: UnauthorizedException) {
                isLoggedOut.postValue(true)
            }
            // Set Loading to false
            isLoading.postValue(false)
        }
    }

    /// Expose if more streams are available for pagination listener
    fun areMoreStreamsAvailable(): Boolean = cursor != null

}