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

import android.widget.TextView





class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter = StreamsAdapter()
    private var cursor = 0
    private var cursorSizeOffset = Binance().cursorSizeOffset
    private val TAG = "HomeFragment"
    lateinit var mainHandler: Handler
    lateinit var binanceKeepAlive: Runnable
    private var binance = Binance()

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


        // Get Streams
        //homeViewModel.getStreams(true, cursor, cursorSizeOffset)
        //homeViewModel.getStreams(true, cursor, Binance().sticks.size-1)
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
        })
    }

    fun initRefresh(view: View) {
        // Swipe to Refresh Listener
        view.swipeRefreshLayout.setOnRefreshListener {
            cursor = 0
            //homeViewModel.getStreams(refresh = true, cursor, cursorSizeOffset)
            homeViewModel.getStreams(true, cursor, Binance().sticks.size-1)
        }
    }

    fun init_repeat_indefinetly() {
        val deltaTime = binance.interval.intervalId
        Log.d(TAG, "repeatIndefinetly $deltaTime")
        mainHandler = Handler(Looper.getMainLooper())
        binanceKeepAlive = object : Runnable {
            override fun run() {
                homeViewModel.getStreams(true, cursor, Binance().sticks.size-1)
                //mainHandler.postDelayed(this, binance.keepAlive)
                //mainHandler.postDelayed(this, deltaTime)
                mainHandler.postDelayed(this, 1*60*1000)
                Log.d(MainActivity.TAG, "repeatIndefinetly done")
            }
        }
        mainHandler.post(binanceKeepAlive)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
