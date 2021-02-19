package quebec.virtualite.commons.android.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep

object StepsUtils {

    private const val INTERVAL = 100L
    private const val TIMEOUT = 2000L

    class SpinnerMatcher(expectedEntries: List<String>) {
        val entries: List<String>

        init {
            entries = expectedEntries
        }

        fun lastIndex(): Int {
            return entries.size - 1
        }

        fun check(parentId: Int, row: Int) {
            click(parentId)
            onData(anything()).atPosition(row).perform(click())
            element(parentId)?.check(matches(hasRow(entries[row])))
        }
    }

    fun assertThat(id: Int, assertion: Matcher<View>) {
        poll { element(id)?.check(matches(assertion)) }
    }

    fun assertThat(actual: Boolean, expected: Boolean) {
        if (actual != expected) {
            throw AssertionError()
        }
    }

    fun assertThat(id: Int, spinnerEntries: SpinnerMatcher) {
        poll {
            assertThat(id, isDisplayed())
            assertThat(id, isEnabled())

            for (i in 0..spinnerEntries.lastIndex()) {
                spinnerEntries.check(id, i)
            }
        }
    }

    fun click(id: Int) {
        element(id)?.perform(click())
    }

    fun enter(id: Int, text: String) {
        val element = element(id)

        element?.perform(clearText())
        element?.perform(typeText(text))
    }

    fun hasMinimumRows(expected: Int): Matcher<View> {
        return hasMinimumChildCount(expected)
    }

    fun hasRow(expected: String?): Matcher<View> {
        return withChild(withText(expected))
    }

    fun hasRows(expectedEntries: List<String>): Matcher<View> {

        val asserts = mutableListOf(isDisplayed(), isEnabled(), hasChildCount(expectedEntries.size))
        for (entry in expectedEntries) {
            asserts.add(hasRow(entry))
        }

        return allOf(asserts)
    }

    fun hasSpinnerText(expected: String): Matcher<View> {
        return allOf(
            isDisplayed(),
            isEnabled(),
            withSpinnerText(equalTo(expected))
        )
    }

    fun hasSpinnerRows(expectedEntries: List<String>): SpinnerMatcher {
        return SpinnerMatcher(expectedEntries)
    }

    fun hasText(expected: String?): Matcher<View> {
        return withText(equalTo(expected))
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
