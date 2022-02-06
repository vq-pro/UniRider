package quebec.virtualite.unirider.test

import androidx.test.rule.ActivityTestRule
import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.applicationContext
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.back
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.enter
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRow
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRows
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasSelectedText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isEmpty
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.start
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.stop
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.database.impl.WheelDbImpl
import quebec.virtualite.unirider.views.MainActivity
import quebec.virtualite.unirider.views.WheelRow
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.util.stream.Collectors.toList


class Steps {

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private val db = WheelDbImpl(applicationContext())
    private val mapWheels = HashMap<String, WheelEntity>()

    private lateinit var mainActivity: MainActivity

    private lateinit var selectedWheel: WheelEntity
    private lateinit var updatedWheel: WheelEntity

    @Before
    fun beforeScenario() {
        db.deleteAll()
    }

    @After
    fun afterScenario() {
        stop(activityTestRule)
    }

    @Then("it shows the updated name and mileage on the main view")
    fun itShowsTheUpdatedNameAndMileageOnTheMainView() {
        back(R.id.view_name)
        assertThat(R.id.wheels, hasRow(WheelRow(selectedWheel.id, updatedWheel.name, updatedWheel.mileage)))
    }

    @Then("I see my wheels and their mileage:")
    fun seeMyWheelsAndTheirMileage(expectedWheels: DataTable) {
        assertThat(expectedWheels.topCells(), equalTo(listOf("Name", "Mileage")))
        val expectedRows = expectedWheels.cells(1)
            .stream()
            .map { row ->
                val name = row[0]
                val mileage = parseInt(row[1])
                WheelRow(mapWheels[name]!!.id, name, mileage)
            }
            .collect(toList())

        assertThat(R.id.wheels, hasRows(expectedRows))
    }

    @Then("I see the total mileage")
    fun seeTotalMileage() {
        assertThat(R.id.total_mileage, hasText(calculateTotalMileage().toString()))
    }

    @When("I set these new values:")
    fun setNewWheelValues(newValues: DataTable) {

        val mapDetailToId = mapOf(
            Pair("Name", R.id.edit_name),
            Pair("Mileage", R.id.edit_mileage),
            Pair("Voltage Min", R.id.edit_voltage_min),
            Pair("Voltage Max", R.id.edit_voltage_max)
        )

        val mapEntity = mutableMapOf<String, String>()
        newValues.cells(0).forEach { row ->
            val field = row[0]
            val value = row[1]
            mapEntity[field] = value

            val key = mapDetailToId[field]!!
            setText(key, value)
        }

        updatedWheel = WheelEntity(
            selectedWheel.id,
            mapEntity["Name"]!!,
            parseInt(mapEntity["Mileage"]!!),
            parseFloat(mapEntity["Voltage Min"]!!),
            parseFloat(mapEntity["Voltage Max"]!!)
        )

        click(R.id.button_save)
    }

    @When("I start the app")
    fun startApp() {
        mainActivity = start(activityTestRule)!!
    }

    @Then("it blanks the displays")
    fun thenBlanksTheDisplays() {
        assertThat(R.id.edit_voltage, isEmpty())
        assertThat(R.id.view_battery, isEmpty())
    }

    @Then("^the selected entry is (.*?)$")
    fun theSelectedEntryIs(expectedEntry: String) {
        assertThat(R.id.wheels, hasSelectedText(expectedEntry))
    }

    @Then("the details view shows the details for that wheel")
    fun inDetailsView() {
        assertThat(R.id.view_name, hasText(selectedWheel.name))
    }

    @Then("the details view shows the correct name and a mileage of that wheel")
    fun detailsViewShowsNameAndMileage() {
        assertThat(R.id.view_name, hasText(selectedWheel.name))

        val selectedWheelMileage = mapWheels[selectedWheel.name]!!.mileage
        assertThat(R.id.view_mileage, hasText("$selectedWheelMileage"))
    }

    @Then("^it displays a percentage of (.*?)$")
    fun thenDisplaysPercentage(percentage: String) {
        assertThat(R.id.view_battery, hasText(percentage))
    }

    @When("I edit the wheel")
    fun editWheel() {
        click(R.id.button_edit)
    }

    @Given("these wheels:")
    fun givenTheseWheels(wheels: DataTable) {
        assertThat(wheels.topCells(), equalTo(listOf("Name", "Voltage Min", "Voltage Max", "Mileage")))
        val wheelEntities = wheels.cells(1)
            .stream()
            .map { row ->
                WheelEntity(0, row[0], parseInt(row[3]), parseVoltage(row[1]), parseVoltage(row[2]))
            }
            .collect(toList())

        db.saveWheels(wheelEntities)

        db.getWheels().forEach { wheel ->
            mapWheels[wheel.name] = wheel
        }
    }

    @When("^I select the (.*?)$")
    fun whenSelect(wheelName: String) {
        selectedWheel = db.findWheel(wheelName)!!
        selectListViewItem(R.id.wheels, "name", wheelName)
    }

    @Then("the wheel was updated")
    fun wheelWasUpdated() {
        val wheel = db.getWheel(selectedWheel.id)
        assertThat(wheel, equalTo(updatedWheel))
    }

    @When("^I enter a voltage of (.*?)V$")
    fun whenEnterVoltage(voltage: Float) {
        enter(R.id.edit_voltage, voltage.toString())
    }

    private fun calculateTotalMileage(): Int {
        var totalMileage = 0
        mapWheels.keys.stream()
            .forEach { wheelName -> totalMileage += mapWheels[wheelName]!!.mileage }

        return totalMileage
    }

    private fun parseVoltage(value: String): Float {
        assertThat(value, endsWith("V"))
        return parseFloat(value.substring(0, value.length - 1))
    }
}
