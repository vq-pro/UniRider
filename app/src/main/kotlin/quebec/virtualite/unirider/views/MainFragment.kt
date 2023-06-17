package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import quebec.virtualite.commons.android.utils.ArrayListUtils.addTo
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import java.util.stream.Collectors.toList


open class MainFragment : BaseFragment() {

    private val SOLD_ENTRY = "<Sold>"
    private val NEW_ENTRY = "<New>"

    internal val wheelList = ArrayList<WheelRow>()

    lateinit var lvWheels: ListView
    lateinit var textTotalMileage: TextView

    var showSoldWheels = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvWheels = view.findViewById(R.id.wheels) as ListView
        textTotalMileage = view.findViewById(R.id.total_mileage)

        widgets.enable(lvWheels)
        widgets.multifieldListAdapter(lvWheels, view, R.layout.wheels_item, wheelList, onDisplayWheel())
        widgets.setOnItemClickListener(lvWheels, onSelectWheel())

        showWheels()
    }

    fun onDisplayWheel() = { view: View, item: WheelRow ->
        val textName = view.findViewById<TextView?>(R.id.row_name)
        textName.text = item.name()

        val textMileage = view.findViewById<TextView?>(R.id.row_mileage)
        textMileage.text = if (item.name() == NEW_ENTRY) "" else "${item.mileage()}"
    }

    fun onSelectWheel() = { _: View, index: Int ->
        when (wheelList[index].name()) {
            NEW_ENTRY -> addWheel()
            SOLD_ENTRY -> {
                showSoldWheels = !showSoldWheels
                showWheels()
            }

            else -> viewWheel(wheelList[index])
        }
    }

    open fun showWheels() {
        external.runDB { db ->
            val wheels = db.getWheels()
            var wheelEntries = getSortedWheelItems(wheels.filter { !it.isSold })
            val soldWheels = getSortedWheelItems(wheels.filter { it.isSold })

            if (soldWheels.isNotEmpty()) {
                if (showSoldWheels) {
                    wheelEntries = addTo(
                        wheelEntries,
                        WheelRow(0, SOLD_ENTRY, 0),
                    )
                    for (soldWheel in soldWheels) {
                        wheelEntries = addTo(
                            wheelEntries,
                            WheelRow(soldWheel.id(), "- " + soldWheel.name(), soldWheel.mileage())
                        )
                    }
                } else {
                    wheelEntries = addTo(
                        wheelEntries,
                        WheelRow(0, SOLD_ENTRY, soldWheels.map { it.mileage() }.sum()),
                    )
                }
            }
            wheelEntries = addTo(wheelEntries, WheelRow(0, NEW_ENTRY, 0))

            fragments.runUI {
                setList(wheelList, wheelEntries)
                textTotalMileage.text = "${wheelEntries.map { it.mileage() }.sum()}"
            }
        }

    }

    private fun addWheel() {
        fragments.navigateTo(
            R.id.action_MainFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_ID, 0L)
        )
    }

    private fun getSortedWheelItems(wheelList: List<WheelEntity>): List<WheelRow> {
        return wheelList
            .stream()
            .map { wheel -> WheelRow(wheel.id, wheel.name, wheel.totalMileage()) }
            .sorted(sortWheelsByMileageDescAndNameAsc())
            .collect(toList())
    }

    private fun sortWheelsByMileageDescAndNameAsc() = { rowA: WheelRow, rowB: WheelRow ->
        val byMileage = rowB.mileage().compareTo(rowA.mileage())
        if (byMileage != 0)
            byMileage
        else
            rowA.name().compareTo(rowB.name())
    }

    private fun viewWheel(wheel: WheelRow) {
        fragments.navigateTo(
            R.id.action_MainFragment_to_WheelViewFragment,
            Pair(PARAMETER_WHEEL_ID, wheel.id())
        )
    }
}
