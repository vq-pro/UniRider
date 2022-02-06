package quebec.virtualite.unirider.views

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quebec.virtualite.unirider.database.WheelDb

open class BaseFragment : Fragment() {

    lateinit var db: WheelDb

    internal open fun connectDb(function: () -> Unit) {
        db = MainActivity.db

        subThread(function)
    }

    internal open fun navigateBack() {
        findNavController().popBackStack()
    }

    internal open fun navigateTo(id: Int, param: Pair<String, Any>) {
        findNavController().navigate(id, bundleOf(param.first to param.second))
    }

    internal open fun runDb(function: () -> Unit) {
        subThread(function)
    }

    internal open fun subThread(function: () -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            function()
        }
    }
}
