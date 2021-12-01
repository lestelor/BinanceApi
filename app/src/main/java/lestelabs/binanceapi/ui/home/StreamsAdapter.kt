package lestelabs.binanceapi.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_after_notification.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_stream.view.*
import lestelabs.binanceapi.R
import lestelabs.binanceapi.binance.Binance
import lestelabs.binanceapi.binance.api.client.domain.account.request.CancelOrderRequest
import lestelabs.binanceapi.data.streams.datasource.Candlestick
import lestelabs.binanceapi.binance.api.client.domain.account.request.OrderRequest

import lestelabs.binanceapi.binance.api.client.domain.account.Order
import org.koin.ext.scope


/**
 * Created by alex on 07/09/2020.
 */

class StreamsAdapter : ListAdapter<Candlestick, StreamsAdapter.StreamViewHolder>(streamsDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamViewHolder {
        return StreamViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stream, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        val candlestick = getItem(position)
        if (candlestick != null ) holder.bindTo(getItem(position))
        holder.itemView.button_cancel_order.setOnClickListener {
            val binance = Binance()
            val symbol = binance.sticks[position]
            val symbolShort = symbol.substring(0,symbol.length-4)
            val openOrders: List<Order> = binance.syncClient.getOpenOrders(OrderRequest(symbol))

            if (openOrders.isNotEmpty()) {
                val firstOpenOrder = openOrders[0].orderId
                binance.syncClient.cancelOrder(CancelOrderRequest(symbol,firstOpenOrder))
                Toast.makeText(it.context,"Order $firstOpenOrder cancelled. Now reloading ...", Toast.LENGTH_LONG).show()
                /*val balancesEUR = binance.getBalance("EUR")
                holder.itemView.text_home.text = "free: " + balancesEUR[0] + " locked: " + balancesEUR[1]

                val balancesStick = binance.getBalance(symbolShort)
                holder.itemView.recycler_free.text = "free: " + "%5f".format(balancesStick[0])
                holder.itemView.recycler_locked.text = "locked: " + "%5f".format(balancesStick[1])*/
                //notifyDataSetChanged()
            } else Toast.makeText(it.context,"Not found!!", Toast.LENGTH_LONG).show()


        }
    }

    class StreamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(candlestick: Candlestick) {

            // Set Stream Info
            itemView.recycler_name.text = candlestick.stick
            itemView.recycler_price.text = "price: " + "%.5f".format(candlestick.close.toDouble())
            itemView.recycler_price_max.text = "80% max: " + "%.5f".format(candlestick.maxValue80) + candlestick.stick.takeLast(3)
            itemView.recycler_free.text = "free: " + "%.5f".format(candlestick.ownFree.toDouble())
            itemView.recycler_locked.text = "locked: " + "%.5f".format(candlestick.ownLocked.toDouble())
            itemView.recycler_eur.text = "eur: " + "%.2f".format(candlestick.ownValueEUR)
            itemView.recycler_sma.text = "sma: " + "%.5f".format(candlestick.sma.toDouble())
            itemView.recycler_rsi.text = "rsi: " + "%.2f".format(candlestick.rsi.toDouble())
            val percentage = (candlestick.sma - candlestick.close.toDouble())/(candlestick.close.toDouble())*100
            itemView.recycler_percentage.text = "%: " + "%.1f".format(percentage)

            if(candlestick.sma >= candlestick.close.toDouble()) {
                itemView.recycler_sma.setTextColor(Color.GREEN)
            } else itemView.recycler_sma.setTextColor(Color.RED)

            if(candlestick.rsi >= 70.0) {
                itemView.recycler_rsi.setTextColor(Color.RED)
            } else if (candlestick.rsi <= 30.0) itemView.recycler_rsi.setTextColor(Color.GREEN)
            else itemView.recycler_rsi.setTextColor(Color.BLACK)

            if(candlestick.ownLocked >0) itemView.recycler_locked.setTextColor(Color.BLACK)
            if(candlestick.ownFree>0) itemView.recycler_free.setTextColor(Color.BLACK)
            if(candlestick.ownValueEUR >0) itemView.recycler_eur.setTextColor(Color.BLACK)

            // Set Stream Image
/*            candlestick.thumbnailUrl
                ?.replace("{width}", "1014")
                ?.replace("{height}", "396")
                ?.let {
                    Glide.with(itemView)
                        .load(it)
                        .centerCrop()
                        .into(itemView.imageView)
                }*/
            // Set Stream Views
/*            val formattedViews = NumberFormat.getInstance().format(candlestick.viewerCount)
            itemView.viewsText.text = itemView.context.resources
                .getQuantityString(R.plurals.viewers_text, stream.viewerCount, formattedViews)*/
        }
    }

    companion object {
        private val streamsDiffCallback = object : DiffUtil.ItemCallback<Candlestick>() {

            override fun areItemsTheSame(oldItem: Candlestick, newItem: Candlestick): Boolean {
                return oldItem.stick == newItem.stick
            }

            override fun areContentsTheSame(oldItem: Candlestick, newItem: Candlestick): Boolean {
                return oldItem.stick == newItem.stick
            }
        }
    }
}