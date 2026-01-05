package quebec.virtualite.unirider.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import quebec.virtualite.commons.android.utils.CollectionUtils.addTo
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import java.util.stream.Collectors.toList

open class MainFragment : BaseFragment() {

    private val SOLD_ENTRY = "<Sold>"
    private val NEW_ENTRY = "<New>"

    internal val wheelList = ArrayList<WheelRow>()

    lateinit var lvWheels: ListView
    lateinit var textTotalMileage: TextView

    var labelKm = ""
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

        labelKm = fragments.string(R.string.label_km)

        showWheels()
    }

    fun onDisplayWheel() = { view: View, item: WheelRow ->
        val textName = view.findViewById<TextView?>(R.id.row_name)
        textName.text = item.name()

        val textMileage = view.findViewById<TextView?>(R.id.row_mileage)
        textMileage.text = when {
            item.name() == NEW_ENTRY -> ""
            item.name() == SOLD_ENTRY && item.mileage() == 0 -> ""
            else -> "${item.mileage()} $labelKm"
        }
    }

    fun onSelectWheel() = { _: View, index: Int ->
        when (wheelList[index].name()) {
            NEW_ENTRY -> addWheel()
            SOLD_ENTRY -> toggleSoldWheels()
            else -> viewWheel(wheelList[index])
        }
    }

    @SuppressLint("SetTextI18n")
    open fun showWheels() {
        external.runDB { db ->
            val wheels = db.getWheels()
            var activeWheelList = getSortedWheelItems(wheels.filter { !it.isSold })
            val soldWheels = getSortedWheelItems(wheels.filter { it.isSold })

            if (soldWheels.isNotEmpty()) {
                if (showSoldWheels) {
                    activeWheelList = addTo(
                        activeWheelList,
                        WheelRow(0, SOLD_ENTRY, 0),
                    )
                    for (soldWheel in soldWheels) {
                        activeWheelList = addTo(
                            activeWheelList, WheelRow(soldWheel.id(), "- " + soldWheel.name(), soldWheel.mileage())
                        )
                    }
                } else {
                    activeWheelList = addTo(
                        activeWheelList,
                        WheelRow(0, SOLD_ENTRY, soldWheels.sumOf { it.mileage() }),
                    )
                }
            }
            activeWheelList = addTo(activeWheelList, WheelRow(0, NEW_ENTRY, 0))

            fragments.runUI {
                widgets.setListViewEntries(lvWheels, wheelList, activeWheelList)
                textTotalMileage.text = "${activeWheelList.sumOf { it.mileage() }} $labelKm"
            }
        }
    }

    private fun addWheel() {
        wheel = null
        fragments.navigateTo(R.id.action_MainFragment_to_WheelEditFragment)
    }

    private fun getSortedWheelItems(wheelList: List<WheelEntity>): List<WheelRow> {
        return wheelList.stream().map { wheel -> WheelRow(wheel.id, wheel.name, wheel.totalMileage()) }
            .sorted(sortWheelsByMileageDescAndNameAsc()).collect(toList())
    }

    private fun sortWheelsByMileageDescAndNameAsc() = { rowA: WheelRow, rowB: WheelRow ->
        val byMileage = rowB.mileage().compareTo(rowA.mileage())
        if (byMileage != 0) byMileage
        else rowA.name().compareTo(rowB.name())
    }

    private fun toggleSoldWheels() {
        wheel = null
        showSoldWheels = !showSoldWheels
        showWheels()
    }

    private fun viewWheel(wheelRow: WheelRow) {
        external.runDB { db ->
            wheel = db.getWheel(wheelRow.id())
            fragments.navigateTo(R.id.action_MainFragment_to_WheelViewFragment)
        }
    }
}
