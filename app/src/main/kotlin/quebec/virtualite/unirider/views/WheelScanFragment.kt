package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.WheelScanner
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID
import java.util.stream.Collectors.toList
import kotlin.math.roundToInt

open class WheelScanFragment : BaseFragment() {

    internal val devices = ArrayList<Device>()

    internal var parmWheelId: Long? = 0
    internal var wheel: WheelEntity? = null

    private lateinit var scanner: WheelScanner
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

        connectScanner()
        scanner.scan { device ->

            devices.add(device)

            val names = devices.stream().map(Device::name).collect(toList())
            widgets.stringListAdapter(lvWheels, view, names)
        }
    }

    fun onSelectDevice(): (View, Int) -> Unit = { _: View, pos: Int ->

        val device = devices[pos]

        scanner.getDeviceInfo(device.address) { info ->
            val updatedWheel = WheelEntity(
                wheel!!.id, wheel!!.name, device.name, device.address,
                info.mileage.roundToInt(), wheel!!.voltageMin, wheel!!.voltageMax
            )

            runDb { db.saveWheel(updatedWheel) }

            uiThread { navigateBack() }
        }
    }

    internal open fun connectScanner() {
        scanner = MainActivity.scanner
    }
}
