package quebec.virtualite.unirider.test.steps

import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import quebec.virtualite.unirider.test.fragments.ViewFragmentObject

class ViewSteps : BaseSteps() {

    private val viewFragment = ViewFragmentObject(app, domain)

    @Before
    override fun beforeScenario() {
        super.beforeScenario()
    }

    @After
    override fun afterScenario() {
        super.afterScenario()
    }

    @When("I charge the wheel")
    fun chargeWheel() {
        viewFragment.charge()
        ChargeSteps().validateOnChargeScreen()
    }

    @When("^I connect to the (.*?)$")
    fun connectTo(deviceName: String) {
        viewFragment.connectTo(deviceName)
    }

    @When("I edit the wheel")
    fun editWheel() {
        viewFragment.editWheel()
    }

    @When("I go back to view the wheel")
    fun goBackToViewWheel() {
        app.back()
        validateOnViewScreen()
    }

    @When("I reconnect to the wheel")
    fun reconnectToWheel() {
        viewFragment.reconnect()
    }

    @When("I cancel the scan and go back")
    fun scanCancel() {
        app.back(2)
    }

    @When("^I do a scan and see the (.*?) \\((.*?)\\) but go back without connecting$")
    fun scanToConnectTo(deviceName: String, deviceAddr: String) {
        viewFragment.connectAndAbort(deviceName, deviceAddr)
    }

    @When("the updated mileage for some of these wheels should be:")
    fun updateMileageForSomeOfTheseWheels(table: DataTable) {
        viewFragment.useTheseUpdateMileageValues(table)
    }

    @Given("^I set the actual voltage to (.*?)$")
    fun givenActualVoltageTo(voltage: String) {
        viewFragment.setActualVoltageTo(voltage)
    }

    @Given("^I set the distance to (.*?)$")
    fun givenDistanceTo(km: String) {
        viewFragment.setDistanceTo(km)
    }

    @Then("it displays blank estimated values")
    fun validateBlankEstimatedValues() {
        viewFragment.validateBlankEstimates()
    }

    @Then("the wheel's Bluetooth name is updated")
    fun validateBluetoothNameUpdated() {
        viewFragment.validateBluetoothName()
    }

    @Then("I can charge the wheel")
    fun validateCanChargeWheel() {
        viewFragment.validateCanCharge()
    }

    @Then("I cannot charge the wheel")
    fun validateCannotChargeWheel() {
        viewFragment.validateCannotCharge()
    }

    @Then("^the details view shows the (.*) with a mileage of (.*) km$")
    fun validateDetailsViewShowsNameAndMileage(expectedName: String, expectedMileage: String) {
        viewFragment.validateMileageUpdated(expectedMileage)
        viewFragment.validateName(expectedName)
    }

    @Then("it displays these estimates:")
    fun validateEstimates(expectedEstimates: DataTable) {
        viewFragment.validateEstimates(expectedEstimates)
    }

    @Then("the details view shows the details for that wheel")
    fun validateInDetailsView() {
        viewFragment.validateViewing(selectedWheel)
    }

    @Then("^the km is updated to (.*?)$")
    fun validateKmUpdatedTo(expectedKm: Float) {
        viewFragment.validateKm(expectedKm)
    }

    @Then("^the mileage is updated to (.*?) km$")
    fun validateMileageUpdatedTo(expectedMileage: String) {
        viewFragment.validateMileageUpdated(expectedMileage)
    }

    @Then("the mileage is updated to its up-to-date value")
    fun validateMileageUpdatedToUpToDateValue() {
        viewFragment.validateUpToDateMileage(selectedWheel)
    }

    @Then("^it displays a percentage of (.*?)%$")
    fun validatePercentage(expectedPercentage: String) {
        viewFragment.validatePercentage(expectedPercentage)
    }

    @When("the wh/km is available")
    fun whPerKmIsAvailable() {
        viewFragment.setActualVoltageTo("90")
        viewFragment.setDistanceTo("40")
    }

    @Then("^the voltage is updated to (.*?)V and the battery (.*?)%$")
    fun validateVoltageAndBatteryUpdatedTo(expectedVoltage: Float, expectedBattery: Float) {
        viewFragment.validateVoltageAndBattery(expectedVoltage, expectedBattery)
    }

    @Then("the wheel appears as sold")
    fun validateWheelAppearsAsSold() {
        viewFragment.validateSold(selectedWheel.name)
    }

    @Then("the wheel is shown as unsold")
    fun validateWheelShownAsUnsold() {
        viewFragment.validateUnsold(selectedWheel)
    }

    fun validateOnViewScreen() {
        viewFragment.validateView()
    }
}