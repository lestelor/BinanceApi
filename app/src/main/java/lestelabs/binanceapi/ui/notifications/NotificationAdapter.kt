package lestelabs.binanceapi.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_notification.view.*
import lestelabs.binanceapi.R


class NotificationAdapter(context: Context, notifications: List<String>) :
    ArrayAdapter<String?>(context, 0, notifications) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Get the data item for this position
        val notification: String? = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view

        val view: View = convertView?: LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        // Lookup view for data population
        //val tvNotification = view.findViewById(R.id.list_item) as TextView

        // Populate the data into the template view using the data object
        notification?.let {view.tvItemNotification.text =  notification}

        // Return the completed view to render on screen
        return view
    }
}