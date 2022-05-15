package quebec.virtualite.unirider.views

import quebec.virtualite.commons.android.views.CommonFragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.ExternalServices

open class BaseFragment : CommonFragment<ExternalServices>(R.string.dialog_wait) {

    companion object {
        const val PARAMETER_WHEEL_ID = "wheelID"
    }

    fun goto(id: Int, wheel: WheelEntity) {
        fragments.navigateTo(id, Pair(PARAMETER_WHEEL_ID, wheel.id))
    }

    override fun getExternalServices(): ExternalServices {
        return ExternalServices(this)
    }
}
