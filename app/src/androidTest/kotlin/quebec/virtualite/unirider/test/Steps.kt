package quebec.virtualite.unirider.test

import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import quebec.virtualite.commons.android.utils.NumberUtils.floatOf
import quebec.virtualite.commons.android.utils.NumberUtils.intOf
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.applicationContext
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.getSpinnerText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.getText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRow
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasSelectedText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasSpinnerText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isDisabled
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isHidden
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.longClick
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setChecked
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.setText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.strip
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.test.domain.TestDomain
import quebec.virtualite.unirider.test.fragments.TestChargeFragment
import quebec.virtualite.unirider.test.fragments.TestEditFragment
import quebec.virtualite.unirider.test.fragments.TestMainFragment
import quebec.virtualite.unirider.test.fragments.TestViewFragment
import quebec.virtualite.unirider.views.MainFragment
import quebec.virtualite.unirider.views.WheelChargeFragment
import quebec.virtualite.unirider.views.WheelDeleteConfirmationFragment
import quebec.virtualite.unirider.views.WheelEditFragment
import quebec.virtualite.unirider.views.WheelRow
import java.lang.Thread.sleep

//FIXME-0 PageObjects
class Steps {

    private val IS_NOT_SOLD = false
    private val IS_SOLD = true

    private val app = TestApp()
    private val domain = TestDomain(applicationContext())

    private val chargeFragment = TestChargeFragment(app)
    private val editFragment = TestEditFragment(app)
    private val mainFragment = TestMainFragment(app, domain)
    private val viewFragment = TestViewFragment(app, domain)

    private var expectedDeviceName: String = ""
    private lateinit var selectedWheel: WheelEntity
    private lateinit var updatedWheel: WheelEntity

    @Before
    fun beforeScenario() {
        domain.clear()
    }

    @After
    fun afterScenario() {
        app.stop()
    }

    @When("I add a new wheel")
    fun addNewWheel() {
        selectedWheel = mainFragment.addWheel()
    }

    @Then("I am back at the main screen")
    fun backOnMainScreen() {
        assertThat(app.activeFragment(), equalTo(MainFragment::class.java))
    }

    @Then("I can charge the wheel")
    fun canChargeWheel() {
        click(R.id.button_charge)
        assertThat(app.activeFragment(), equalTo(WheelChargeFragment::class.java))
    }

    @Then("I cannot charge the wheel")
    fun cannotChargeWheel() {
        assertThat("Charge button is not disabled", R.id.button_charge, isDisabled())
    }

    @Then("I cannot connect to the wheel on the charge screen")
    fun cannotConnectToWheelOnChargeScreen() {
        chargeFragment.validateCannotConnect()
    }

    @Then("it shows that every field is editable")
    fun itShowsThatEveryFieldIsEditable() {
        assertThat(app.activeFragment(), equalTo(WheelEditFragment::class.java))
    }

    @Then("^it shows the updated name and a mileage of (.*?) on the main view$")
    fun itShowsTheUpdatedNameAndMileageOnTheMainView(expectedMileage: Int) {
        assertThat(app.activeFragment(), equalTo(MainFragment::class.java))
        assertThat(R.id.wheels, hasRow(WheelRow(selectedWheel.id, updatedWheel.name, expectedMileage)))
    }

    @Then("^the km is updated to (.*?)$")
    fun kmUpdatedTo(expectedKm: Float) {
        viewFragment.validateKm(expectedKm)
    }

    @Then("^the mileage is updated to (.*?) km$")
    fun mileageUpdatedTo(expectedMileage: Int) {
        viewFragment.validateMileageUpdated(expectedMileage)
    }

    @Then("the mileage is updated to its up-to-date value")
    fun mileageUpdatedToUpToDateValue() {
        viewFragment.validateUpToDateMileage(selectedWheel)
    }

    @Then("^the voltage is updated to (.*?)V and the battery (.*?)%$")
    fun voltageAndBatteryUpdatedTo(expectedVoltage: Float, expectedBattery: Float) {
        viewFragment.validateVoltageAndBattery(expectedVoltage, expectedBattery)
    }

    @Then("the wheel is gone")
    fun wheelIsGone() {
        assertThat(
            "The wheel is not gone", R.id.wheels,
            not(hasRow(WheelRow(selectedWheel.id, selectedWheel.name, selectedWheel.mileage)))
        )
    }

    @When("the wh/km is available")
    fun whPerKmIsAvailable() {
        setActualVoltageTo("90")
        setDistanceTo("40")
    }

    @When("the wh/km is not available")
    fun whPerKmIsNotAvailable() {
        // Nothing to do, values are already cleared
    }

    @When("^I reuse the name (.*?)$")
    fun reuseTheWheelName(newName: String) {
        setText(R.id.edit_name, newName)
    }

