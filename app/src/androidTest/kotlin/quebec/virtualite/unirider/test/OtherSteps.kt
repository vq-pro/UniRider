package quebec.virtualite.unirider.test

import androidx.test.rule.ActivityTestRule
import cucumber.api.java.After
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.junit.Rule
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.stop
import quebec.virtualite.unirider.views.MainActivity

class OtherSteps {

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var selectedDevice: Device

    @After
    fun afterScenario() {
        stop(activityTestRule)
    }

    @Then("I see the screen for this wheel")
    fun thenSeeScreenForThisWheel() {
        assertThat(R.id.device_name, hasText(selectedDevice.name))
        assertThat(R.id.device_address, hasText(selectedDevice.address))
    }

    @Then("I see the type of wheel it is")
    fun thenSeeTypeOfWheel() {
//        assertThat(R.id.device_type, hasText("toto"))
    }

    @When("I scan again")
    fun whenScanAgain() {
        whenScanForDevices()
    }

    @When("I scan for devices")
    fun whenScanForDevices() {
        click(R.id.scan)
    }

//    @When("^I select the \\\"(.*?)\\\"$")
//    fun whenSelect(deviceName: String) {
//
//        selectedDevice = mockedScanner.devices
//            .filter { device -> device.name.equals(deviceName) }
//            .first()
//    }
}
