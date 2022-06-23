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
import org.hamcrest.Matchers.notNullValue
import org.junit.Rule
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.sim.BluetoothServicesSim
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
        selectedWheel = WheelEntity(0L, "", "", "", 0, 0, 0, 0f, 0f, 0f, 0f)
        selectListViewItem(R.id.wheels, "name", NEW_WHEEL_ENTRY)
    }

    @Then("I am back at the main screen and the wheel is gone")
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
        assertThat(R.id.wheels, hasRow(WheelRow(selectedWheel.id, updatedWheel.name, expectedMileage)))
    }

    @Then("^the km is updated to (.*?)$")
    fun kmUpdatedTo(expectedKm: Float) {
        assertThat(R.id.edit_km, hasText("$expectedKm"))
    }

    @Then("^the mileage is updated to (.*?) km$")
    fun mileageUpdatedTo(expectedMileage: Int) {
        assertThat(R.id.view_mileage, hasText("$expectedMileage km"))
    }

    @Then("^the voltage is updated to (.*?)V and the battery (.*?)%$")
    fun voltageAndBatteryUpdatedTo(expectedVoltage: Float, expectedBattery: Float) {
        assertThat(R.id.edit_voltage_actual, hasText("$expectedVoltage"))
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
            Pair("Wh", R.id.edit_wh),
            Pair("Voltage Min", R.id.edit_voltage_min),
            Pair("Voltage Reserve", R.id.edit_voltage_reserve),
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
            intOf(mapEntity["Previous Mileage"]!!),
            intOf(mapEntity["Mileage"]!!),
            intOf(mapEntity["Wh"]!!),
            floatOf(mapEntity["Voltage Max"]!!),
            floatOf(mapEntity["Voltage Min"]!!),
            floatOf(mapEntity["Voltage Reserve"]!!),
            null
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
        assertThat(R.id.edit_voltage_actual, isEmpty())
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

    @When("I blank the wh")
    fun blankWheelWh() {
        setText(R.id.edit_wh, " ")
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

    @When("I cancel the scan and go back")
    fun cancelScan() {
        back(2)
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

    @When("I change the wh")
    fun changeWheelWh() {
        setText(R.id.edit_wh, "123")
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

    @Then("^the details view shows the (.*) with a mileage of (.*) and a starting voltage of (.*)V$")
    fun detailsViewShowsNameAndMileage(expectedName: String, expectedMileage: String, expectedStartingVoltage: Float) {
        assertThat(R.id.view_name, hasText(expectedName))
        assertThat(R.id.view_mileage, hasText(expectedMileage))
        assertThat(R.id.edit_voltage_start, hasText("$expectedStartingVoltage"))
    }

    @Then("^the starting voltage is (.*)V$")
    fun startingVoltageIs(expectedStartingVoltage: Float) {
        assertThat(R.id.edit_voltage_start, hasText("$expectedStartingVoltage"))
    }

    @Then("^it displays a percentage of (.*?)$")
    fun displaysPercentage(percentage: String) {
        assertThat(R.id.view_battery, hasText(percentage))
    }

    @Then("it displays blank estimated values")
    fun displaysBlankEstimatedValues() {
        assertThat(R.id.view_remaining_range, isEmpty())
        assertThat(R.id.view_total_range, isEmpty())
        assertThat(R.id.view_wh_per_km, isEmpty())
    }

    @Then("^it displays an estimated remaining range of \"(.*?)\"$")
    fun displaysRemainingRange(range: String) {
        assertThat(R.id.view_remaining_range, hasText(range))
    }

    @Then("^it displays an estimated total range of \"(.*?)\"$")
    fun displaysTotalRange(range: String) {
        assertThat(R.id.view_total_range, hasText(range))
    }

    @Then("^it displays an estimated wh/km of \"(.*?)\"$")
    fun displaysWhPerKm(whPerKm: String) {
        assertThat(R.id.view_wh_per_km, hasText(whPerKm))
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
        assertThat(wheels.topCells(), equalTo(listOf("Name", "Mileage", "Wh", "Voltage Min", "Voltage Reserve", "Voltage Max")))

        val wheelEntities = wheels.cells(1)
            .stream()
            .map { row ->
                val name = row[0]
                val mileage = parseInt(row[1])
                val wh = parseInt(row[2])
                val voltageMin = voltageOf(row[3])
                val voltageReserve = voltageOf(row[4])
                val voltageMax = voltageOf(row[5])

                WheelEntity(0, name, null, null, 0, mileage, wh, voltageMax, voltageMin, voltageReserve, null)
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
                    db.saveWheel(
                        WheelEntity(
                            it.id, name, btName, btAddress,
                            it.premileage, it.mileage, it.wh,
                            it.voltageMax, it.voltageMin, it.voltageReserve, null
                        )
                    )
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

    @When("I go back to the main view")
    fun goBackToMainView() {
        back()
        assertThat(currentFragment(mainActivity), equalTo(MainFragment::class.java))
    }

    @When("I go back to view the wheel")
    fun goBackToViewWheel() {
        back()
        assertThat(currentFragment(mainActivity), equalTo(WheelViewFragment::class.java))
    }

    @Given("^I set the distance to (.*?)$")
    fun setDistanceTo(km: String) {
        setText(R.id.edit_km, km)
    }

    @Given("^I set the current voltage to (.*?)$")
    fun setCurrentVoltageTo(voltage: String) {
        setText(R.id.edit_voltage_actual, voltage)
    }

    @Given("^I set the starting voltage to (.*)V$")
    fun setStartingVoltageTo(startingVoltage: String) {
        setText(R.id.edit_voltage_start, startingVoltage)
    }

    @Then("the wheel can be saved")
    fun wheelCanBeSaved() {
        assertThat(R.id.button_save, isEnabled())
    }

    @Then("the wheel cannot be saved")
    fun wheelCannotBeSaved() {
        assertThat(R.id.button_save, isDisabled())
    }

    @Given("^the (.*?) has a previous mileage of (.*?) km$")
    fun wheelHasPreviousMileage(name: String, premileage: Int) {
        db.findWheel(name)?.let {
            db.saveWheel(
                WheelEntity(
                    it.id, it.name, it.btName, it.btAddr,
                    premileage, it.mileage, it.wh,
                    it.voltageMax, it.voltageMin, it.voltageReserve, it.voltageStart
                )
            )
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
        assertThat(selectedWheel.wh, equalTo(updatedWheel.wh))
    }

    @Then("the wheel was updated")
    fun wheelWasUpdated() {
        val wheel = db.getWheel(selectedWheel.id)
        assertThat(wheel, equalTo(updatedWheel.copy(voltageStart = selectedWheel.voltageMax)))
    }

    @When("^I connect to the (.*?)$")
    fun whenConnectTo(deviceName: String) {
        click(R.id.button_connect)
        selectListViewItem(R.id.devices, deviceName)

        expectedDeviceName = deviceName
    }

    @When("^I enter an actual voltage of (.*?)$")
    fun whenEnterActualVoltage(voltageParm: String) {
        enter(
            R.id.edit_voltage_actual,
            if (voltageParm.endsWith("V")) voltageParm.substring(0, voltageParm.length - 1) else voltageParm
        )
    }

    @When("I reconnect to the wheel")
    fun whenReconnectToWheel() {
        click(R.id.button_connect)
    }

    @When("^I select the (.*?)$")
    fun whenSelect(wheelName: String) {
        selectedWheel = db.findWheel(wheelName)!!
        assertThat(selectedWheel, notNullValue())

        selectListViewItem(R.id.wheels, "name", wheelName)
    }

    @When("^I do a scan and see the (.*?) but go back without connecting$")
    fun whenTryingToConnectTo(deviceName: String) {
        click(R.id.button_connect)
        assertThat(R.id.devices, hasRow(deviceName))
        goBackToViewWheel()
    }

    @Given("this simulated device:")
    fun simulatedDevice(device: DataTable) {
        assertThat(device.topCells(), equalTo(listOf("Bt Name", "Bt Address", "Km", "Mileage", "Voltage")))
        val deviceFields = device.cells(1)[0]

        BluetoothServicesSim.setDevice(BluetoothDevice(deviceFields[0], deviceFields[1]))
        BluetoothServicesSim.setKm(floatOf(deviceFields[2]))
        BluetoothServicesSim.setMileage(floatOf(deviceFields[3]))
        BluetoothServicesSim.setVoltage(voltageOf(deviceFields[4]))
    }

    private fun updateMapWheels() {
        db.getWheels().forEach { wheel ->
            mapWheels[wheel.name] = wheel
        }
    }

    private fun voltageOf(value: String): Float {
        assertThat(value, endsWith("V"))
        return parseFloat(value.substring(0, value.length - 1))
    }
}
