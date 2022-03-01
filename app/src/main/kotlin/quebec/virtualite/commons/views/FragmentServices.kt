package quebec.virtualite.commons.views

import android.app.ProgressDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.views.BaseFragment

open class FragmentServices(val fragment: BaseFragment, val idStringPleaseWait: Int) {

    private val BACK_ON_CANCEL = true
    private val STAY_IN_FRAGMENT = false

    private var waitDialog: ProgressDialog? = null

    open fun dismissWait() {
        runUI {
            waitDialog?.hide()
            waitDialog = null
        }
    }

    open fun getString(id: Int): String? {
        return fragment.activity?.applicationContext?.getString(id)
    }

    internal open fun navigateBack(nb: Int = 1) {
        runUI {
            if (nb < 1)
                throw RuntimeException("Cannot go back $nb times")

            var i = nb
            while (i-- > 0)
                fragment.findNavController().popBackStack()
        }
    }

    open fun navigateTo(id: Int, param: Pair<String, Any>) {
        runUI { fragment.findNavController().navigate(id, bundleOf(param.first to param.second)) }
    }

    open fun runBackground(function: (() -> Unit)?) {
        internalRunBackground {
            function!!()
        }
    }

    open fun runDB(function: ((WheelDb) -> Unit)?) {
        if (waitDialogWasDismissed())
            return

        internalRunBackground {
            function!!(fragment.externalServices.db())
        }
    }

    open fun runWithWait(function: (() -> Unit)?) {
        waitDialog(STAY_IN_FRAGMENT, function!!)
    }

    open fun runWithWaitAndBack(function: (() -> Unit)?) {
        waitDialog(BACK_ON_CANCEL, function!!)
    }

    open fun runUI(function: (() -> Unit)?) {
        fragment.activity?.runOnUiThread(function)
    }

    private fun internalRunBackground(function: () -> Unit) {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            function()
        }
    }

    private fun waitDialog(backOnCancel: Boolean, function: () -> Unit) {

        waitDialog = ProgressDialog(fragment.activity)
        waitDialog!!.setMessage(getString(idStringPleaseWait))
        if (backOnCancel)
            waitDialog!!.setOnCancelListener {
                navigateBack()
            }

        waitDialog!!.show()

        runBackground {
            function()
        }
    }

    private fun waitDialogWasDismissed() = waitDialog != null && !waitDialog!!.isShowing
}
