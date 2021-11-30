package lestelabs.binanceapi.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_stream.view.*
import lestelabs.binanceapi.R
import lestelabs.binanceapi.data.streams.datasource.Candlestick

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
    }

    class StreamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(candlestick: Candlestick) {

            // Set Stream Info
            itemView.recycler_name.text = candlestick.stick
            itemView.recycler_price.text = "price: " + "%.5f".format(candlestick.close.toDouble())
            itemView.recycler_owned.text = "por implementar"
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