package lestelabs.binanceapi.ui.notifications

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lestelabs.binanceapi.MainActivity

val TAG = "NotificationsViewModel"

interface ListenerNotificationViewModel {
    fun sendNotification(notification: String)
}


class NotificationsViewModel : ViewModel() {

/*    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text*/
    val notifications = MutableLiveData<List<String>>()

    fun notificationPutText(text: List<String>) {
            val currentNotifications = notifications.value.orEmpty()
            val totalNotifications = currentNotifications.plus(text)
            notifications.value = totalNotifications
            //Log.d(TAG, "timer notification view model ${notifications.value!!.size}")
    }

}