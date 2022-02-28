package quebec.virtualite.commons.views

import android.app.Dialog
import android.app.ProgressDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// FIXME-1 Get rid of these two dependencies
import quebec.virtualite.unirider.R

open class FragmentServices(val fragment: Fragment) {

    private val BACK_ON_CANCEL = true
    private val STAY_IN_FRAGMENT = false

    internal open fun navigateBack(nb: Int = 1) {
        if (nb < 1)
            throw RuntimeException("Cannot go back $nb times")

        var i = nb
        while (i-- > 0)
            fragment.findNavController().popBackStack()
    }

    open fun runBackground(function: () -> Unit) {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            function()
        }
    }

    open fun runWithWaitDialog(function: (() -> Unit)?) {

        val waitDialog = showWaitDialog(STAY_IN_FRAGMENT)

        runBackground {
            function!!()

            runUI {
                waitDialog.hide()
            }
        }
    }

    open fun runUI(function: () -> Unit) {
        fragment.activity!!.runOnUiThread(function)
    }

    private fun showWaitDialog(backOnCancel: Boolean): Dialog {
        val dialog = ProgressDialog(fragment.activity)
        dialog.setMessage(fragment.activity!!.applicationContext.getString(R.string.dialog_wait))
        if (backOnCancel)
            dialog.setOnCancelListener {
                navigateBack()
            }
        dialog.show()

        return dialog
    }
}
