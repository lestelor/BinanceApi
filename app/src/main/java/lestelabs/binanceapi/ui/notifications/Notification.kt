package lestelabs.binanceapi.ui.notifications

import android.os.Build

import android.app.Notification

import androidx.core.content.ContextCompat

import android.app.NotificationChannel

import android.app.NotificationManager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresApi
import lestelabs.binanceapi.MainActivity
import lestelabs.binanceapi.R


class Notification {
    private var notificationPendingIntent: PendingIntent? = null

    /**
     * This is the method  called to create the Notification
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setNotification(context: Context, title: String?, text: String?, icon: Int): Notification? {
        if (notificationPendingIntent == null) {
            val notificationIntent = Intent(context, MainActivity::class.java)
            notificationIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)
        }
        val notification: Notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // OREO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name: CharSequence = "Permanent Notification"
            //mContext.getString(R.string.channel_name);
            val importance = NotificationManager.IMPORTANCE_LOW
            val CHANNEL_ID = "uk.ac.shef.oak.channel"
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            //String description = mContext.getString(R.string.notifications_description);
            val description = "I would like to receive travel alerts and notifications for:"
            channel.description = description
            val notificationBuilder: Notification.Builder = Notification.Builder(context, CHANNEL_ID)
            notificationManager?.createNotificationChannel(channel)
            notification =
                notificationBuilder //the log is PNG file format with a transparent background
                    .setSmallIcon(icon)
                    .setColor(ContextCompat.getColor(context, R.color.black))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(notificationPendingIntent)
                    .build()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification =
                Notification.Builder(
                    context,
                    "channel"
                ) // to be defined in the MainActivity of the app
                    .setSmallIcon(icon)
                    .setContentTitle(title) //                    .setColor(mContext.getResources().getColor(R.color.colorAccent))
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent).build()
        } else {
            notification =
                Notification.Builder(
                    context,
                    "channel"
                ) // to be defined in the MainActivity of the app
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent).build()
        }
        return notification
    }
}