package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity

open class WheelChargeFragment : BaseFragment() {

    internal lateinit var textName: TextView
    internal lateinit var textWhPerKm: TextView

    internal var parmWheelId: Long? = 0
    internal var parmWhPerKm: Float? = 0f
    internal var wheel: WheelEntity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        parmWhPerKm = arguments?.getFloat(PARAMETER_WH_PER_KM)
        return inflater.inflate(R.layout.wheel_charge_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textName = view.findViewById(R.id.view_name)
        textWhPerKm = view.findViewById(R.id.view_wh_per_km)

        external.runDB {
            wheel = it.getWheel(parmWheelId!!)

            textName.text = wheel!!.name
            textWhPerKm.text = "$parmWhPerKm ${fragments.string(R.string.label_wh_per_km)}"
        }
    }
}
