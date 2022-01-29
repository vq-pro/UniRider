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

    internal open fun connectDb() {
        db = MainActivity.db
    }

    internal open fun navigateTo(id: Int, parms: Pair<String, String>?) {
        findNavController().navigate(id, bundleOf(parms!!.first to parms.second))
    }

    internal open fun subThread(function: () -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            function()
        }
    }
}
