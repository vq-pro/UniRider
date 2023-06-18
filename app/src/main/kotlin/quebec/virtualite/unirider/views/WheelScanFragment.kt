package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import kotlin.math.roundToInt

open class WheelScanFragment : BaseFragment() {

    internal lateinit var lvDevices: ListView

    internal val devices = ArrayList<BluetoothDevice>()
    internal var parmWheelId: Long? = 0
    internal var wheel: WheelEntity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        return inflater.inflate(R.layout.wheel_scan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvDevices = view.findViewById(R.id.devices)

        widgets.multifieldListAdapter(lvDevices, view, android.R.layout.simple_list_item_1, devices, onDisplayDevice())
        widgets.setOnItemClickListener(lvDevices, onSelectDevice())

        external.runDB { wheel = it.getWheel(parmWheelId!!) }
        fragments.runWithWaitAndBack { scanForDevices(view) }
    }

    fun onDisplayDevice() = { view: View, item: BluetoothDevice ->
        val textName = view.findViewById<TextView?>(android.R.id.text1)
        textName.text = item.name
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
                    db.saveWheel(
                        wheel!!.copy(
                            btName = device.name,
                            btAddr = device.address,
                            mileage = info!!.mileage.roundToInt()
                        )
                    )
                }

                fragments.navigateBack()
            }
        }
    }

    // FIXME-1 Remove view parameter
    private fun scanForDevices(view: View) {
        external.bluetooth().scan {
            fragments.doneWaiting(it) {
                val updatedDevices = ArrayList<BluetoothDevice>()
                setList(updatedDevices, devices)
                updatedDevices.add(it)

                // FIXME-0 Define new addListViewEntry()
                fragments.runUI { widgets.setListViewEntries(lvDevices, devices, updatedDevices) }
            }
        }
    }
}
