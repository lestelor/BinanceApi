package lestelabs.binanceapi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import lestelabs.binanceapi.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter = StreamsAdapter()
    private var cursor = 0
    private var cursorSizeOffset = 2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        // Init RecyclerView
        initRecyclerView(root)
        // Init LiveData Observers
        initObservers()
        // Get Streams
        homeViewModel.getStreams(true,0, cursorSizeOffset)

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
                cursor += cursorSizeOffset
                homeViewModel.getStreams(refresh = false, cursor, cursorSizeOffset)
            }

            override fun isLastPage(): Boolean {
                return !homeViewModel.areMoreStreamsAvailable()
            }

            override fun isLoading(): Boolean {
                return swipeRefreshLayout.isRefreshing
            }
        })
    }

    private fun initObservers() {
        // Loading
        homeViewModel.isLoading.observe(requireActivity()) {
            swipeRefreshLayout.isRefreshing = it
        }
        // Streams
        homeViewModel.streams.observe(requireActivity()) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Swipe to Refresh Listener
/*    override fun onRefresh() {
        //homeViewModel.getStreams(refresh = true)
    }*/
}