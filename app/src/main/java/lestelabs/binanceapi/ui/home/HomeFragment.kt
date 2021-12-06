package lestelabs.binanceapi.ui.home

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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import lestelabs.binanceapi.MainActivity
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.databinding.FragmentHomeBinding
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews

import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import lestelabs.binanceapi.data.streams.datasource.Candlestick


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var binance = Binance()
    private val adapter = StreamsAdapter()
    private var cursor = 0
    private var cursorSizeOffset = binance.cursorSizeOffset
    private val TAG = "HomeFragment"
    lateinit var mainHandler: Handler
    private lateinit var repeatIndefinetly: Runnable

    // declaring notification variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    lateinit var contentView:RemoteViews

    // declaring notification variables
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Init LiveData Observers
        initObservers(root)
        // Init RecyclreView
        initRecyclerView(root)
        //Init refresh
        initRefresh(root)
        // Init the repetition of getStreams every delta time
        init_repeat_indefinetly()
        // Init the notification alerts
        init_notification(root.context)

        // Get Streams (not needed, it is done as first repeat)
        //homeViewModel.getStreams(true, cursor, cursorSizeOffset)
        //homeViewModel.getStreams(true, cursor, Binance().sticks.size)
        return root
    }


    private fun initRecyclerView(view: View) {

        // Set Layout Manager
        val layoutManager = LinearLayoutManager(requireActivity())
        view.recyclerView.layoutManager = layoutManager
        // Set Adapter
        view.recyclerView.adapter = adapter
        // Set Pagination Listener
       view.recyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                //cursor += cursorSizeOffset
                //homeViewModel.getStreams(refresh = false, cursor, cursorSizeOffset)
            }

            override fun isLastPage(): Boolean {
                return !homeViewModel.areMoreStreamsAvailable(cursorSizeOffset)
            }

            override fun isLoading(): Boolean {
                return swipeRefreshLayout.isRefreshing
            }

        })
    }

    private fun initObservers(view: View) {
        //Text
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            view.text_home.text = it
        })
        // Loading
        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            view.swipeRefreshLayout.isRefreshing = it
        })
        // Streams
        homeViewModel.streams.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            // doesnt work since only change with the view and not in background, so check in the homeviewModel
/*            if (cursor == cursorSizeOffset-1) {
                checkIfSendBuySellNotification(view.context, it)
                cursor = 0
            } else  cursor +=1*/

        })
    }

    fun initRefresh(view: View) {
        // Swipe to Refresh Listener
        view.swipeRefreshLayout.setOnRefreshListener {
            cursor = 0
            //homeViewModel.getStreams(refresh = true, cursor, cursorSizeOffset)
            homeViewModel.getStreams(true, cursor, binance.cursorSizeOffset, notificationManager, contentView, builder)
        }
    }

    fun init_repeat_indefinetly() {
        mainHandler = Handler(Looper.getMainLooper())
        repeatIndefinetly = object : Runnable {
            override fun run() {
                homeViewModel.getStreams( true, cursor, cursorSizeOffset, notificationManager, contentView, builder)
                //homeViewModel.getStreams( true, cursor, 2)
                //mainHandler.postDelayed(this, binance.keepAlive)
                //mainHandler.postDelayed(this, deltaTime)
                mainHandler.postDelayed(this, binance.intervalms)
                Log.d(MainActivity.TAG, "repeatIndefinetly done")
            }
        }
        mainHandler.post(repeatIndefinetly)
    }

    fun init_notification(context: Context) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        contentView = RemoteViews(context.packageName, lestelabs.binanceapi.R.layout.activity_after_notification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            builder = Notification.Builder(context, channelId)
                .setContent(contentView)
                .setSmallIcon(lestelabs.binanceapi.R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, lestelabs.binanceapi.R.drawable.ic_launcher_background))
            //.setContentIntent(pendingIntent)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        } else {
            builder = Notification.Builder(context)
                .setContent(contentView)
                .setSmallIcon(lestelabs.binanceapi.R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, lestelabs.binanceapi.R.drawable.ic_launcher_background))
            //.setContentIntent(pendingIntent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
