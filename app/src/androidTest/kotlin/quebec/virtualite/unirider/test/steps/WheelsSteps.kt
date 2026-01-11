package quebec.virtualite.unirider.test.steps

import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import org.hamcrest.Matchers.equalTo
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat

class WheelsSteps : BaseSteps() {

    @Before
    override fun beforeScenario() {
        super.beforeScenario()
    }

    @After
    override fun afterScenario() {
        super.afterScenario()
    }

    @Given("this simulated device:")
    fun givenSimulatedWheel(device: DataTable) {
        domain.simulateDevice(device)
    }

    @Given("^the (.*?) has a previous mileage of (.*?) km$")
    fun givenWheelHasPreviousMileage(name: String, premileage: Int) {
        domain.updateWheelPreviousMileage(name, premileage)
    }

    @Given("^(?:these|this) wheel[s]*:$")
    fun givenWheels(wheels: DataTable) {
        domain.loadWheels(wheels)
    }

    @Given("^(?:these|this) wheel[s]* (?:are|is) connected:$")
    fun givenWheelsAreConnected(wheels: DataTable) {
        domain.loadConnectedWheels(wheels)
    }

    @Then("the wheel was added")
    fun validateWheelWasAdded() {
        selectedWheel = domain.locateWheel(updatedWheel.name)!!

        assertThat(selectedWheel.chargeAmperage, equalTo(updatedWheel.chargeAmperage))
        assertThat(selectedWheel.chargeRate, equalTo(updatedWheel.chargeRate))
        assertThat(selectedWheel.name, equalTo(updatedWheel.name))
        assertThat(selectedWheel.mileage, equalTo(updatedWheel.mileage))
        assertThat(selectedWheel.voltageMax, equalTo(updatedWheel.voltageMax))
        assertThat(selectedWheel.voltageMin, equalTo(updatedWheel.voltageMin))
        assertThat(selectedWheel.voltageFull, equalTo(updatedWheel.voltageFull))
        assertThat(selectedWheel.wh, equalTo(updatedWheel.wh))
    }

    @Then("the wheel was updated")
    fun validateWheelWasUpdated() {
        val wheel = domain.loadWheel(selectedWheel.id)
        assertThat(wheel, equalTo(updatedWheel))
    }
}