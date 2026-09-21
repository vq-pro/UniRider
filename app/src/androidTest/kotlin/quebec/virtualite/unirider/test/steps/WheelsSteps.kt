package quebec.virtualite.unirider.test.steps

import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import io.cucumber.datatable.DataTable

class WheelsSteps : BaseSteps()
{
    @Before
    override fun beforeScenario()
    {
        super.beforeScenario()
    }

    @After
    override fun afterScenario()
    {
        super.afterScenario()
    }

    @Given("this simulated device:")
    fun givenSimulatedWheel(device: DataTable)
    {
        domain.simulateDevice(device)
    }

    @Given("^the (.*?) has a previous mileage of (.*?)$")
    fun givenWheelHasPreviousMileage(name: String, premileage: String)
    {
        domain.updateWheelPreviousMileage(name, premileage)
    }

    @Given("^(?:these|this) wheel[s]*:$")
    fun givenWheels(wheels: DataTable)
    {
        domain.loadWheels(wheels)
    }

    @Given("^(?:these|this) wheel[s]* (?:are|is) connected:$")
    fun givenWheelsAreConnected(wheels: DataTable)
    {
        domain.loadConnectedWheels(wheels)
    }
}