    @Then("I see my wheels and their mileage:")
    fun seeMyWheelsAndTheirMileage(expectedWheels: DataTable) {
        mainFragment.validateWheels(expectedWheels)
    }

    @Then("I see the total mileage")
    fun seeTotalMileage() {
        mainFragment.validateTotalMileage()
    }

    @Then("^the selected entry is (.*?)$")
    fun selectedEntryIs(expectedEntry: String) {
        assertThat(R.id.wheels, hasSelectedText(expectedEntry))
    }

    @When("I set these new values:")
    fun setNewWheelValues(newValues: DataTable) {

        val mapDetailToId = mapOf(
            Pair("Charge Rate", R.id.edit_charge_rate),
            Pair("Name", R.id.edit_name),
            Pair("Previous Mileage", R.id.edit_premileage),
            Pair("Mileage", R.id.edit_mileage),
            Pair("Wh", R.id.edit_wh),
            Pair("Voltage Min", R.id.edit_voltage_min),
            Pair("Voltage Reserve", R.id.edit_voltage_reserve),
            Pair("Voltage Max", R.id.edit_voltage_max),
            Pair("Sold", R.id.check_sold),
        )

        val mapEntity = mutableMapOf<String, String>()
        newValues.cells(0).forEach { row ->
            val field = row[0]
            val value = row[1]
            mapEntity[field] = value

            val rawField = mapDetailToId[field]
            assertThat("Field '$field' is not defined", rawField, not(nullValue()))

            val key = rawField!!
            if ("Sold" == field)
                setChecked(key, "Yes" == value)
            else
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
            floatOf(mapEntity["Voltage Max"]!!),
            floatOf(mapEntity["Charge Rate"]!!),
            "yes".equals(mapEntity["Sold"]!!, ignoreCase = true)
        )

        click(R.id.button_save)
    }

    @When("I start the app")
    fun startApp() {
        app.start()
    }

    @Then("the details view shows the details for that wheel")
    fun inDetailsView() {
        viewFragment.validateViewing(selectedWheel)
    }

