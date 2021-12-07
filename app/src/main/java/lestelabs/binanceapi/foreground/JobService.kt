package lestelabs.binanceapi.foreground

import android.content.Intent

import android.app.job.JobParameters
import android.content.Context

import android.content.IntentFilter

import android.os.Build
import android.os.Handler
import android.util.Log

import androidx.annotation.RequiresApi
import lestelabs.binanceapi.ProcessMainClass
import lestelabs.binanceapi.tools.Globals
import java.lang.Exception




@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class JobService : android.app.job.JobService() {
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        val serviceAdmin = ProcessMainClass()
        serviceAdmin.launchService(this)
        instance = this
        Companion.jobParameters = jobParameters
        return false
    }

    private fun registerRestarterReceiver() {

        // the context can be null if app just installed and this is called from restartsensorservice
        // https://stackoverflow.com/questions/24934260/intentreceiver-components-are-not-allowed-to-register-to-receive-intents-when
        // Final decision: in case it is called from installation of new version (i.e. from manifest, the application is
        // null. So we must use context.registerReceiver. Otherwise this will crash and we try with context.getApplicationContext
        if (restartBroadcastReceiver == null) restartBroadcastReceiver =
            RestartBroadcastReceiver() else try {
            unregisterReceiver(restartBroadcastReceiver)
        } catch (e: Exception) {
            // not registered
        }
        //give the time to run
        Handler().postDelayed(Runnable {
            val filter = IntentFilter()
            filter.addAction(Globals().RESTART_INTENT)
            try {
                registerReceiver(restartBroadcastReceiver, filter)
            } catch (e: Exception) {
                try {
                    applicationContext.registerReceiver(restartBroadcastReceiver, filter)
                } catch (ex: Exception) {
                }
            }
        }, 1000)
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        Log.i(TAG, "Stopping job")
        val broadcastIntent = Intent(Globals().RESTART_INTENT)
        sendBroadcast(broadcastIntent)
        // give the time to run
        Handler().postDelayed(Runnable { unregisterReceiver(restartBroadcastReceiver) }, 1000)
        return false
    }

    companion object {
        private const val TAG = "JobService"
        private var restartBroadcastReceiver: RestartBroadcastReceiver? = null
        private var instance: JobService? = null
        private var jobParameters: JobParameters? = null

        /**
         * called when the tracker is stopped for whatever reason
         * @param context
         */
        fun stopJob(context: Context?) {
            if (instance != null && jobParameters != null) {
                try {
                    instance!!.unregisterReceiver(restartBroadcastReceiver)
                } catch (e: Exception) {
                    // not registered
                }
                Log.i(TAG, "Finishing job")
                instance!!.jobFinished(jobParameters, true)
            }
        }
    }
}