package lestelabs.binanceapi.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import lestelabs.binanceapi.R

class DialogFragment(): DialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        val builder = AlertDialog.Builder(getContext())
        builder.setTitle("Title")
        builder.setView(R.layout.item_place_order)
        return builder.create()
    }
}