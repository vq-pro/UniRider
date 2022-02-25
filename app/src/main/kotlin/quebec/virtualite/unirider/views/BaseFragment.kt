package quebec.virtualite.unirider.views

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quebec.virtualite.unirider.bluetooth.WheelScanner
import quebec.virtualite.unirider.database.WheelDb

open class BaseFragment : Fragment() {

    internal lateinit var db: WheelDb
    internal lateinit var scanner: WheelScanner

    internal open fun connectDb(function: () -> Unit) {
        db = MainActivity.db
        subThread(function)
    }

    internal open fun connectScanner() {
        scanner = MainActivity.scanner
    }

    internal open fun navigateBack(nb: Int = 1) {
        if (nb < 1)
            throw RuntimeException("Cannot go back $nb times")

        var i = nb
        while (i-- > 0)
            findNavController().popBackStack()
    }

    internal open fun navigateTo(id: Int, param: Pair<String, Any>) {
        findNavController().navigate(id, bundleOf(param.first to param.second))
    }

    // FIXME-0 Rename to runBackground
    internal open fun subThread(function: () -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            function()
        }
    }

    internal open fun runDb(function: () -> Unit) {
        subThread(function)
    }

    // FIXME-0 Rename to runUI
    internal open fun uiThread(function: () -> Unit) {
        activity?.runOnUiThread(function)
    }
}
