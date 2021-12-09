package lestelabs.binanceapi

import android.content.Context
import android.os.Build
import android.content.Intent
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity
import lestelabs.binanceapi.foreground.JobService


class ProcessMainClass: AppCompatActivity() {
    val TAG = ProcessMainClass::class.java.simpleName
    private var serviceIntent: Intent? = null

    //fun ProcessMainClass() {}


    private fun setServiceIntent(context: Context) {
        if (serviceIntent == null) {
            serviceIntent = Intent(context, Service::class.java)
        }
    }

    /**
     * launching the service
     */
    fun launchService(context: Context) {
/*        if (context == null) {
            return
        }*/
        setServiceIntent(context)
        // depending on the version of Android we eitehr launch the simple service (version<O)
        // or we start a foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
        Log.d(TAG, "ProcessMainClass: start service go!!!!")
    }
}