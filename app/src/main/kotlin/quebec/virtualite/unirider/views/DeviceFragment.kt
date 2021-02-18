package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.Device
import quebec.virtualite.unirider.services.DeviceConnector
import quebec.virtualite.unirider.services.WheelData
import quebec.virtualite.unirider.services.WheelViewModel

class DeviceFragment : Fragment() {

    private lateinit var textName: TextView
    private lateinit var textAddress: TextView
    private lateinit var textBattery: TextView
    private lateinit var textVoltage: TextView

    private lateinit var device: Device

//    private val connector: DeviceConnector = MainActivity.connector
    private val viewModel: WheelViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.device_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        device = Device(arguments?.getString("name")!!, arguments?.getString("address")!!)

        textName = view.findViewById(R.id.device_name)
        textName.text = device.name

        textAddress = view.findViewById(R.id.device_address)
        textAddress.text = device.address

        textBattery = view.findViewById(R.id.device_battery)
        textVoltage = view.findViewById(R.id.device_voltage)

        viewModel.selectedWheel.observe(viewLifecycleOwner,
            { wheelData ->
                textBattery.text = wheelData.battery.toString()
                textVoltage.text = wheelData.voltage.toString()
            })

//        connector.connect(device) { wheelData: WheelData ->

//            viewModel.selectedWheel.value = wheelData
//            viewModel.selectWheel(wheelData)

//            notifier?.onUpdateDisplay(wheelData)

//            activity?.runOnUiThread({
//                textBattery.text = wheelData.battery.toString()
//                textVoltage.text = wheelData.voltage.toString()
//            })
//        }
    }
}
