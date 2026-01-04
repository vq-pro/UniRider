package quebec.virtualite.unirider.test.steps

import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import quebec.virtualite.unirider.test.fragments.MainFragmentObject

class MainScreenSteps : BaseSteps() {

    private val mainFragment = MainFragmentObject(app, domain)

    @Before
    override fun beforeScenario() {
        super.beforeScenario()
    }

    @After
    override fun afterScenario() {
        super.afterScenario()
    }

    @When("I add a new wheel")
    fun addNewWheel() {
        selectedWheel = mainFragment.addWheel()
    }

    @When("I collapse the sold wheels")
    fun collapseSoldWheels() {
        mainFragment.toggleSoldWheels()
    }

    @When("I go back to the main view")
    fun goBackToMainView() {
        app.back()
        mainFragment.validateView()
    }

    /**
     * Code: [quebec.virtualite.unirider.views.MainFragment.onSelectWheel]
     */
    @When("I open up the sold wheels")
    fun openSoldWheels() {
        mainFragment.toggleSoldWheels()
    }

    @When("^I select the (.*?)$")
    fun select(wheelName: String) {
        selectedWheel = mainFragment.selectWheel(wheelName)
    }

    @Then("I am back at the main screen")
    fun validateBackOnMainScreen() {
        mainFragment.validateView()
    }

    @Then("the wheel's Bluetooth name is undefined")
    fun validateBluetoothNameUndefined() {
        mainFragment.validateBluetoothDeviceUndefined(selectedWheel)
    }

    @Then("^it shows the updated name and a mileage of (.*?) on the main view$")
    fun validateShowsTheUpdatedNameAndMileageOnTheMainView(expectedMileage: Int) {
        mainFragment.validateUpdatedNameAndMileage(selectedWheel.id, updatedWheel.name, expectedMileage)
    }

    @Then("I see the total mileage")
    fun validateTotalMileage() {
        mainFragment.validateTotalMileage()
    }

    @Then("the wheel is gone")
    fun validateWheelIsGone() {
        mainFragment.validateWheelIsGone(selectedWheel)
    }

    @Then("I see my wheels and their mileage:")
    fun validateWheelsAndTheirMileage(expectedWheels: DataTable) {
        mainFragment.validateWheels(expectedWheels)
    }
}