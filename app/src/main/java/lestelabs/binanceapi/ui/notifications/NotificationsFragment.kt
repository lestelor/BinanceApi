package lestelabs.binanceapi.ui.notifications

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.coroutines.*
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.data.streams.datasource.Candlestick
import lestelabs.binanceapi.databinding.FragmentNotificationsBinding
import java.util.*
import kotlin.collections.ArrayList


const val STATE_LIST = "List Notifications Adapter Data"
const val STATE_TIMER = "Timer is on"
var items: MutableList<String> = mutableListOf()
var timer: Long = 0
var timerOn = false


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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //val texts = this.adapter.getList() as ArrayList
        //outState.putStringArrayList(STATE_LIST, texts)
        outState.putStringArrayList(STATE_LIST, items as java.util.ArrayList<String>)
        outState.putBoolean(STATE_TIMER, true)
    }


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
        if (!timerOn) init_runnable()

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState);
        //If restoring from state, load the list from the bundle
        if (savedInstanceState != null) {
            items =
                savedInstanceState.getStringArray(STATE_LIST) as MutableList<String>
            timerOn = savedInstanceState.getBoolean(STATE_TIMER)
            adapter = NotificationAdapter(requireActivity(), items)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun init_list(view: View) {
        // Construct the data source
        //val texts: MutableList<String> = adapter.mNotifications?: mutableListOf()
        // Create the adapter to convert the array to views
        adapter = NotificationAdapter(requireActivity(), items?: mutableListOf())
        // Attach the adapter to a ListView
        view.NotificationsList.adapter = adapter
    }


    fun init_observers(root: View) {
/*        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            root.text_notifications.text = it
        })*/

        notificationsViewModel.notifications.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "timer notifications number: ${it.size}")
            //adapter.clear()

            //adapter.addAll(it)
            adapter.notifyDataSetChanged()
        })
    }

    @DelicateCoroutinesApi
    fun init_runnable() {
        mainHandler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                if ((timer*60*1000) % binance.intervalms == 0.toLong()) {
                    Log.d(TAG, "timer notification fragment runnable timer send notification")
                    val notificationsText = check_send_notification()

                }

                mainHandler.postDelayed(this, 60000)
                Log.d(TAG, Date().hours.toString() + ":"+ Date().minutes + " timer notification fragment runnable timer+++++ " + timer++)
            }
        }
        mainHandler.post(runnable)
        timerOn = true
    }

    @DelicateCoroutinesApi
    fun check_send_notification() {

        GlobalScope.launch(Dispatchers.Main) {
            var candlesticks: List<Candlestick> = mutableListOf()
            candlesticks = withContext(Dispatchers.IO) {
                binance.getCandlesticks()
            }
            val notificationsText = notifications.checkIfSendBuySellNotification(candlesticks)
            //notificationsViewModel.notificationPutText(notificationsText)
            items = items.plus(notificationsText).toMutableList()
            adapter.clear()
            adapter.addAll(items)
            adapter.notifyDataSetChanged()
        }
    }
}

