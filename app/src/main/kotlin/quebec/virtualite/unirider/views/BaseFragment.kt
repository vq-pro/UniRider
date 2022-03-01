package quebec.virtualite.unirider.views

import quebec.virtualite.commons.android.views.CommonFragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.ExternalServices

open class BaseFragment : CommonFragment() {

    override fun getExternalService(): ExternalServices {
        return ExternalServices(this)
    }

    override fun getWaitMessageString(): Int {
        return R.string.dialog_wait
    }
}
