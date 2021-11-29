package lestelabs.binanceapi.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import lestelabs.binanceapi.data.streams.model.Stream
import kotlinx.android.synthetic.main.item_stream.view.*
import lestelabs.binanceapi.R
import lestelabs.binanceapi.binance.api.client.domain.market.Candlestick
import java.text.NumberFormat

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
        holder.bindTo(getItem(position))
    }

    class StreamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(candlestick: Candlestick) {

            // Set Stream Info
            itemView.title.text = candlestick.bOpen
            itemView.description.text = candlestick.fVolume

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
                //return oldItem.id == newItem.id
                return true
            }

            override fun areContentsTheSame(oldItem: Candlestick, newItem: Candlestick): Boolean {
                //return oldItem == newItem
                return true
            }
        }
    }
}