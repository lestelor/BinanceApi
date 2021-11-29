package lestelabs.binanceapi.ui.dashboard

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import lestelabs.binanceapi.MainActivity
import lestelabs.binanceapi.R
import lestelabs.binanceapi.RetrieveDataInterface
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.binance.api.client.BinanceApiClientFactory
import lestelabs.binanceapi.binance.api.client.BinanceApiRestClient
import lestelabs.binanceapi.binance.api.client.BinanceApiWebSocketClient
import lestelabs.binanceapi.binance.api.client.domain.account.Account
import lestelabs.binanceapi.binance.api.client.domain.market.CandlestickInterval
import lestelabs.binanceapi.charts.Charts
import lestelabs.binanceapi.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var spinner: Spinner
    private var binance: Binance? = null
    private var listener: RetrieveDataInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard

        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        init_binance()
        init_spinner()

        return root
    }


    fun init_binance() {
        binance = listener?.retrieveDataInterface()

    }

    fun init_spinner() {
        spinner = binding.spinner1
        val sticks: Array<String> = binance?.sticks?: arrayOf()

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.

        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, sticks)
        //set the spinners adapter to the previously created one.
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                onSpinnerSelection(sticks[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // vacio
            }
        }
    }

    fun onSpinnerSelection(symbol: String) {



        val charts = Charts(requireContext())
        //val account: Account = restClient.account

        //Log.d(TAG,"binance balances " + account.balances)
        //Log.d(MainActivity.TAG,"binance balance ADA " + account.getAssetBalance("ADA").free)
        //println(account.balances)
        //println(account.getAssetBalance("ADA").free)

        //val openOrders = client.getOpenOrders(OrderRequest("ADAEUR"))
        //Log.d(TAG, "binance open orders $openOrders")

        //val candlesticksCacheExample = CandlesticksCacheExample("ETHBTC", CandlestickInterval.ONE_MINUTE);
        //Log.d(TAG, "binance candle ADA $candlesticksCacheExample")

        val candleSticks = binance?.syncClient?.let { binance?.interval?.let { it1 ->
            Binance().getCandleSticksSync(symbol)

        } }
        candleSticks?.first?.let {
            charts.printLinearGraph(binding.graphView1,
                it,candleSticks.second.first)
        }
        candleSticks?.first?.let {
            charts.printLinearGraph(binding.graphView2,
                it,candleSticks.second.second)
        }
        candleSticks?.second?.let { updateTextView(binding.textDashboard, it) }
    }


    fun updateTextView(textView: TextView, input: Pair<List<DoubleArray>, List<DoubleArray>>) {
        val value = input.first[0]
        val sma = input.first[1]
        val rsi = input.second[0]

        textView.text = "endPrice: " + value[value.size-1].toString() + " sma: " + String.format("%.5f", sma[sma.size-1]) + " rsi: " + String.format("%.5f", rsi[rsi.size-1])
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = activity as RetrieveDataInterface
            // listener.showFormula(show?);
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener.  */
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}