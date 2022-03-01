package quebec.virtualite.commons.android.views

import android.app.ProgressDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class CommonFragmentServices(val fragment: CommonFragment<*>, val idStringPleaseWait: Int) {

    private val BACK_ON_CANCEL = true
    private val STAY_IN_FRAGMENT = false

    internal var waitDialog: ProgressDialog? = null

    open fun dismissWait() {
        runUI {
            synchronized(this) {
                waitDialog?.hide()
                waitDialog = null
            }
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

    open fun runWithWait(function: (() -> Unit)?) {
        waitDialog(STAY_IN_FRAGMENT, function!!)
    }

    open fun runWithWaitAndBack(function: (() -> Unit)?) {
        waitDialog(BACK_ON_CANCEL, function!!)
    }

    open fun runUI(function: (() -> Unit)?) {
        fragment.activity?.runOnUiThread(function)
    }

    open fun waitDialogWasDismissed(): Boolean {
        synchronized(this) {
            return (waitDialog != null) && !waitDialog!!.isShowing
        }
    }

    private fun internalRunBackground(function: () -> Unit) {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            function()
        }
    }

    private fun waitDialog(backOnCancel: Boolean, function: () -> Unit) {

        synchronized(this) {
            waitDialog = ProgressDialog(fragment.activity)
            waitDialog!!.setMessage(getString(idStringPleaseWait))
            if (backOnCancel)
                waitDialog!!.setOnCancelListener {
                    navigateBack()
                }

            waitDialog!!.show()
        }

        runBackground {
            function()
        }
    }
}
