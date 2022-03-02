package quebec.virtualite.unirider.test

import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.rule.ActivityTestRule
import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.Rule
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.simulation.WheelConnectorSimulation
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.applicationContext
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.back
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.currentFragment
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.enter
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRow
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRows
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasSelectedText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isDisabled
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isEmpty
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.longClick
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.start
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.stop
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.database.impl.WheelDbImpl
import quebec.virtualite.unirider.views.MainActivity
import quebec.virtualite.unirider.views.MainFragment
import quebec.virtualite.unirider.views.WheelDeleteConfirmationFragment
import quebec.virtualite.unirider.views.WheelEditFragment
import quebec.virtualite.unirider.views.WheelRow
import quebec.virtualite.unirider.views.WheelViewFragment
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.util.stream.Collectors.toList

class Steps {

    private val NEW_WHEEL_ENTRY = "<New>"

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private val db = WheelDbImpl(applicationContext())
    private val mapWheels = HashMap<String, WheelEntity>()

    private lateinit var mainActivity: MainActivity

    private var expectedDeviceName: String = ""
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

    @When("I add a new wheel")
    fun addNewWheel() {
        selectedWheel = WheelEntity(0L, "", "", "", 0, 0, 0f, 0f)
        selectListViewItem(R.id.wheels, "name", NEW_WHEEL_ENTRY)
    }

    @Then("I am back to the main screen and the wheel is gone")
    fun backOnMainScreenAndWheelIsGone() {
        assertThat(currentFragment(mainActivity), equalTo(MainFragment::class.java))
        assertThat(R.id.wheels, not(hasRow(WheelRow(selectedWheel.id, selectedWheel.name, selectedWheel.mileage))))
    }

    @Then("it shows that every field is editable")
    fun itShowsThatEveryFieldIsEditable() {
        assertThat(currentFragment(mainActivity), equalTo(WheelEditFragment::class.java))
    }

    @Then("^it shows the updated name and a mileage of (.*?) on the main view$")
    fun itShowsTheUpdatedNameAndMileageOnTheMainView(expectedMileage: Int) {
        assertThat(currentFragment(mainActivity), equalTo(MainFragment::class.java))
        assertThat(
            R.id.wheels,
            hasRow(WheelRow(selectedWheel.id, updatedWheel.name, expectedMileage))
        )
    }

    @Then("^the mileage is updated to (.*?)$")
    fun mileageUpdatedTo(expectedMileage: Int) {
        assertThat(R.id.view_mileage, hasText("$expectedMileage"))
    }

    @Then("^the voltage is set to (.*?)V and battery to (.*?)%$")
    fun voltageAndBatteryTo(expectedVoltage: Float, expectedBattery: Float) {
        assertThat(R.id.edit_voltage, hasText("$expectedVoltage"))
        assertThat(R.id.view_battery, hasText("$expectedBattery%"))
    }

    @When("^I reuse the name (.*?)$")
    fun reuseTheWheelName(newName: String) {
        setText(R.id.edit_name, newName)
    }

    @Then("I see my wheels and their mileage:")
    fun seeMyWheelsAndTheirMileage(expectedWheels: DataTable) {
        assertThat(expectedWheels.topCells(), equalTo(listOf("Name", "Mileage")))
        val expectedRows = expectedWheels.cells(1)
            .stream()
            .map { row ->
                val name = row[0]
                val mileage = intOf(row[1])
                val id = if (name == NEW_WHEEL_ENTRY) 0 else mapWheels[name]!!.id

                WheelRow(id, name, mileage)
            }
            .collect(toList())

        assertThat(R.id.wheels, hasRows(expectedRows))
    }

    @Then("I see the total mileage")
    fun seeTotalMileage() {
        var totalMileage = 0
        mapWheels.forEach { (_, wheel) -> totalMileage += (wheel.totalMileage()) }

        assertThat(R.id.total_mileage, hasText("$totalMileage"))
    }

    @Then("^the selected entry is (.*?)$")
    fun selectedEntryIs(expectedEntry: String) {
        assertThat(R.id.wheels, hasSelectedText(expectedEntry))
    }

