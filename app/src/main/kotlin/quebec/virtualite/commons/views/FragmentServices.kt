package quebec.virtualite.commons.views

import android.app.ProgressDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class FragmentServices(val fragment: Fragment, val idStringPleaseWait: Int) {

    private val BACK_ON_CANCEL = true
    private val STAY_IN_FRAGMENT = false

    open fun getString(id: Int): String {
        return fragment.activity!!.applicationContext.getString(id)
    }

    internal open fun navigateBack(nb: Int = 1) {
        if (nb < 1)
            throw RuntimeException("Cannot go back $nb times")

        var i = nb
        while (i-- > 0)
            fragment.findNavController().popBackStack()
    }

    open fun runBackground(function: (() -> Unit)?) {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            function!!()
        }
    }

    open fun runDB(function: (() -> Unit)?) {
        runBackground(function)
    }

    open fun runWithWaitDialog(function: (() -> Unit)?) {
        waitDialog(STAY_IN_FRAGMENT, function!!)
    }

    open fun runWithWaitDialogAndBack(function: (() -> Unit)?) {
        waitDialog(BACK_ON_CANCEL, function!!)
    }

    open fun runUI(function: (() -> Unit)?) {
        fragment.activity!!.runOnUiThread(function)
    }

    private fun waitDialog(backOnCancel: Boolean, function: () -> Unit) {

        val waitDialog = ProgressDialog(fragment.activity)

        waitDialog.setMessage(getString(idStringPleaseWait))
        if (backOnCancel)
            waitDialog.setOnCancelListener {
                navigateBack()
            }

        waitDialog.show()

        runBackground {
            function()

            runUI {
                waitDialog.hide()
            }
        }
    }
}
