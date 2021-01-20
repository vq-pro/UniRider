package quebec.virtualite.unirider.test

import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.rule.ActivityTestRule
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.When
import org.junit.Rule
import quebec.virtualite.commons.android.rest.BackendServerMock
import quebec.virtualite.commons.android.rest.BasicAuthentication
import quebec.virtualite.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.commons.android.utils.StepsUtils.click
import quebec.virtualite.commons.android.utils.StepsUtils.enter
import quebec.virtualite.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.commons.android.utils.StepsUtils.isEmpty
import quebec.virtualite.commons.android.utils.StepsUtils.start
import quebec.virtualite.commons.android.utils.StepsUtils.stop
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.GreetingResponse
import quebec.virtualite.unirider.services.client.TEST_PASSWORD
import quebec.virtualite.unirider.services.client.TEST_USER
import quebec.virtualite.unirider.test.BuildConfig.SERVER_PORT
import quebec.virtualite.unirider.views.MainActivity
import java.lang.Thread.sleep

class Steps {
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private val token = BasicAuthentication.token(TEST_USER, TEST_PASSWORD)
    private var mockRestServer = when (BuildConfig.SERVER_MOCKED) {
        true -> BackendServerMock()
        else -> null
    }

    private lateinit var mainActivity: MainActivity

    @Before
    fun beforeScenario() {
        mainActivity = start(activityTestRule)!!

        mockRestServer?.start(SERVER_PORT)
    }

    @After
    fun afterScenario() {
        sleep(2000)

        mockRestServer?.stop()

        stop(activityTestRule)
    }

    @Given("^we see \\\"(.*?)\\\"$")
    fun givenWeSee(expectedContent: String) {
        assertThat(R.id.title_message, isDisplayed())
        assertThat(R.id.title_message, hasText(expectedContent))

        assertThat(R.id.name, isEmpty())
    }

    @Given("^we've already greeted \\\"(.*?)\\\" (\\d*) times$")
    fun givenWeveAlreadyGreetedXTimes(name: String, nb: Int) {
        mockRestServer!!.whenGet("/v2/greetings/$name", token)
            .thenReturn(GreetingResponse("Hello $name!", nb + 1))
    }

    @Given("^we've never greeted \\\"(.*?)\\\" before$")
    fun givenWeveNeverGreetedXBefore(name: String) {
        mockRestServer!!.whenGet("/v2/greetings/$name", token)
            .thenReturn(GreetingResponse("Hello $name!", 1))
    }

    /**
     * [quebec.virtualite.unirider.views.GreetingsFragment.onViewCreated]
     */
    @When("^we enter the name \\\"(.*?)\\\"$")
    fun whenWeEnterAsName(name: String) {
        enter(R.id.name, name)
        click(R.id.send)

//        mockRestServer?.verifyGet("/v2/greetings/$name", token)
    }
}