    @When("I set these new values:")
    fun setNewWheelValues(newValues: DataTable) {

        val mapDetailToId = mapOf(
            Pair("Name", R.id.edit_name),
            Pair("Previous Mileage", R.id.edit_premileage),
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
            null,
            null,
            parseInt(mapEntity["Previous Mileage"]!!),
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

    @Then("the details view shows the details for that wheel")
    fun inDetailsView() {
        assertThat(currentFragment(mainActivity), equalTo(WheelViewFragment::class.java))
        assertThat(R.id.view_name, hasText(selectedWheel.name))
    }

    @Then("it blanks the displays")
    fun blanksTheDisplays() {
        assertThat(R.id.edit_voltage, isEmpty())
        assertThat(R.id.view_battery, isEmpty())
    }

    @When("I blank the mileage")
    fun blankWheelMileage() {
        setText(R.id.edit_mileage, " ")
    }

    @When("I blank the name")
    fun blankWheelName() {
        setText(R.id.edit_name, " ")
    }

    @When("I blank the maximum voltage")
    fun blankWheelMaximumVoltage() {
        setText(R.id.edit_voltage_max, " ")
    }

    @When("I blank the minimum voltage")
    fun blankWheelMinimumVoltage() {
        setText(R.id.edit_voltage_min, " ")
    }

    @When("I blank the previous mileage")
    fun blankWheelPreMileage() {
        setText(R.id.edit_premileage, " ")
    }

    @Then("^the wheel's Bluetooth name is undefined$")
    fun bluetoothNameUndefined() {
        assertThat(selectedWheel.btName, equalTo(null))
        assertThat(selectedWheel.btAddr, equalTo(null))
    }

    @Then("^the wheel's Bluetooth name is updated$")
    fun bluetoothNameUpdated() {
        assertThat(R.id.view_bt_name, hasText(expectedDeviceName))
    }

    @Then("I can enter the details for that wheel")
    fun canEnterDetailsForNewWheel() {
        assertThat(currentFragment(mainActivity), equalTo(WheelEditFragment::class.java))
    }

    @When("I change nothing")
    fun changeNothing() {
    }

    @When("I change the mileage")
    fun changeWheelMileage() {
        setText(R.id.edit_mileage, "123")
    }

    @When("I change the minimum voltage")
    fun changeWheelVoltageMin() {
        setText(R.id.edit_voltage_min, "1.2")
    }

    @When("I change the maximum voltage")
    fun changeWheelVoltageMax() {
        setText(R.id.edit_voltage_max, "2.4")
    }

    @When("I change the name")
    fun changeWheelName() {
        setText(R.id.edit_name, "Toto")
    }

    @When("I change the previous mileage")
    fun changeWheelPreMileage() {
        setText(R.id.edit_premileage, "123")
    }

    @When("I confirm the deletion")
    fun confirmDelete() {
        assertThat(currentFragment(mainActivity), equalTo(WheelDeleteConfirmationFragment::class.java))
        click(R.id.button_delete_confirmation)
    }

    @When("I delete the wheel")
    fun deleteWheel() {
        longClick(R.id.button_delete)
    }

    @Then("^the details view shows the (.*?) with a mileage of (.*?)$")
    fun detailsViewShowsNameAndMileage(expectedName: String, expectedMileage: Int) {
        assertThat(R.id.view_name, hasText(expectedName))
        assertThat(R.id.view_mileage, hasText("$expectedMileage"))
    }

    @Then("^it displays a percentage of (.*?)$")
    fun displaysPercentage(percentage: String) {
        assertThat(R.id.view_battery, hasText(percentage))
    }

    @When("I edit the wheel")
    fun editWheel() {
        click(R.id.button_edit)
        assertThat(currentFragment(mainActivity), equalTo(WheelEditFragment::class.java))
    }

    @Given("this connected wheel:")
    fun givenThisConnectedWheel(wheel: DataTable) {
        givenTheseWheelsAreConnected(wheel)
    }

    @Given("these wheels:")
    fun givenTheseWheels(wheels: DataTable) {
        assertThat(wheels.topCells(), equalTo(listOf("Name", "Mileage", "Voltage Min", "Voltage Max")))

        val wheelEntities = wheels.cells(1)
            .stream()
            .map { row ->
                val name = row[0]
                val mileage = parseInt(row[1])
                val voltageMin = parseVoltage(row[2])
                val voltageMax = parseVoltage(row[3])

                WheelEntity(0, name, null, null, 0, mileage, voltageMin, voltageMax)
            }
            .collect(toList())

        db.saveWheels(wheelEntities)

        updateMapWheels()
    }

    @Given("these wheels are connected:")
    fun givenTheseWheelsAreConnected(wheels: DataTable) {
        assertThat(wheels.topCells(), equalTo(listOf("Name", "Bt Name", "Bt Address")))

        wheels.cells(1)
            .forEach { row ->
                val name = row[0]
                val btName = row[1]
                val btAddress = row[2]

                db.findWheel(name)?.let {
                    db.saveWheel(WheelEntity(it.id, name, btName, btAddress, it.premileage, it.mileage, it.voltageMin, it.voltageMax))
                }
            }

        updateMapWheels()
    }

    @Given("this wheel:")
    fun givenThisWheel(wheel: DataTable) {
        givenTheseWheels(wheel)
    }

    @Given("this wheel is connected:")
    fun givenThisWheelIsConnected(wheel: DataTable) {
        givenTheseWheelsAreConnected(wheel)
    }

    @Then("we go back to the main view")
    fun weGoBackToMainView() {
        back(R.id.view_name)
    }

    @Then("the wheel can be saved")
    fun wheelCanBeSaved() {
        assertThat(R.id.button_save, isEnabled())
    }

    @Then("the wheel cannot be saved")
    fun wheelCannotBeSaved() {
        assertThat(R.id.button_save, isDisabled())
    }

    @Given("^the (.*?) has a previous mileage of (.*?)$")
    fun wheelHasPreviousMileage(name: String, premileage: Int) {
        db.findWheel(name)?.let {
            db.saveWheel(WheelEntity(it.id, it.name, it.btName, it.btAddr, premileage, it.mileage, it.voltageMin, it.voltageMax))
            updateMapWheels()
        }
    }

    @Then("the wheel was added")
    fun wheelWasAdded() {
        selectedWheel = db.findWheel(updatedWheel.name)!!

        assertThat(selectedWheel.name, equalTo(updatedWheel.name))
        assertThat(selectedWheel.mileage, equalTo(updatedWheel.mileage))
        assertThat(selectedWheel.voltageMax, equalTo(updatedWheel.voltageMax))
        assertThat(selectedWheel.voltageMin, equalTo(updatedWheel.voltageMin))
    }

    @Then("the wheel was updated")
    fun wheelWasUpdated() {
        val wheel = db.getWheel(selectedWheel.id)
        assertThat(wheel, equalTo(updatedWheel))
    }

    @When("^I connect to the (.*?)$")
    fun whenConnectTo(deviceName: String) {
        click(R.id.button_connect)
        selectListViewItem(R.id.devices, deviceName)

        expectedDeviceName = deviceName
    }

    @When("I reconnect to the wheel")
    fun whenReconnectToWheel() {
        click(R.id.button_connect)
    }

    @When("^I enter a voltage of (.*?)V$")
    fun whenEnterVoltage(voltage: Float) {
        enter(R.id.edit_voltage, voltage.toString())
    }

    @When("^I select the (.*?)$")
    fun whenSelect(wheelName: String) {
        selectedWheel = db.findWheel(wheelName)!!
        selectListViewItem(R.id.wheels, "name", wheelName)
    }

    @Given("this simulated device:")
    fun simulatedDevice(device: DataTable) {
        assertThat(device.topCells(), equalTo(listOf("Bt Name", "Bt Address", "Mileage", "Voltage")))
        val deviceFields = device.cells(1)[0]

        WheelConnectorSimulation.setDevice(Device(deviceFields[0], deviceFields[1]))
        WheelConnectorSimulation.setMileage(parseFloat(deviceFields[2]))
        WheelConnectorSimulation.setVoltage(parseVoltage(deviceFields[3]))
    }

    private fun parseVoltage(value: String): Float {
        assertThat(value, endsWith("V"))
        return parseFloat(value.substring(0, value.length - 1))
    }

    private fun updateMapWheels() {
        db.getWheels().forEach { wheel ->
            mapWheels[wheel.name] = wheel
        }
    }
}
