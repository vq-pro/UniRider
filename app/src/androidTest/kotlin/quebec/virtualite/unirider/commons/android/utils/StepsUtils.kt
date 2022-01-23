package quebec.virtualite.unirider.commons.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep


object StepsUtils {

    private const val INTERVAL = 100L
    private const val TIMEOUT = 2000L

    fun assertThat(id: Int, assertion: Matcher<View>) {
        poll {
            try {
                element(id)?.check(matches(assertion))
            } catch (e: Exception) {
                throw AssertionError("Mismatch between the view and the expected", e)
            }
        }
    }

    fun assertThat(actual: Boolean, expected: Boolean) {
        if (actual != expected) {
            throw AssertionError()
        }
    }

    fun click(id: Int) {
        assertThat(id, isEnabled())
        element(id)?.perform(click())
    }

    fun enter(id: Int, text: String) {
        val element = element(id)

        element?.perform(clearText())
        element?.perform(typeText(text))
    }

    fun applicationContext(): Context {
        return ApplicationProvider.getApplicationContext()!!
    }

    fun hasMinimumRows(expected: Int): Matcher<View> {
        return hasMinimumChildCount(expected)
    }

    fun hasRow(expected: String?): Matcher<View> {
        return withChild(withText(expected))
    }

    fun hasRows(expectedItems: List<String>): Matcher<View> {
        return object : FeatureMatcher<View, List<String>?>(
            equalTo(expectedItems), "list", "list"
        ) {

            override fun featureValueOf(view: View?): List<String> {
                val adapter = (view as AdapterView<*>).adapter

                val actualItems = ArrayList<String>()
                for (i in 0 until adapter.count) {
                    actualItems.add(adapter.getItem(i).toString())
                }

                return actualItems
            }
        }
    }

    fun hasSpinnerText(expected: String): Matcher<View> {
        return allOf(
            isDisplayed(),
            isEnabled(),
            withSpinnerText(equalTo(expected))
        )
    }

    fun hasText(expected: String?): Matcher<View> {
        return withText(equalTo(expected))
    }

    fun isDisabled(): Matcher<View> {
        return not(isEnabled())
    }

    fun isEmpty(): Matcher<View> {
        return hasText("")
    }

    fun isTrue(): Boolean {
        return true
    }

    fun selectListViewItem(id: Int, entry: String) {
        onData(hasToString(startsWith(entry))).inAdapterView(withId(id)).perform(click())
    }

    fun selectSpinnerItem(id: Int, entry: String) {
        click(id)
        onData(`is`(entry)).perform(click())
    }

    fun <T : Activity> start(activityTestRule: ActivityTestRule<T>): T? {
        activityTestRule.launchActivity(Intent())
        return activityTestRule.activity
    }

    fun <T : Activity> stop(activityTestRule: ActivityTestRule<T>) {
        activityTestRule.finishActivity()
    }

    internal fun element(id: Int): ViewInteraction? {
        return onView(withId(id))
    }

    internal fun poll(callback: () -> Unit) {

        var exception: Throwable? = null
        val start = currentTimeMillis()

        do {
            try {
                callback()
                return

            } catch (e: Throwable) {
                if (exception == null) {
                    exception = e
                }
                sleep(INTERVAL)
            }

            val elapsed = currentTimeMillis() - start

        } while (elapsed < TIMEOUT)

        throw exception!!
    }
}
