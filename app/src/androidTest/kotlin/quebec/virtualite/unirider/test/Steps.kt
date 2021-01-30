package quebec.virtualite.unirider.test

import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.hamcrest.Matchers.*
import org.junit.Rule
import quebec.virtualite.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.commons.android.utils.StepsUtils.click
import quebec.virtualite.commons.android.utils.StepsUtils.hasMinimumRows
import quebec.virtualite.commons.android.utils.StepsUtils.hasRow
import quebec.virtualite.commons.android.utils.StepsUtils.hasRows
import quebec.virtualite.commons.android.utils.StepsUtils.start
import quebec.virtualite.commons.android.utils.StepsUtils.stop
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.mocks.DeviceScannerMock
import quebec.virtualite.unirider.views.MainActivity
import java.lang.Thread.sleep

class Steps {

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var mainActivity: MainActivity

    private val mockedScanner = DeviceScannerMock()

    @Before
    fun beforeScenario() {
        MainActivity.Companion.scanner = mockedScanner
        mainActivity = start(activityTestRule)!!
    }

    @After
    fun afterScenario() {
        sleep(2000)

        stop(activityTestRule)
    }

    @Given("I start the app")
    fun givenStartApp() {
        assertThat(R.id.scan, isDisplayed())
        assertThat(R.id.devices, isDisplayed())
        assertThat(R.id.devices, not(isEnabled()))
    }

    @Given("these test devices:")
    fun givenTheseTestDevices(deviceNames: List<String>) {
        mockedScanner.devices = deviceNames
    }

    @Then("I see these devices:")
    fun thenSeeListOfDevices(expectedTestDevices: List<String>) {
        assertThat(R.id.devices, isEnabled())
        assertThat(R.id.devices, hasRows(mockedScanner.devices))
    }

    @When("I scan for devices")
    fun whenScanForDevices() {
        click(R.id.scan)
    }

//    /**
//     * [quebec.virtualite.unirider.views.GreetingsFragment.onViewCreated]
//     */
//    @When("^we enter the name \\\"(.*?)\\\"$")
//    fun whenWeEnterAsName(name: String) {
//        enter(R.id.name, name)
//        click(R.id.send)
//
//        // mockRestServer?.verifyGet("/v2/greetings/$name", token)
//    }
}
