package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.Device

class ScanFragment : Fragment() {

    private val NOT_FOUND = -1

    private val deviceNames: MutableList<String> = mutableListOf()
    private val devicesInfo: MutableMap<String, Device> = mutableMapOf()

    private lateinit var scanButton: Button
    private lateinit var devicesListView: ListView
    private lateinit var devicesAdapter: ArrayAdapter<String>

//    private var scanner: DeviceScanner = MainActivity.scanner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.scan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        devicesAdapter = ArrayAdapter(view.context, R.layout.scan_item, deviceNames)
        devicesListView = view.findViewById(R.id.devices)
        devicesListView.adapter = devicesAdapter
        devicesListView.isEnabled = false
        devicesListView.setOnItemClickListener { parent, itemView, position, id ->

//            Toast.makeText(view.context, "Clicked item: " + devicesAdapter.getItem(position), Toast.LENGTH_SHORT).show()

//            scanner.stop()

            val name: String = devicesAdapter.getItem(position)!!
            val device: Device = devicesInfo.get(name)!!

            val bundle = Bundle()
            bundle.putString("name", device.name)
            bundle.putString("address", device.address)

//            findNavController().navigate(R.id.action_ScanFragment_to_DeviceFragment, bundle)
        }

        scanButton = view.findViewById(R.id.scan)
        scanButton.setOnClickListener(onScanButton())
    }

    internal fun addDevice(device: Device) {

        if (devicesAdapter.getPosition(device.name) != NOT_FOUND)
            return

        devicesInfo.put(device.name, device)
        devicesAdapter.add(device.name)
        devicesListView.isEnabled = true
    }

    internal fun onScanButton(): (View) -> Unit {
        return {
            devicesAdapter.clear()
//            scanner.scan { device -> addDevice(device) }
        }
    }
}
