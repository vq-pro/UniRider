package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import quebec.virtualite.unirider.R

class ScanFragment : Fragment() {

    private val devicesContents: MutableList<String> = mutableListOf()

    private lateinit var scanButton: Button
    private lateinit var devicesListView: ListView
    private lateinit var devicesAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? {
        return inflater.inflate(R.layout.scan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        devicesAdapter = ArrayAdapter(view.context, R.layout.scan_item, devicesContents)
        devicesListView = view.findViewById(R.id.devices)
        devicesListView.adapter = devicesAdapter
        devicesListView.isEnabled = false

        scanButton = view.findViewById(R.id.scan)
        scanButton.setOnClickListener(onScanButton())
    }

    internal fun onScanButton(): (View) -> Unit {
        return {
            devicesAdapter.add("toto")
            devicesListView.isEnabled = true
        }
    }
}
