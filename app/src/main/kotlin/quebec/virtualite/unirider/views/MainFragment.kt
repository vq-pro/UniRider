package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import quebec.virtualite.commons.android.utils.ArrayListUtils.set
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import java.util.stream.Collectors.toList

open class MainFragment : BaseFragment() {

    internal val wheelList = ArrayList<WheelRow>()

    private var widgets = WidgetUtils()

    private lateinit var wheelTotalDistance: TextView
    private lateinit var wheels: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wheels = view.findViewById(R.id.wheels) as ListView
        wheels.isEnabled = true
        widgets.multifieldListAdapter(wheels, view, R.layout.wheels_item, wheelList, onDisplayWheel())
        widgets.setOnItemClickListener(wheels, onSelectWheel())

        wheelTotalDistance = view.findViewById(R.id.total_distance)

        connectDb {
            set(wheelList, getSortedWheelItems(db.getWheelList()))
            wheelTotalDistance.text = calculateTotalDistance().toString()
        }
    }

    fun onDisplayWheel() = { view: View, item: WheelRow ->

        val wheelName = view.findViewById<TextView?>(R.id.row_name)
        wheelName.text = item.name()

        val wheelDistance = view.findViewById<TextView?>(R.id.row_distance)
        wheelDistance.text = item.distance().toString()
    }

    fun onSelectWheel() = { _: View, index: Int ->
        navigateTo(
            R.id.action_MainFragment_to_WheelFragment,
            Pair(WheelFragment.PARAMETER_WHEEL_NAME, wheelList[index].name())
        )
    }

    private fun calculateTotalDistance(): Int {
        var totalDistance = 0
        wheelList.forEach { wheel ->
            totalDistance += wheel.distance()
        }

        return totalDistance
    }

    private fun getSortedWheelItems(wheelList: List<WheelEntity>): List<WheelRow> {
        return wheelList
            .stream()
            .map { wheel ->
                WheelRow(wheel.name, wheel.distance)
            }
            .sorted { itemA, itemB ->
                val byDistance = itemB.distance().compareTo(itemA.distance())
                if (byDistance != 0) byDistance else itemA.name().compareTo(itemB.name())
            }
            .collect(toList())
    }
}
