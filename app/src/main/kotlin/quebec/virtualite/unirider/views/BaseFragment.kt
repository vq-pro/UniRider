package quebec.virtualite.unirider.views

import quebec.virtualite.commons.android.views.CommonFragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.ExternalServices

open class BaseFragment : CommonFragment<ExternalServices>(R.string.dialog_wait) {

    override fun getExternalServices(): ExternalServices {
        return ExternalServices(this)
    }
}
