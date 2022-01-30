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
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.not
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
                throw AssertionError(e)
            }
        }
    }

    fun <T> assertThat(actual: T, matcher: Matcher<T>) {
        org.hamcrest.MatcherAssert.assertThat(actual, matcher)
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

    @Suppress("UNCHECKED_CAST")
    fun <T> hasRows(expectedRows: List<T>): Matcher<View> {
        return object : FeatureMatcher<View, List<T>?>(
            equalTo(expectedRows), "list", "list"
        ) {

            override fun featureValueOf(view: View?): List<T> {
                val adapter = (view as AdapterView<*>).adapter

                val actualItems = ArrayList<T>()
                for (i in 0 until adapter.count) {
                    actualItems.add(adapter.getItem(i) as T)
                }

                return actualItems
            }
        }
    }

    fun hasSelectedText(expected: String): Matcher<View> {
        return allOf(
            isDisplayed(),
            isEnabled(),
            withText(equalTo(expected))
        )
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

    fun selectListViewItem(id: Int, fieldName: String, value: Any) {
        onData(hasEntry(equalTo(fieldName), `is`(value)))
            .inAdapterView(withId(id)).perform(click())
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
