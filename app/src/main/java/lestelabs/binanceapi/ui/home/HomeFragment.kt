package lestelabs.binanceapi.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_stream.view.*
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.databinding.FragmentHomeBinding
import android.app.Dialog
import lestelabs.binanceapi.R


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter = StreamsAdapter()
    private var cursor = 0
    private var cursorSizeOffset = Binance().cursorSizeOffset



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

        // Get Streams
        homeViewModel.getStreams(true, cursor, cursorSizeOffset)


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
            homeViewModel.getStreams(refresh = true, cursor, cursorSizeOffset)
        }
    }

/*    fun setOrderOnclick() {
        val popup =  PopupMenu()
        popup.view. .inflate(R.layout.item_place_order)

        val dialog: DialogFragment = DialogFragment(binding)
        dialog.show(requireFragmentManager(), "dialog")
    }*/

    fun cancelOrderOnClick(view: View, position: Int): List<String>? {
        return Binance().cancelOrderBinance(view,position)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    @SuppressLint("ResourceType")
    fun setOrderOnClick(fragmentView: View) {

        val dialog = Dialog(fragmentView.context)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.item_place_order)
        dialog.show()

            /*val popup = DialogFragment(fragmentView.context, fragmentView)
            popup.inflate(R.layout.item_place_order)*/
            /*popup.menu.getItem(0).icon = ContextCompat.getDrawable(context, R.drawable.ic_movistar)
            popup.menu.getItem(1).icon = ContextCompat.getDrawable(context, R.drawable.ic_orange)
            popup.menu.getItem(2).icon = ContextCompat.getDrawable(context, R.drawable.ic_vodafone)
            popup.menu.getItem(3).icon = ContextCompat.getDrawable(context, R.drawable.ic_masmovil)
            popup.menu.getItem(4).icon = ContextCompat.getDrawable(context, R.drawable.ic_omv_green)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.opheaderTelefonica -> {
                        menuOperatorOnClick("Telefonica")
                    }
                    R.id.opheaderOrange -> {
                        menuOperatorOnClick("Orange")
                    }
                    R.id.opheaderVodafone -> {
                        menuOperatorOnClick("Vodafone")
                    }
                    R.id.opheaderMasMovil -> {
                        menuOperatorOnClick("MasMovil")
                    }
                    R.id.opheaderOMV -> {
                        menuOperatorOnClick("OMV")
                    }
                }

                true
            })


            // show icons on popup menu
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                popup.setForceShowIcon(true)
//            }else{
            try {
                val fields = popup.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field[popup]
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }*/
            //}
            //popup.show()
            //}
        }


}
