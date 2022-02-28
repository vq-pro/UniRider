package quebec.virtualite.unirider.views

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.commons.views.FragmentServices
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.database.WheelDb

open class BaseFragment : Fragment() {

    internal lateinit var connector: WheelConnector
    internal lateinit var db: WheelDb

    internal var services = FragmentServices(this, R.string.dialog_wait)
    internal var widgets = WidgetUtils()

    internal open fun initConnector() {
        connector = MainActivity.connector
    }

    internal open fun initDB(function: () -> Unit) {
        db = MainActivity.db
        runDB(function)
    }

    internal open fun navigateTo(id: Int, param: Pair<String, Any>) {
        findNavController().navigate(id, bundleOf(param.first to param.second))
    }

    internal open fun runBackground(function: () -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            function()
        }
    }

    internal open fun runDB(function: () -> Unit) {
        runBackground(function)
    }

    internal open fun runUI(function: () -> Unit) {
        activity?.runOnUiThread(function)
    }
}
