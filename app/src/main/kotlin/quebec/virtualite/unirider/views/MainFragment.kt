package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.utils.WidgetUtils

open class MainFragment : Fragment() {

    internal val wheelList = ArrayList<String>()

    private var widgets = WidgetUtils()

    private lateinit var db: WheelDb
    private lateinit var wheels: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = connectDb()

        subThread {
            wheelList.clear()
            wheelList.addAll(
                db.getWheelList().sorted()
            )
        }

        wheels = view.findViewById(R.id.wheels) as ListView
        wheels.isEnabled = true
        // FIXME 1 Recoder selon widgets
        wheels.adapter = widgets.listAdapter(view, R.layout.wheels_item, wheelList)
        widgets.setOnItemClickListener(wheels, onSelectWheel())
    }

    fun onSelectWheel() = { _: View, index: Int ->
        navigateTo(
            R.id.action_MainFragment_to_CalculatorFragment,
            Pair("wheel", wheelList[index])
        )
    }

    internal open fun connectDb(): WheelDb {
        return MainActivity.db
    }

    internal open fun navigateTo(id: Int, parms: Pair<String, String>?) {
        findNavController().navigate(id, bundleOf(parms!!.first to parms.second))
    }

    internal open fun subThread(function: () -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            function()
        }
    }
}