    @When("I blank the charge rate")
    fun blankWheelChargeRate() {
        setText(R.id.edit_charge_rate, " ")
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

    @When("I blank the reserve voltage")
    fun blankWheelReserveVoltage() {
        setText(R.id.edit_voltage_reserve, " ")
    }

    @When("I blank the wh")
    fun blankWheelWh() {
        setText(R.id.edit_wh, " ")
    }

    @Then("the wheel's Bluetooth name is undefined")
    fun bluetoothNameUndefined() {
        assertThat(selectedWheel.btName, equalTo(null))
        assertThat(selectedWheel.btAddr, equalTo(null))
    }

    @Then("the wheel's Bluetooth name is updated")
    fun bluetoothNameUpdated() {
        viewFragment.validateBluetootName(expectedDeviceName)
    }

    @When("I cancel the scan and go back")
    fun cancelScan() {
        app.back(2)
    }

    @Then("I can enter the details for that wheel")
    fun canEnterDetailsForNewWheel() {
        editFragment.validateView()
    }

    @When("I change nothing")
    fun changeNothing() {
    }

    @When("^I change the rate to (.*) wh/km$")
    fun changeRate(newRate: Int) {
        chargeFragment.changeRateTo(newRate)
    }

    @When("^I change the voltage to (.*)$")
    fun changeVoltage(newVoltage: String) {
        chargeFragment.changeVoltageTo(newVoltage)
    }

    @When("I change the mileage")
    fun changeWheelMileage() {
        setText(R.id.edit_mileage, "123")
    }

    @When("I change the minimum voltage")
    fun changeWheelVoltageMin() {
        setText(R.id.edit_voltage_min, "${getVoltageMin() + 0.1f}")
    }

    @When("I change the maximum voltage")
    fun changeWheelVoltageMax() {
        setText(R.id.edit_voltage_max, "${getVoltageMax() + 0.1f}")
    }

    @When("I change the charge rate")
    fun changeWheelChargeRate() {
        setText(R.id.edit_charge_rate, "3")
    }

    @When("I change the name")
    fun changeWheelName() {
        setText(R.id.edit_name, "Toto")
    }

    @When("I change the previous mileage")
    fun changeWheelPreMileage() {
        setText(R.id.edit_premileage, "123")
    }

    @When("I change the reserve voltage")
    fun changeWheelReserveVoltage() {
        setText(R.id.edit_voltage_reserve, "${getVoltageReserve() + 0.1f}")
    }

    @When("I change the wh")
    fun changeWheelWh() {
        setText(R.id.edit_wh, "123")
    }

    @When("I confirm the deletion")
    fun confirmDelete() {
        assertThat(app.activeFragment(), equalTo(WheelDeleteConfirmationFragment::class.java))
        click(R.id.button_delete_confirmation)
    }

    @When("I delete the wheel")
    fun deleteWheel() {
        longClick(R.id.button_delete)
    }

    @Then("^the details view shows the (.*) with a mileage of (.*) km and a starting voltage of (.*)V$")
    fun detailsViewShowsNameAndMileage(expectedName: String, expectedMileage: String, expectedStartingVoltage: Float) {
        assertThat(R.id.view_name, hasText(expectedName))
        assertThat(R.id.view_mileage, hasText(expectedMileage))
        assertThat(R.id.edit_voltage_start, hasText("$expectedStartingVoltage"))
    }

    @Then("^the starting voltage is (.*)V$")
    fun startingVoltageIs(expectedStartingVoltage: Float) {
        viewFragment.validateStartingVoltage(expectedStartingVoltage)
    }

    @Then("^it displays a percentage of (.*?)%$")
    fun displaysPercentage(percentage: String) {
        assertThat(R.id.view_battery, hasText(percentage))
    }

    @Then("^it displays an actual voltage of (.*?)V$")
    fun displaysActualVoltage(expectedVoltage: String) {
        chargeFragment.validateActualVoltage(expectedVoltage)
    }

    @Then("it displays blank estimated values")
    fun displaysBlankEstimatedValues() {
        displaysTheseEstimates(
            DataTable.create(
                listOf(
                    listOf("remaining", "total range", "wh/km"),
                    listOf("", "", "")
                )
            )
        )
    }

    @Then("^it displays an estimated remaining range of (.*?) km$")
    fun displaysRemainingRange(range: String) {
        assertThat(R.id.view_remaining_range, hasText(range))
    }

    @Then("^it displays an estimated total range of (.*?) km$")
    fun displaysTotalRange(range: String) {
        assertThat(R.id.view_total_range, hasText(range))
    }

    @Then("^it displays an estimated rate of (.*?) wh/km$")
    fun displaysEstimatedRate(whPerKm: String) {
        assertThat(R.id.spinner_wh_per_km, hasSpinnerText(whPerKm))
    }

    @Then("it displays these charging estimates:")
    fun displaysTheseChargingEstimates(expectedEstimates: DataTable) {
        chargeFragment.validateEstimates(expectedEstimates)
    }

    @Then("it displays these estimates:")
    fun displaysTheseEstimates(expectedEstimates: DataTable) {
        expectedEstimates.diff(
            DataTable.create(
                listOf(
                    listOf("remaining", "total range", "wh/km"),
                    listOf(
                        getText(R.id.view_remaining_range),
                        getText(R.id.view_total_range),
                        getSpinnerText(R.id.spinner_wh_per_km)
                    )
                )
            )
        )
    }

    @When("I charge the wheel")
    fun chargeWheel() {
        viewFragment.charge()
        chargeFragment.validateView()
    }

    @When("I edit the wheel")
    fun editWheel() {
        click(R.id.button_edit)
    }

    @When("I set the maximum voltage lower than the minimum")
    fun setMaximumVoltageLowerThanMinimum() {
        setText(R.id.edit_voltage_max, "${getVoltageMin() - 0.1f}")
    }

    @When("I set the reserve voltage higher than the maximum")
    fun setReserveVoltageHigherThanMaximum() {
        setText(R.id.edit_voltage_reserve, "${getVoltageMax() + 0.1f}")
    }

    @When("I set the reserve voltage lower than the minimum")
    fun setReserveVoltageLowerThanMinimum() {
        setText(R.id.edit_voltage_reserve, "${getVoltageMin() - 0.1f}")
    }

    @When("the updated mileage for some of these wheels should be:")
    fun updateMileageForSomeOfTheseWheels(table: DataTable) {
        viewFragment.useTheseUpdateMileageValues(table)
    }

    @Given("this connected wheel:")
    fun givenThisConnectedWheel(wheel: DataTable) {
        givenTheseWheelsAreConnected(wheel)
    }

    @Given("these wheels:")
    fun givenTheseWheels(wheels: DataTable) {
        domain.loadWheels(wheels)
    }

    @Given("these wheels are connected:")
    fun givenTheseWheelsAreConnected(wheels: DataTable) {
        domain.loadConnectedWheels(wheels)
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
        app.back()
        mainFragment.validateView()
    }

    @When("I go back to view the wheel")
    fun goBackToViewWheel() {
        app.back()
        viewFragment.validateView()
    }

    @When("I save and go back to the main view")
    fun goSaveAndGoBackToMainView() {
        saveAndView()
        goBackToMainView()
    }

    @When("I save and view the wheel")
    fun saveAndView() {
        click(R.id.button_save)
    }

    @Given("^I set the actual voltage to (.*?)V$")
    fun setActualVoltageTo(voltage: String) {
        viewFragment.setActualVoltageTo(voltage)
    }

    @Given("^I set the distance to (.*?)$")
    fun setDistanceTo(km: String) {
        viewFragment.setDistanceTo(km)
    }

    @Given("^I set the starting voltage to (.*)$")
    fun setStartingVoltageTo(startingVoltage: String) {
        viewFragment.setStartingVoltageTo(startingVoltage)
    }

    @When("I wait")
    fun waitABit() {
        sleep(1000)
    }

    @Then("the wheel can be saved")
    fun wheelCanBeSaved() {
        assertThat("Save button should be enabled", R.id.button_save, isEnabled())
    }

    @Then("the wheel cannot be saved")
    fun wheelCannotBeSaved() {
        assertThat("Save button should be disabled", R.id.button_save, isDisabled())
    }

    @Given("^the (.*?) has a previous mileage of (.*?) km$")
    fun wheelHasPreviousMileage(name: String, premileage: Int) {
        domain.updateWheelPreviousMileage(name, premileage)
    }

    @Then("the wheel appears as sold")
    fun wheelAppearsAsSold() {
        assertThat("Wrong title", R.id.view_name, hasText("${selectedWheel.name} (Sold)"))
        assertThat("Charge button should not appear", R.id.button_charge, isHidden())
        assertThat("Connect button should not appear", R.id.button_connect_view, isHidden())
    }

    @Then("the wheel is shown as unsold")
    fun wheelShownAsUnsold() {
        assertThat(
            "The wheel is gone", R.id.wheels,
            hasRow(WheelRow(selectedWheel.id, selectedWheel.name, selectedWheel.mileage))
        )
    }

    @Then("the wheel was added")
    fun wheelWasAdded() {
        selectedWheel = domain.locateWheel(updatedWheel.name)!!

        assertThat(selectedWheel.chargeRate, equalTo(updatedWheel.chargeRate))
        assertThat(selectedWheel.name, equalTo(updatedWheel.name))
        assertThat(selectedWheel.mileage, equalTo(updatedWheel.mileage))
        assertThat(selectedWheel.voltageMax, equalTo(updatedWheel.voltageMax))
        assertThat(selectedWheel.voltageMin, equalTo(updatedWheel.voltageMin))
        assertThat(selectedWheel.wh, equalTo(updatedWheel.wh))
    }

    @Then("the wheel was updated")
    fun wheelWasUpdated() {
        val wheel = domain.loadWheel(selectedWheel.id)
        assertThat(wheel, equalTo(updatedWheel))
    }

    @When("I collapse the sold wheels")
    fun whenCollapseSoldWheels() {
        mainFragment.toggleSoldWheels()
    }

    @When("^I connect to the (.*?)$")
    fun whenConnectTo(deviceName: String) {
        expectedDeviceName = viewFragment.connectTo(deviceName)
    }

    @When("^I enter an actual voltage of (.*?)$")
    fun whenEnterActualVoltage(voltage: String) {
        setText(R.id.edit_voltage_actual, strip(voltage, "V"))
    }

    @When("I mark the wheel as sold")
    fun whenMarkWheelAsSold() {
        setChecked(R.id.check_sold, IS_SOLD)
    }

    @When("I mark the wheel as unsold")
    fun whenMarkWheelAsUnsold() {
        setChecked(R.id.check_sold, IS_NOT_SOLD)
    }

    /**
     * Code: [MainFragment.onSelectWheel]
     */
    @When("I open up the sold wheels")
    fun whenOpenSoldWheels() {
        mainFragment.toggleSoldWheels()
    }

    @When("I reconnect to the wheel")
    fun whenReconnectToWheel() {
        viewFragment.reconnect()
    }

    @When("I reconnect to update the voltage$")
    fun whenReconnectToUpdateVoltage() {
        chargeFragment.reconnect()
    }

    @When("^I request to charge for (.*?)$")
    fun whenRequestChargeFor(km: String) {
        chargeFragment.chargeFor(km)
    }

    @When("^I select the (.*?)$")
    fun whenSelect(wheelName: String) {
        selectedWheel = mainFragment.selectWheel(wheelName)
    }

    @When("^I do a scan and see the (.*?) \\((.*?)\\) but go back without connecting$")
    fun whenTryingToConnectTo(deviceName: String, deviceAddr: String) {
        viewFragment.connectAndAbort(deviceName, deviceAddr)
    }

    @Given("this simulated device:")
    fun simulatedDevice(device: DataTable) {
        domain.simulateDevice(device)
    }

    private fun getVoltageMax() = floatOf(getText(R.id.edit_voltage_max))

    private fun getVoltageMin() = floatOf(getText(R.id.edit_voltage_min))

    private fun getVoltageReserve() = floatOf(getText(R.id.edit_voltage_reserve))
}
