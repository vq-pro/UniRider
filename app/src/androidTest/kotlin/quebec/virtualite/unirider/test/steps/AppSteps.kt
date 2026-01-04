package quebec.virtualite.unirider.test.steps

import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.When
import quebec.virtualite.commons.android.utils.DateUtils.Companion.simulateNow

class AppSteps : BaseSteps() {

    @Before
    override fun beforeScenario() {
        super.beforeScenario()
    }

    @After
    override fun afterScenario() {
        super.afterScenario()
    }

    @When("I start the app")
    fun startApp() {
        app.start()
    }

    @Given("^the current time is (.*)$")
    fun givenCurrentTimeIs(currentTime: String) {
        simulateNow(currentTime)
    }
}