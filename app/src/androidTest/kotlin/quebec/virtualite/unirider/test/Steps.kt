package quebec.virtualite.unirider.test

import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.commons.android.utils.DateUtils
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.applicationContext
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.test.domain.TestDomain
import quebec.virtualite.unirider.test.fragments.ChargeFragmentObject
import quebec.virtualite.unirider.test.fragments.EditFragmentObject
import quebec.virtualite.unirider.test.fragments.MainFragmentObject
import quebec.virtualite.unirider.test.fragments.ViewFragmentObject
import quebec.virtualite.unirider.views.MainFragment

class Steps {

    private val app = TestApp()
    private val domain = TestDomain(applicationContext())

    private val chargeFragment = ChargeFragmentObject(app)
    private val editFragment = EditFragmentObject(app)
    private val mainFragment = MainFragmentObject(app, domain)
    private val viewFragment = ViewFragmentObject(app, domain)

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
        mainFragment.validateView()
    }

    @Then("I can charge the wheel")
    fun canChargeWheel() {
        viewFragment.validateCanCharge()
        viewFragment.charge()
        chargeFragment.validateView()
    }

    @Then("I cannot charge the wheel")
    fun cannotChargeWheel() {
        viewFragment.validateCannotCharge()
    }

    @Then("I cannot connect to the wheel on the charge screen")
    fun cannotConnectToWheelOnChargeScreen() {
        chargeFragment.validateCannotConnect()
    }

    @Then("it shows that every field is editable")
    fun itShowsThatEveryFieldIsEditable() {
        editFragment.validateView()
    }

    @Then("^it shows the updated name and a mileage of (.*?) on the main view$")
    fun itShowsTheUpdatedNameAndMileageOnTheMainView(expectedMileage: Int) {
        mainFragment.validateUpdatedNameAndMileage(selectedWheel.id, updatedWheel.name, expectedMileage)
    }

    @Then("^the km is updated to (.*?)$")
    fun kmUpdatedTo(expectedKm: Float) {
        viewFragment.validateKm(expectedKm)
    }

    @Then("^the mileage is updated to (.*?) km$")
    fun mileageUpdatedTo(expectedMileage: String) {
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
        mainFragment.validateWheelIsGone(selectedWheel)
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
        editFragment.changeName(newName)
    }

    @Then("I see my wheels and their mileage:")
    fun seeMyWheelsAndTheirMileage(expectedWheels: DataTable) {
        mainFragment.validateWheels(expectedWheels)
    }

    @Then("I see the total mileage")
    fun seeTotalMileage() {
        mainFragment.validateTotalMileage()
    }

    @When("I set these new values:")
    fun setNewWheelValues(newWheelValues: DataTable) {
        updatedWheel = editFragment.enterNewWheel(newWheelValues, selectedWheel)
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
        editFragment.changeChargeRate(" ")
    }

    @When("I blank the charger offset")
    fun blankWheelChargerOffset() {
        editFragment.changeChargerOffset(" ")
    }

    @When("I blank the distance offset")
    fun blankWheelDistanceOffset() {
        editFragment.changeDistanceOffset(" ")
    }

    @When("I blank the mileage")
    fun blankWheelMileage() {
        editFragment.changeMileage(" ")
    }

    @When("I blank the name")
    fun blankWheelName() {
        editFragment.changeName(" ")
    }

    @When("I blank the full voltage")
    fun blankWheelFullVoltage() {
        editFragment.changeFullVoltage(" ")
    }

    @When("I blank the maximum voltage")
    fun blankWheelMaximumVoltage() {
        editFragment.changeVoltageMax(" ")
    }

    @When("I blank the minimum voltage")
    fun blankWheelMinimumVoltage() {
        editFragment.changeVoltageMin(" ")
    }

    @When("I blank the previous mileage")
    fun blankWheelPreMileage() {
        editFragment.changePremileage(" ")
    }

    @When("I blank the reserve voltage")
    fun blankWheelReserveVoltage() {
        editFragment.changeReserveVoltage(" ")
    }

    @When("I blank the wh")
    fun blankWheelWh() {
        editFragment.changeWh(" ")
    }

    @Then("the wheel's Bluetooth name is undefined")
    fun bluetoothNameUndefined() {
        mainFragment.validateBluetoothDeviceUndefined(selectedWheel)
    }

    @Then("the wheel's Bluetooth name is updated")
    fun bluetoothNameUpdated() {
        viewFragment.validateBluetoothName()
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

    @When("^I change the voltage to (.*)$")
    fun changeVoltage(newVoltage: String) {
        chargeFragment.changeVoltageTo(newVoltage)
    }

    @When("I change the mileage")
    fun changeWheelMileage() {
        editFragment.changeMileage("123")
    }

    @When("I change the full voltage")
    fun changeWheelVoltageFull() {
        editFragment.changeFullVoltage("${editFragment.getVoltageFull() + 0.1f}")
    }

    @When("I change the maximum voltage")
    fun changeWheelVoltageMax() {
        editFragment.changeVoltageMax("${editFragment.getVoltageMax() + 0.1f}")
    }

    @When("I change the minimum voltage")
    fun changeWheelVoltageMin() {
        editFragment.changeVoltageMin("${editFragment.getVoltageMin() + 0.1f}")
    }

    @When("I change the charge rate")
    fun changeWheelChargeRate() {
        editFragment.changeChargeRate("3")
    }

    @When("I change the charger offset")
    fun changeWheelChargerOffset() {
        editFragment.changeChargerOffset("3")
    }

    @When("I change the distance offset")
    fun changeWheelDistanceOffset() {
        editFragment.changeDistanceOffset("3")
    }

    @When("I change the name")
    fun changeWheelName() {
        editFragment.changeName("Toto")
    }

    @When("I change the previous mileage")
    fun changeWheelPreMileage() {
        editFragment.changePremileage("123")
    }

    @When("I change the reserve voltage")
    fun changeWheelReserveVoltage() {
        editFragment.changeReserveVoltage("${editFragment.getVoltageReserve() + 0.1f}")
    }

    @When("I change the wh")
    fun changeWheelWh() {
        editFragment.changeWh("123")
    }

    @When("I confirm the deletion")
    fun confirmDelete() {
        editFragment.confirmDeletion()
    }

    @When("I delete the wheel")
    fun deleteWheel() {
        editFragment.deleteWheel()
    }

    @Then("^the details view shows the (.*) with a mileage of (.*) km$")
    fun detailsViewShowsNameAndMileage(expectedName: String, expectedMileage: String) {
        viewFragment.validateMileageUpdated(expectedMileage)
        viewFragment.validateName(expectedName)
    }

    @Then("^it displays a percentage of (.*?)%$")
    fun displaysPercentage(expectedPercentage: String) {
        viewFragment.validatePercentage(expectedPercentage)
    }

    @Then("^it displays an actual voltage of (.*?)V$")
    fun displaysActualVoltage(expectedVoltage: String) {
        chargeFragment.validateActualVoltage(expectedVoltage)
    }

    @Then("it displays blank estimated values")
    fun displaysBlankEstimatedValues() {
        viewFragment.validateBlankEstimates()
    }

    @Then("it displays these charging estimates:")
    fun displaysTheseChargingEstimates(expectedEstimates: DataTable) {
        chargeFragment.validateEstimates(expectedEstimates)
    }

    @Then("it displays these estimates:")
    fun displaysTheseEstimates(expectedEstimates: DataTable) {
        viewFragment.validateEstimates(expectedEstimates)
    }

    @When("I charge the wheel")
    fun chargeWheel() {
        viewFragment.charge()
        chargeFragment.validateView()
    }

    @When("I edit the wheel")
    fun editWheel() {
        viewFragment.editWheel()
    }

    @When("I set the full voltage lower than the minimum")
    fun setFullVoltageLowerThanMinimum() {
        editFragment.changeFullVoltage("${editFragment.getVoltageMin() - 0.1f}")
    }

    @When("I set the full voltage higher than the maximum")
    fun setFullVoltageHigherThanMaximum() {
        editFragment.changeFullVoltage("${editFragment.getVoltageMax() + 0.1f}")
    }

    @When("I set the maximum voltage lower than the minimum")
    fun setMaximumVoltageLowerThanMinimum() {
        editFragment.changeVoltageMax("${editFragment.getVoltageMin() - 0.1f}")
    }

    @When("I set the reserve voltage higher than the maximum")
    fun setReserveVoltageHigherThanMaximum() {
        editFragment.changeReserveVoltage("${editFragment.getVoltageMax() + 0.1f}")
    }

    @When("I set the reserve voltage lower than the minimum")
    fun setReserveVoltageLowerThanMinimum() {
        editFragment.changeReserveVoltage("${editFragment.getVoltageMin() - 0.1f}")
    }

    @When("the updated mileage for some of these wheels should be:")
    fun updateMileageForSomeOfTheseWheels(table: DataTable) {
        viewFragment.useTheseUpdateMileageValues(table)
    }

    @Given("^the current time is (.*)$")
    fun givenCurrentTimeIs(currentTime: String) {
        DateUtils.simulateNow(currentTime)
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
        editFragment.save()
        viewFragment.validateView()
    }

    @Given("^I set the actual voltage to (.*?)$")
    fun setActualVoltageTo(voltage: String) {
        viewFragment.setActualVoltageTo(voltage)
    }

    @Given("^I set the distance to (.*?)$")
    fun setDistanceTo(km: String) {
        viewFragment.setDistanceTo(km)
    }

    @Then("the wheel can be saved")
    fun wheelCanBeSaved() {
        editFragment.validateCanSave()
    }

    @Then("the wheel cannot be saved")
    fun wheelCannotBeSaved() {
        editFragment.validateCannotSave()
    }

    @Given("^the (.*?) has a previous mileage of (.*?) km$")
    fun wheelHasPreviousMileage(name: String, premileage: Int) {
        domain.updateWheelPreviousMileage(name, premileage)
    }

    @Then("the wheel appears as sold")
    fun wheelAppearsAsSold() {
        viewFragment.validateSold(selectedWheel.name)
    }

    @Then("the wheel is shown as unsold")
    fun wheelShownAsUnsold() {
        viewFragment.validateUnsold(selectedWheel)
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
        viewFragment.connectTo(deviceName)
    }

    @When("I mark the wheel as sold")
    fun whenMarkWheelAsSold() {
        editFragment.markAsSold()
    }

    @When("I mark the wheel as unsold")
    fun whenMarkWheelAsUnsold() {
        editFragment.markAsUnsold()
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
}
