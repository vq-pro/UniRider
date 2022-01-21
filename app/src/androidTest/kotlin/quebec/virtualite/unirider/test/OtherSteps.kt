package quebec.virtualite.unirider.test

import androidx.test.rule.ActivityTestRule
import cucumber.api.DataTable
import cucumber.api.java.After
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.junit.Rule
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.click
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasRows
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.isTrue
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.selectListViewItem
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.stop
import quebec.virtualite.unirider.mocks.DeviceScannerMock
import quebec.virtualite.unirider.services.Device
import quebec.virtualite.unirider.views.MainActivity

class OtherSteps {

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var selectedDevice: Device

    private val mockedScanner = DeviceScannerMock()

    @After
    fun afterScenario() {
        stop(activityTestRule)
    }

    @Given("I have these devices:")
    fun givenTheseTestDevices(devicesTable: DataTable) {

        mockedScanner.devices = devicesTable
            .asLists(String::class.java)
            .map { row -> Device(row.get(0), row.get(1)) }

//        MainActivity.Companion.scanner = mockedScanner
//        whenStartApp()
    }

    @Then("the scanning has stopped")
    fun thenScanningHasStopped() {
        assertThat(mockedScanner.isStopped(), isTrue())
    }

    @Then("I see my devices")
    fun thenSeeListOfDevices() {
        assertThat(R.id.devices, hasRows(mockedScanner.devices.map { device -> device.name }))
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

    @When("^I select the \\\"(.*?)\\\"$")
    fun whenSelect(deviceName: String) {

        selectedDevice = mockedScanner.devices
            .filter { device -> device.name.equals(deviceName) }
            .first()

        selectListViewItem(R.id.devices, selectedDevice.name)
    }
}
