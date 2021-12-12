package lestelabs.binanceapi.ui.notifications

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RemoteViews
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.coroutines.*
import lestelabs.binanceapi.MainActivity
import lestelabs.binanceapi.R
import lestelabs.binanceapi.Service
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.databinding.FragmentNotificationsBinding
import java.lang.Runnable


class NotificationsFragment : Fragment() {



    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var adapter: NotificationAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var mainHandler: Handler
    lateinit var runnable: Runnable

    lateinit var binance: Binance
    lateinit var notifications: Notifications



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textNotifications

        init_list(root)
        init_observers(root)
        binance = Binance()
        notifications = Notifications(root.context)
        notifications.initNotifications()
        init_runnable()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun init_list(view: View) {
        // Construct the data source
        val texts: ArrayList<String> = arrayListOf()
        // Create the adapter to convert the array to views
        adapter = NotificationAdapter(requireActivity(), texts)
        // Attach the adapter to a ListView
        view.NotificationsList.adapter = adapter
    }


    fun init_observers(root: View) {
/*        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            root.text_notifications.text = it
        })*/

        notificationsViewModel.notifications.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "timer notifications number: ${it.size}")
            adapter.clear()
            adapter.addAll(it)
        })
    }

    @DelicateCoroutinesApi
    fun init_runnable() {
        mainHandler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                check_send_notification()
                //notificationsViewModel.notificationPutText(listOf("hola caracola"))
                //notificationsViewModel.notificationPutText(listOf("nice to meet you"))
                //binance.syncClient.keepAliveUserDataStream(listenKey);
                mainHandler.postDelayed(this, binance.intervalms)
                Log.d(TAG, "timer notification fragment runnable")
            }
        }
        mainHandler.postDelayed(runnable, binance.intervalms)
    }

    @DelicateCoroutinesApi
    fun check_send_notification() {
        GlobalScope.launch(Dispatchers.Main) {
            val candlesticks = withContext(Dispatchers.IO) {
                binance.getCandlesticks()
            }
            val notificationsText = notifications.checkIfSendBuySellNotification(candlesticks)
            notificationsViewModel.notificationPutText(notificationsText)
        }

    }

}