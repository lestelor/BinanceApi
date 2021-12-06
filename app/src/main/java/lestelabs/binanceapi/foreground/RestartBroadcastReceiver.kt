package lestelabs.binanceapi.foreground

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log


class RestartBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(
            RestartBroadcastReceiver::class.java.simpleName,
            "Service Stopped, but this is a never ending service."
        )
        context.startService(Intent(context, AutoStartService::class.java))
    }
}