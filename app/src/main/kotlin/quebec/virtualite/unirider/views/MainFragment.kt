package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.utils.WidgetUtils
import java.util.stream.Collectors.toList

open class MainFragment : BaseFragment() {

    internal val wheelList = ArrayList<String>()

    private var widgets = WidgetUtils()

    private lateinit var wheels: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectDb()

        subThread {
            wheelList.clear()
            wheelList.addAll(getWheelNames(db.getWheelList()))
        }

        wheels = view.findViewById(R.id.wheels) as ListView
        wheels.isEnabled = true
        widgets.listAdapter(wheels, view, R.layout.wheels_item, wheelList)
        widgets.setOnItemClickListener(wheels, onSelectWheel())
    }

    fun onSelectWheel() = { _: View, index: Int ->
        navigateTo(
            R.id.action_MainFragment_to_WheelFragment,
            Pair(WheelFragment.PARAMETER_WHEEL_NAME, wheelList[index])
        )
    }

    private fun getWheelNames(wheelList: List<WheelEntity>): List<String> {
        return wheelList
            .stream()
            .map { wheel -> wheel.name }
            .sorted()
            .collect(toList())
    }
}
