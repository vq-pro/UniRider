package quebec.virtualite.unirider.test.steps

import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import quebec.virtualite.unirider.test.fragments.EditFragmentObject

class EditSteps : BaseSteps() {

    private val editFragment = EditFragmentObject(app)

    @Before
    override fun beforeScenario() {
        super.beforeScenario()
    }

    @After
    override fun afterScenario() {
        super.afterScenario()
    }

    @When("I blank the charge amperage")
    fun blankChargeAmperage() {
        editFragment.changeChargeAmperage(" ")
    }

    @When("I blank the charge rate")
    fun blankChargeRate() {
        editFragment.changeChargeRate(" ")
    }

    @When("I blank the distance offset")
    fun blankDistanceOffset() {
        editFragment.changeDistanceOffset(" ")
    }

    @When("I blank the full voltage")
    fun blankFullVoltage() {
        editFragment.changeFullVoltage(" ")
    }

    @When("I blank the maximum voltage")
    fun blankMaximumVoltage() {
        editFragment.changeVoltageMax(" ")
    }

    @When("I blank the mileage")
    fun blankMileage() {
        editFragment.changeMileage(" ")
    }

    @When("I blank the minimum voltage")
    fun blankMinimumVoltage() {
        editFragment.changeVoltageMin(" ")
    }

    @When("I blank the name")
    fun blankName() {
        editFragment.changeName(" ")
    }

    @When("I blank the previous mileage")
    fun blankPreMileage() {
        editFragment.changePremileage(" ")
    }

    @When("I blank the wh")
    fun blankWh() {
        editFragment.changeWh(" ")
    }

    @When("I change the charge amperage")
    fun changeChargeAmperage() {
        editFragment.changeChargeAmperage("2")
    }

    @When("I change the charge rate")
    fun changeChargeRate() {
        editFragment.changeChargeRate("3")
    }

    @When("I change the distance offset")
    fun changeDistanceOffset() {
        editFragment.changeDistanceOffset("3")
    }

    @When("I change the mileage")
    fun changeMileage() {
        editFragment.changeMileage("123")
    }

    @When("I change the name")
    fun changeName() {
        editFragment.changeName("Toto")
    }

    @When("I change nothing")
    fun changeNothing() {
    }

    @When("I change the previous mileage")
    fun changePreMileage() {
        editFragment.changePremileage("123")
    }

    @When("I change the full voltage")
    fun changeVoltageFull() {
        editFragment.changeFullVoltage("${editFragment.getVoltageFull() + 0.1f}")
    }

    @When("I change the maximum voltage")
    fun changeVoltageMax() {
        editFragment.changeVoltageMax("${editFragment.getVoltageMax() + 0.1f}")
    }

    @When("I change the minimum voltage")
    fun changeVoltageMin() {
        editFragment.changeVoltageMin("${editFragment.getVoltageMin() + 0.1f}")
    }

    @When("I change the wh")
    fun changeWh() {
        editFragment.changeWh("123")
    }

    @When("I confirm the deletion")
    fun deleteConfirm() {
        editFragment.confirmDeletion()
    }

    @When("I delete the wheel")
    fun deleteWheel() {
        editFragment.deleteWheel()
    }

    @When("I save and go back to the main view")
    fun goSaveAndGoBackToMainView() {
        saveAndView()
        MainScreenSteps().goBackToMainView()
    }

    @When("I mark the wheel as sold")
    fun markWheelAsSold() {
        editFragment.markAsSold()
    }

    @When("I mark the wheel as unsold")
    fun markWheelAsUnsold() {
        editFragment.markAsUnsold()
    }

    @When("^I reuse the name (.*?)$")
    fun reuseWheelName(newName: String) {
        editFragment.changeName(newName)
    }

    @When("I save and view the wheel")
    fun saveAndView() {
        editFragment.save()
        ViewSteps().validateOnViewScreen()
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

    @When("I set these new values:")
    fun setNewWheelValues(newWheelValues: DataTable) {
        updatedWheel = editFragment.enterNewWheel(newWheelValues, selectedWheel)
    }

    @Then("the wheel can be saved")
    fun validateCanBeSaved() {
        editFragment.validateCanSave()
    }

    @Then("I can enter the details for that wheel")
    fun validateCanEnterDetailsForNewWheel() {
        editFragment.validateView()
    }

    @Then("the wheel cannot be saved")
    fun validateCannotBeSaved() {
        editFragment.validateCannotSave()
    }

    @Then("it shows that every field is editable")
    fun validateEveryFieldIsEditable() {
        editFragment.validateView()
    }
}