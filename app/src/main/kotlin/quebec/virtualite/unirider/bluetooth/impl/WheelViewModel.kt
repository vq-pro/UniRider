package quebec.virtualite.unirider.bluetooth.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WheelViewModel : ViewModel() {
    private val mutableSelectedWheel = MutableLiveData<WheelData>()
    val selectedWheel: LiveData<WheelData> get() = mutableSelectedWheel

    fun selectWheel(wheelData: WheelData) {
        mutableSelectedWheel.value = wheelData
    }
}