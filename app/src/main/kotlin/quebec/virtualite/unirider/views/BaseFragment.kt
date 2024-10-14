package quebec.virtualite.unirider.views

import quebec.virtualite.commons.android.views.CommonFragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.ExternalServices
import java.util.Locale.ENGLISH

const val NB_DECIMALS = 1
const val PARAMETER_RATES = "rates"
const val PARAMETER_SELECTED_RATE = "selectedRate"
const val PARAMETER_VOLTAGE = "voltage"

open class BaseFragment : CommonFragment<ExternalServices>(R.string.dialog_wait) {

    companion object {
        // FIXME-1 Remove the 2
        var wheel2: WheelEntity? = null
    }

    override fun getExternalServices(): ExternalServices {
        return ExternalServices(this)
    }

    internal fun textKm(value: Int?) = when {
        value != null -> "$value"
        else -> ""
    }

    internal fun textKmWithDecimal(value: Float?) = when {
        value != null -> "$value".replace("0.0", "0")
        else -> ""
    }

    internal fun textPercentageWithDecimal(percentage: Float?) = when {
        percentage != null && percentage in 0f..110f -> "%.1f".format(ENGLISH, percentage)
        else -> ""
    }

    internal fun textWhPerKm(value: Float?) = when {
        value != null -> "$value"
        else -> ""
    }
}
