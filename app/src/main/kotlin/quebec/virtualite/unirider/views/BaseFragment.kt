package quebec.virtualite.unirider.views

import quebec.virtualite.commons.android.views.CommonFragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.ExternalServices
import java.util.Locale.ENGLISH

open class BaseFragment : CommonFragment<ExternalServices>(R.string.dialog_wait) {

    companion object {
        const val PARAMETER_WHEEL_ID = "wheelID"
        const val PARAMETER_WH_PER_KM = "whPerKm"
    }

    fun goto(id: Int, wheel: WheelEntity) {
        fragments.navigateTo(id, Pair(PARAMETER_WHEEL_ID, wheel.id))
    }

    override fun getExternalServices(): ExternalServices {
        return ExternalServices(this)
    }

    protected fun textPercentageWithDecimal(percentage: Float?) = when {
        percentage != null && percentage in 0f..100f -> "%.1f%%".format(ENGLISH, percentage)
        else -> ""
    }

    protected fun textKm(value: Int?) = when {
        value != null -> {
            val labelKm = fragments.string(R.string.label_km)
            "$value $labelKm"
        }
        else -> ""
    }

    protected fun textKmWithDecimal(value: Float?) = when {
        value != null -> {
            val labelKm = fragments.string(R.string.label_km)
            "$value $labelKm".replace("0.0", "0")
        }
        else -> ""
    }

    protected fun textWhPerKm(value: Float?) = when {
        value != null -> {
            val labelWhPerKm = fragments.string(R.string.label_wh_per_km)
            "$value $labelWhPerKm"
        }
        else -> ""
    }
}
