package lestelabs.binanceapi.foreground

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import lestelabs.binanceapi.ProcessMainClass
import lestelabs.binanceapi.Service


val TAG = "Background"



private var jobScheduler: JobScheduler? = null

class RestartBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "about to start timer $context")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob(context)
        } else {
            val bck = ProcessMainClass()
            bck.launchService(context)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun scheduleJob(context: Context) {
        if (jobScheduler == null) {
            jobScheduler = context
                .getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        }
        val componentName = ComponentName(
            context,
            JobService::class.java
        )
        val jobInfo = JobInfo.Builder(
            1,
            componentName
        ) // setOverrideDeadline runs it immediately - you must have at least one constraint
            // https://stackoverflow.com/questions/51064731/firing-jobservice-without-constraints
            .setOverrideDeadline(0)
            .setPersisted(true).build()
        jobScheduler?.schedule(jobInfo)
    }
}