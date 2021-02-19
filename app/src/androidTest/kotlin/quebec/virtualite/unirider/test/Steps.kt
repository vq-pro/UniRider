package quebec.virtualite.unirider.test

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.rule.ActivityTestRule
import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.hamcrest.Matchers.*
import org.junit.Assert.fail
import org.junit.Rule
import quebec.virtualite.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.commons.android.utils.StepsUtils.click
import quebec.virtualite.commons.android.utils.StepsUtils.enter
import quebec.virtualite.commons.android.utils.StepsUtils.hasRows
import quebec.virtualite.commons.android.utils.StepsUtils.hasSpinnerText
import quebec.virtualite.commons.android.utils.StepsUtils.hasSpinnerRows
import quebec.virtualite.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.commons.android.utils.StepsUtils.isTrue
import quebec.virtualite.commons.android.utils.StepsUtils.selectListViewItem
import quebec.virtualite.commons.android.utils.StepsUtils.selectSpinnerItem
import quebec.virtualite.commons.android.utils.StepsUtils.start
import quebec.virtualite.commons.android.utils.StepsUtils.stop
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.mocks.DeviceScannerMock
import quebec.virtualite.unirider.services.Device
import quebec.virtualite.unirider.views.MainActivity

class Steps {

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var mainActivity: MainActivity

    @After
    fun afterScenario() {
//        sleep(5000)

        stop(activityTestRule)
    }

    @Then("I can choose from these wheels:")
    fun thenCanChooseFromTheseWheels(rows: List<String>) {
        assertThat(R.id.wheel_selector, hasSpinnerText("<Select Model>"))
        assertThat(R.id.wheel_selector, hasSpinnerRows(rows))
    }

    @Then("^it displays a percentage of (.*?)$")
    fun thenDisplaysPercentage(percentage: String) {
        assertThat(R.id.wheel_battery, hasText(percentage))
    }

    @When("^I choose the \\\"(.*?)\\\"$")
    fun whenChoose(wheelName: String) {
        selectSpinnerItem(R.id.wheel_selector, wheelName)

        assertThat(R.id.wheel_voltage, isDisplayed())
//        assertThat(R.id.wheel_battery, isDisplayed())
    }

    @When("^I enter a voltage of (.*?)$")
    fun whenEnterVoltage(voltage: Float) {
        enter(R.id.wheel_voltage, voltage.toString())
    }

    @When("I start the app")
    fun whenStartApp() {
        mainActivity = start(activityTestRule)!!

        assertThat(R.id.wheel_battery, not(isDisplayed()))
        assertThat(R.id.wheel_voltage, not(isDisplayed()))
    }
}
