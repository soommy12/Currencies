package pl.bgn.currencies.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData

class ConnectionLiveData(private val context: Context): LiveData<Boolean>() {

    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(networkReceiver)
    }

    private val networkReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if(it.extras != null) {
                    val activeNetwork = it.extras?.get(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo
                    val isConnected = activeNetwork.isConnected
                    if(isConnected) {
                        postValue(true)
                    } else postValue(false)

                }
            }
        }
    }

}