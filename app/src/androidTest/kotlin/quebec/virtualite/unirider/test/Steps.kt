package quebec.virtualite.unirider.test

import androidx.test.rule.ActivityTestRule
import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.endsWith
import org.junit.Rule
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.applicationContext
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.enter
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRows
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasSelectedText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isEmpty
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.start
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.stop
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.database.impl.WheelDbImpl
import quebec.virtualite.unirider.views.MainActivity
import java.lang.Float.parseFloat
import java.util.stream.Collectors.toList

class Steps {

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private var db = WheelDbImpl(applicationContext())

    private lateinit var mainActivity: MainActivity
    private lateinit var selectedWheel: String

    @Before
    fun beforeScenario() {
        db.deleteAll()
    }

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

    @Then("^the selected entry is (.*?)$")
    fun theSelectedEntryIs(expectedEntry: String) {
        assertThat(R.id.wheels, hasSelectedText(expectedEntry))
    }

    @Then("I go into the detailed view for that wheel")
    fun thenCanSeeNameOfWheel() {
        assertThat(R.id.wheel_name, hasText(selectedWheel))
    }

    @Then("^it displays a percentage of (.*?)$")
    fun thenDisplaysPercentage(percentage: String) {
        assertThat(R.id.wheel_battery, hasText(percentage))
    }

    @Given("these wheels:")
    fun givenTheseWheels(wheels: DataTable) {
        val rows = wheels.cells(1)
        db.saveWheels(rows.stream()
            .map { row -> WheelEntity(0, row[0], 0, parseVoltage(row[1]), parseVoltage(row[2])) }
            .collect(toList()))
    }

    @When("^I select the (.*?)$")
    fun whenSelect(wheelName: String) {
        selectedWheel = wheelName
        selectListViewItem(R.id.wheels, wheelName)
    }

    @When("^I enter a voltage of (.*?)V$")
    fun whenEnterVoltage(voltage: Float) {
        enter(R.id.wheel_voltage, voltage.toString())
    }

    private fun parseVoltage(value: String): Float {
        assertThat(value, endsWith("V"))
        return parseFloat(value.substring(0, value.length - 1))
    }
}
