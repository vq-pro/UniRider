package quebec.virtualite.unirider.test

import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.rule.ActivityTestRule
import cucumber.api.java.After
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.junit.Rule
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.enter
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRow
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRows
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasSpinnerText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isDisabled
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isEmpty
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectSpinnerItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.start
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.stop
import quebec.virtualite.unirider.views.MainActivity

class Steps {

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var mainActivity: MainActivity
    private lateinit var selectedWheel: String

    @After
    fun afterScenario() {
        stop(activityTestRule)
    }

    @Then("I see my wheels and their distance:")
    fun seeMyWheelsAndTheirDistance(expectedWheels: List<String>) {
        assertThat(R.id.wheels, hasRows(expectedWheels))
    }

    @When("I start the app")
    fun startApp() {
        mainActivity = start(activityTestRule)!!
    }

    @Then("it blanks the displays")
    fun thenBlanksTheDisplays() {
        assertThat(R.id.wheel_voltage, isEmpty())
        assertThat(R.id.wheel_battery, isEmpty())
    }

    @Then("I can choose from these wheels:")
    fun thenCanChooseFromTheseWheels(rows: List<String>) {
        assertThat(R.id.wheel_selector, hasRows(rows))
    }

    @Then("^the selected entry is (.*?)$")
    fun theSelectedEntryIs(expectedEntry: String) {
        assertThat(R.id.wheel_selector, hasSpinnerText(expectedEntry))
        assertThat(R.id.wheel_selector, hasRow(expectedEntry))
    }

    @Then("I can see the name of the wheel")
    fun thenCanSeeNameOfWheel() {
        assertThat(R.id.wheel_name, hasText(selectedWheel))
    }

    @Then("I cannot go into the calculator")
    fun thenCannotGoCalculator() {
        assertThat(R.id.button_calculator, isDisabled())
    }

    @Then("^it displays a percentage of (.*?)$")
    fun thenDisplaysPercentage(percentage: String) {
        assertThat(R.id.wheel_battery, hasText(percentage))
    }

    @When("^I choose the (.*?)$")
    fun whenChoose(wheelName: String) {
        selectedWheel = wheelName
        selectSpinnerItem(R.id.wheel_selector, wheelName)
    }

    @When("I don't choose a wheel")
    fun whenDontChooseWheel() {
        // Nothing to do
    }

    @When("^I enter a voltage of (.*?)$")
    fun whenEnterVoltage(voltage: Float) {
        enter(R.id.wheel_voltage, voltage.toString())
    }

    @When("I go into the calculator")
    fun whenGoCalculator() {
        click(R.id.button_calculator)

        assertThat(R.id.wheel_voltage, isDisplayed())
//        assertThat(R.id.wheel_battery, isDisplayed())
    }
}
