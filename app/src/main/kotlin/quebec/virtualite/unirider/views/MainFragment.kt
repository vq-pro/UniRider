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

    private lateinit var wheelTotalMileage: TextView
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

        wheelTotalMileage = view.findViewById(R.id.total_mileage)

        connectDb {
            set(wheelList, getSortedWheelItems(db.getWheelList()))
            wheelTotalMileage.text = calculateTotalMileage().toString()
        }
    }

    fun onDisplayWheel() = { view: View, item: WheelRow ->

        val wheelName = view.findViewById<TextView?>(R.id.row_name)
        wheelName.text = item.name()

        val wheelMileage = view.findViewById<TextView?>(R.id.row_mileage)
        wheelMileage.text = item.mileage().toString()
    }

    fun onSelectWheel() = { _: View, index: Int ->
        navigateTo(
            R.id.action_MainFragment_to_WheelViewFragment,
            Pair(WheelViewFragment.PARAMETER_WHEEL_NAME, wheelList[index].name())
        )
    }

    private fun calculateTotalMileage(): Int {
        var totalMileage = 0
        wheelList.forEach { wheel ->
            totalMileage += wheel.mileage()
        }

        return totalMileage
    }

    private fun getSortedWheelItems(wheelList: List<WheelEntity>): List<WheelRow> {
        return wheelList
            .stream()
            .map { wheel ->
                WheelRow(wheel.name, wheel.mileage)
            }
            .sorted { itemA, itemB ->
                val byMileage = itemB.mileage().compareTo(itemA.mileage())
                if (byMileage != 0) byMileage else itemA.name().compareTo(itemB.name())
            }
            .collect(toList())
    }
}
