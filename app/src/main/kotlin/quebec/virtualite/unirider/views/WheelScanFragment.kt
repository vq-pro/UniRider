package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.services.Device
import quebec.virtualite.unirider.services.WheelScanner
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID

open class WheelScanFragment : BaseFragment() {

    internal val devices = ArrayList<String>()

    internal var parmWheelId: Long? = 0
    internal var wheel: WheelEntity? = null

    internal lateinit var scanner: WheelScanner

    private var widgets = WidgetUtils()

    internal lateinit var lvWheels: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        return inflater.inflate(R.layout.wheel_scan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvWheels = view.findViewById(R.id.devices)

        lvWheels.isEnabled = true
        widgets.setOnItemClickListener(lvWheels, onSelectDevice())

        connectDb {
            wheel = db.getWheel(parmWheelId!!)
        }

        connectScanner { device ->
            devices.add(device.name)
            widgets.stringListAdapter(lvWheels, view, devices)
        }
    }

    fun onSelectDevice(): (View, Int) -> Unit = { _: View, pos: Int ->
        val deviceName = devices[pos]
        val b = true

        runDb {
            // FIXME-1 Add BT name
            db.saveWheel(WheelEntity(wheel!!.id, wheel!!.name, 655, wheel!!.voltageMin, wheel!!.voltageMax))
        }

        navigateBack()
    }

    internal open fun connectScanner(function: (Device) -> Unit) {
        scanner = MainActivity.scanner
        scanner.scan(function)
    }
}
