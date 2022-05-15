package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import java.util.stream.Collectors.toList
import kotlin.math.roundToInt

open class WheelScanFragment : BaseFragment() {

    internal lateinit var lvWheels: ListView

    internal val devices = ArrayList<BluetoothDevice>()
    internal var parmWheelId: Long? = 0
    internal var wheel: WheelEntity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        return inflater.inflate(R.layout.wheel_scan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvWheels = view.findViewById(R.id.devices)
        widgets.setOnItemClickListener(lvWheels, onSelectDevice())

        external.runDB { wheel = it.getWheel(parmWheelId!!) }
        fragments.runWithWaitAndBack { scanForDevices(view) }
    }

    fun onSelectDevice(): (View, Int) -> Unit = { _: View, pos: Int ->
        fragments.runWithWaitAndBack { connectWithWheel(devices[pos]) }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        external.bluetooth().stopScanning()
    }

    private fun connectWithWheel(device: BluetoothDevice) {
        external.bluetooth().getDeviceInfo(device.address) { info ->
            fragments.doneWaiting(info) {
                external.runDB { db ->
                    val updatedWheel = WheelEntity(
                        wheel!!.id, wheel!!.name,
                        device.name, device.address,
                        wheel!!.premileage, info!!.mileage.roundToInt(),
                        wheel!!.wh,
                        wheel!!.voltageMin, wheel!!.voltageReserve, wheel!!.voltageMax
                    )
                    db.saveWheel(updatedWheel)
                }

                fragments.navigateBack()
            }
        }
    }

    private fun scanForDevices(view: View) {
        external.bluetooth().scan {
            fragments.doneWaiting(it) {
                devices.add(it)

                fragments.runUI {
                    val names = devices.stream().map(BluetoothDevice::name).collect(toList())
                    widgets.stringListAdapter(lvWheels, view, names)
                }
            }
        }
    }
}
