package quebec.virtualite.commons.views

import android.app.Dialog
import android.app.ProgressDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// FIXME-1 Get rid of these two dependencies
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.views.BaseFragment

open class FragmentServices {

    private val BACK_ON_CANCEL = true
    private val STAY_IN_FRAGMENT = false

    // FIXME-0 Pass fragment as constructor parameter instead?
    open fun runWithWaitDialog(fragment: BaseFragment?, function: (() -> Unit)?) {

        val waitDialog = showWaitDialog(fragment!!, STAY_IN_FRAGMENT)

        fragment.lifecycleScope.launch(Dispatchers.IO) {
            function!!()

            runUI(fragment) {
                waitDialog.hide()
            }
        }
    }

    open fun runUI(fragment: BaseFragment, function: () -> Unit) {
        fragment.activity!!.runOnUiThread(function)
    }

    private fun showWaitDialog(fragment: BaseFragment, backOnCancel: Boolean): Dialog {
        val dialog = ProgressDialog(fragment.activity)
        dialog.setMessage(fragment.activity!!.applicationContext.getString(R.string.dialog_wait))
        if (backOnCancel)
            dialog.setOnCancelListener {
                fragment.navigateBack()
            }
        dialog.show()

        return dialog
    }
}
