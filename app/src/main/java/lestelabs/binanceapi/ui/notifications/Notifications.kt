package lestelabs.binanceapi.ui.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import lestelabs.binanceapi.R
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.data.streams.datasource.Candlestick
import java.util.*
import android.app.PendingIntent

import android.content.Intent
import androidx.annotation.RequiresApi

import lestelabs.binanceapi.MainActivity




class Notifications(context: Context) {

    private val mContext = context
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    lateinit var contentView:RemoteViews
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    private var notificationId = 1


    fun initNotifications() {
        notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        contentView = RemoteViews(mContext.packageName, R.layout.activity_after_notification)
        val intent = Intent(mContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            mContext,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            builder = Notification.Builder(mContext, channelId)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        mContext.resources,
                        R.drawable.ic_launcher_background
                    )
                )
            .setContentIntent(pendingIntent)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
            mContext.applicationContext.startForegroundService(intent)
        } else {
            builder = Notification.Builder(mContext)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        mContext.resources,
                        R.drawable.ic_launcher_background
                    )
                )
            .setContentIntent(pendingIntent)
        }

    }

    fun sendNotification(text:String) {
        contentView.setTextViewText(R.id.tvNotification, Date().hours.toString() + ":" + Date().minutes + " " + text)
        notificationManager.notify(notificationId++, builder.build())
    }

    @DelicateCoroutinesApi
    fun checkIfSendBuySellNotification(candlesticks: List<Candlestick?>) {
        sendNotification("Hola caracola " + Date().hours + ":" + Date().minutes + " " + candlesticks.size)
        for (i in candlesticks.indices) {
            val rsi = candlesticks[i]?.rsi
            val value = candlesticks[i]?.close?.toDouble()
            val sma = candlesticks[i]?.sma?.toDouble()
            val symbol = candlesticks[i]?.stick
            val price = candlesticks[i]?.maxValue80
            if (rsi != null && sma !=null && value !=null) {
                if (rsi < Binance().rsiLowerLimit && sma > value) {
                    sendNotification("Buy $symbol rsi: $rsi sma: $sma")
                } else if(rsi > Binance().rsiUpperLimit && sma<value) {
                    sendNotification("Sell $symbol at a $price rsi: $rsi sma: $sma")
                }
            }
        }
    }
}