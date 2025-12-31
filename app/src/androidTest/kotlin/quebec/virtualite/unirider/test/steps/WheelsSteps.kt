package quebec.virtualite.unirider.test.steps

import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import org.hamcrest.Matchers
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

    @Given("^(?:these|this) wheel[s]*:$")
    fun givenWheels(wheels: DataTable) {
        domain.loadWheels(wheels)
    }

    @Given("^(?:these|this) wheel[s]* (?:are|is) connected:$")
    fun givenWheelsAreConnected(wheels: DataTable) {
        domain.loadConnectedWheels(wheels)
    }

    @Given("^the (.*?) has a previous mileage of (.*?) km$")
    fun givenWheelHasPreviousMileage(name: String, premileage: Int) {
        domain.updateWheelPreviousMileage(name, premileage)
    }

    @Then("the wheel was added")
    fun validateWheelWasAdded() {
        selectedWheel = domain.locateWheel(updatedWheel.name)!!

        assertThat(selectedWheel.chargeRate, Matchers.equalTo(updatedWheel.chargeRate))
        assertThat(selectedWheel.name, Matchers.equalTo(updatedWheel.name))
        assertThat(selectedWheel.mileage, Matchers.equalTo(updatedWheel.mileage))
        assertThat(selectedWheel.voltageMax, Matchers.equalTo(updatedWheel.voltageMax))
        assertThat(selectedWheel.voltageMin, Matchers.equalTo(updatedWheel.voltageMin))
        assertThat(selectedWheel.wh, Matchers.equalTo(updatedWheel.wh))
    }

    @Then("the wheel was updated")
    fun validateWheelWasUpdated() {
        val wheel = domain.loadWheel(selectedWheel.id)
        assertThat(wheel, Matchers.equalTo(updatedWheel))
    }
}