package quebec.virtualite.unirider.views

import quebec.virtualite.commons.android.views.CommonFragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.ExternalServices

open class BaseFragment : CommonFragment<ExternalServices>() {

    override fun getExternalServices(): ExternalServices {
        return ExternalServices(this)
    }

    override fun getWaitMessageStringId(): Int {
        return R.string.dialog_wait
    }
}
