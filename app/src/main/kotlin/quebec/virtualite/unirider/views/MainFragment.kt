package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import quebec.virtualite.commons.android.utils.ArrayListUtils.addTo
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID
import java.util.stream.Collectors.toList


open class MainFragment : BaseFragment() {

    private val NEW_ENTRY = "<New>"
    private val NEW_ROW = WheelRow(0, NEW_ENTRY, 0)

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
        wheelTotalMileage = view.findViewById(R.id.total_mileage)

        wheels.isEnabled = true
        widgets.multifieldListAdapter(wheels, view, R.layout.wheels_item, wheelList, onDisplayWheel())
        widgets.setOnItemClickListener(wheels, onSelectWheel())

        connectDb {
            setList(wheelList, addTo(getSortedWheelItems(db.getWheels()), NEW_ROW))
            wheelTotalMileage.text = calculateTotalMileage().toString()
        }
    }

    fun onDisplayWheel() = { view: View, item: WheelRow ->

        val textName = view.findViewById<TextView?>(R.id.row_name)
        textName.text = item.name()

        val textMileage = view.findViewById<TextView?>(R.id.row_mileage)
        textMileage.text = if (item.name() == NEW_ENTRY) "" else "${item.mileage()}"
    }

    fun onSelectWheel() = { _: View, index: Int ->
        when {
            wheelList[index].name() == NEW_ENTRY -> addWheel()
            else -> viewWheel(wheelList[index])
        }
    }

    private fun addWheel() {
        navigateTo(
            R.id.action_MainFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_ID, 0L)
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
            .map { wheel -> WheelRow(wheel.id, wheel.name, wheel.mileage) }
            .sorted(sortWheelsByMileageDescAndNameAsc())
            .collect(toList())
    }

    private fun sortWheelsByMileageDescAndNameAsc() = { rowA: WheelRow, rowB: WheelRow ->
        val byMileage = rowB.mileage().compareTo(rowA.mileage())
        if (byMileage != 0) byMileage else rowA.name().compareTo(rowB.name())
    }

    private fun viewWheel(wheel: WheelRow) {
        navigateTo(
            R.id.action_MainFragment_to_WheelViewFragment,
            Pair(PARAMETER_WHEEL_ID, wheel.id())
        )
    }
}
