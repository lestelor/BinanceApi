package lestelabs.binanceapi.ui.home

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ContentView
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
import java.util.*

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
    fun getStreams(refresh: Boolean, puntero:Int, punteroSizeOffset: Int, notificationManager: NotificationManager, contentView: RemoteViews, builder: Notification.Builder) {

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

                checkIfSendBuySellNotification(candlesticks, notificationManager, contentView, builder)

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

    private fun checkIfSendBuySellNotification(candlesticks: List<Candlestick?>,notificationManager: NotificationManager, contentView: RemoteViews, builder: Notification.Builder) {
        send_notification("Hola", notificationManager, contentView,  builder)
        for (i in candlesticks.indices) {
            val rsi = candlesticks[i]?.rsi
            val value = candlesticks[i]?.close?.toDouble()
            val sma = candlesticks[i]?.sma?.toDouble()
            val symbol = candlesticks[i]?.stick
            val price = candlesticks[i]?.maxValue80
            if (rsi != null && sma !=null && value !=null) {
                if (rsi < 35.0 && sma > value) {
                    send_notification("Buy $symbol rsi: $rsi sma: $sma", notificationManager, contentView, builder )
                } else if(rsi > 65.0 && sma<value) {
                    send_notification("Sell $symbol at a $price rsi: $rsi sma: $sma", notificationManager, contentView, builder )
                }
            }
        }
    }

    fun send_notification(text:String, notificationManager: NotificationManager, contentView: RemoteViews, builder: Notification.Builder) {
        // pendingIntent is an intent for future use i.e after
        // the notification is clicked, this intent will come into action

        //val intent = Intent(this, afterNotification::class.java)

        // FLAG_UPDATE_CURRENT specifies that if a previous
        // PendingIntent already exists, then the current one
        // will update it with the latest intent
        // 0 is the request code, using it later with the
        // same method again will get back the same pending
        // intent for future reference
        // intent passed here is to our afterNotification class
        //val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // RemoteViews are used to use the content of
        // some different layout apart from the current activity layout

        contentView.setTextViewText(R.id.tvNotification, Date().hours.toString() + ":" + Date().minutes + " " + text)
        notificationManager.notify((0..123456).random(), builder.build())

    }
}