package quebec.virtualite.unirider.test.steps

import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import quebec.virtualite.unirider.test.fragments.ChargeFragmentObject

class ChargeSteps : BaseSteps() {

    private val chargeFragment = ChargeFragmentObject(app)

    @Before
    override fun beforeScenario() {
        super.beforeScenario()
    }

    @After
    override fun afterScenario() {
        super.afterScenario()
    }

    @When("^I change the voltage to (.*)$")
    fun changeVoltage(newVoltage: String) {
        chargeFragment.changeVoltageTo(newVoltage)
    }

    @When("I reconnect to update the voltage$")
    fun reconnectToUpdateVoltage() {
        chargeFragment.reconnect()
    }

    @When("^I request to charge for (.*?)$")
    fun requestChargeFor(km: String) {
        chargeFragment.chargeFor(km)
    }

    @Then("^it displays an actual voltage of (.*?)V$")
    fun validateActualVoltage(expectedVoltage: String) {
        chargeFragment.validateActualVoltage(expectedVoltage)
    }

    @Then("I cannot connect to the wheel on the charge screen")
    fun validateCannotConnectToWheelOnChargeScreen() {
        chargeFragment.validateCannotConnect()
    }

    @Then("it displays these charging estimates:")
    fun validateChargingEstimates(expectedEstimates: DataTable) {
        chargeFragment.validateEstimates(expectedEstimates)
    }

    @Then("the full charge indicator is on")
    fun validateFullChargeIndicatorOn() {
        chargeFragment.validateFullChargeIndicatorOn();
    }

    @Then("the full charge indicator is off")
    fun validateFullChargeIndicatorOff() {
        chargeFragment.validateFullChargeIndicatorOff();
    }

    fun validateOnChargeScreen() {
        chargeFragment.validateView()
    }
}