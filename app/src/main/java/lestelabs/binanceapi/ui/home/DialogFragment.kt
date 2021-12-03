package lestelabs.binanceapi.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.item_place_order.view.*
import lestelabs.binanceapi.R

class DialogFragment(): DialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Place an Order")
        builder.setView(R.layout.item_place_order)
        return builder.create()
    }

}