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

    private lateinit var factory: BinanceApiClientFactory
    private lateinit var restClient: BinanceApiRestClient
    private lateinit var webSocketClient: BinanceApiWebSocketClient
    private lateinit var textDashboard: TextView
    private lateinit var spinner: Spinner
    private val interval = CandlestickInterval.HOURLY

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
        //binance = Binance(requireActivity())
        //restClient = binance.initRestClient()

        val factory = BinanceApiClientFactory.newInstance("O6TtsJzwkJr2QsecVQZQNcM1KWjMKeSe6YqIFBCupGEDdP5OrwUDbQJJ3bQPDssO", "clZG1nQ5FDIcLuK0KsspwFUTzlg56Gsw6F4maYrxO8yJDcfxVUndHQfF5mPtfTBq")
        restClient = factory.newRestClient()

        //val factory = BinanceApiClientFactory.newInstance("O6TtsJzwkJr2QsecVQZQNcM1KWjMKeSe6YqIFBCupGEDdP5OrwUDbQJJ3bQPDssO", "clZG1nQ5FDIcLuK0KsspwFUTzlg56Gsw6F4maYrxO8yJDcfxVUndHQfF5mPtfTBq")
        webSocketClient = factory.newWebSocketClient()
    }

    fun init_spinner() {
        spinner = binding.spinner1

        val items = arrayOf("ADAEUR", "BTCEUR", "ETHEUR", "DOGEEUR")
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, items)
        //set the spinners adapter to the previously created one.
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                onSpinnerSelection(items[position])
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

        val candleSticks = Binance(requireContext()).getCandleSticks(restClient, symbol, interval)
        charts.printLinearGraph(binding.graphView1, candleSticks.first,candleSticks.second.first)
        charts.printLinearGraph(binding.graphView2, candleSticks.first,candleSticks.second.second)
        updateTextView(binding.textDashboard, candleSticks.second)
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
}