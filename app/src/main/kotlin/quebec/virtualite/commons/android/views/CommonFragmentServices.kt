package quebec.virtualite.commons.android.views

import android.app.ProgressDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class CommonFragmentServices(val fragment: CommonFragment<*>, private val idStringPleaseWait: Int) {

    private val BACK_ON_CANCEL = true
    private val STAY_IN_FRAGMENT = false

    private var waitDialog: DialogWithInstance? = null
    private var waitDialogInstance = 0

    open fun doneWaiting(function: (() -> Unit)?) {
        if (waitDialogWasDismissed())
            return

        if (waitDialog?.instance != waitDialogInstance)
            return

        hideWaitDialog()
        function!!.invoke()
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
        fragment.lifecycleScope.launch(Dispatchers.IO) {
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

    private fun hideWaitDialog() {
        runUI {
            synchronized(this) {
                waitDialog?.dialog!!.hide()
                waitDialog = null
            }
        }
    }

    private fun waitDialog(backOnCancel: Boolean, function: () -> Unit) {
        synchronized(this) {
            waitDialog = DialogWithInstance(
                ProgressDialog(fragment.activity),
                ++waitDialogInstance
            )

            waitDialog!!.dialog.setMessage(getString(idStringPleaseWait))
            if (backOnCancel)
                waitDialog!!.dialog.setOnCancelListener {
                    navigateBack()
                }

            waitDialog?.dialog!!.show()
        }

        runBackground {
            function()
        }
    }

    private fun waitDialogWasDismissed(): Boolean {
        synchronized(this) {
            return (waitDialog != null) && !waitDialog!!.dialog.isShowing
        }
    }

    private data class DialogWithInstance(
        val dialog: ProgressDialog,
        val instance: Int
    )
}
