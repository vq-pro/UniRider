package quebec.virtualite.unirider.commons.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.apache.http.util.TextUtils.isBlank
import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasToString
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.startsWith
import quebec.virtualite.unirider.BuildConfig.BLUETOOTH_ACTUAL
import quebec.virtualite.unirider.views.MainActivity
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep

object StepsUtils {

    private val INTERVAL = 250L
    private val PAUSE = if (BLUETOOTH_ACTUAL) 3000L else 0L
    private val TIMEOUT = if (BLUETOOTH_ACTUAL) 20000L else 5000L

    fun applicationContext(): Context {
        return ApplicationProvider.getApplicationContext()!!
    }

    fun assertThat(id: Int, assertion: Matcher<View>) {
        poll {
            element(id)?.check(matches(assertion))
        }
    }

    fun assertThat(message: String, id: Int, assertion: Matcher<View>) {
        poll(message) {
            element(id)?.check(matches(assertion))
        }
    }

    fun <T> assertThat(actual: T, matcher: Matcher<T>) {
        org.hamcrest.MatcherAssert.assertThat(actual, matcher)
    }

    fun back(nb: Int = 1) {
        var i = nb
        while (i-- > 0) {
            pressBackUnconditionally()
        }
    }

    fun click(id: Int) {
        assertThat("Cannot click button $id", id, isEnabled())
        element(id)?.perform(click())
    }

    fun currentFragment(mainActivity: MainActivity): Class<Fragment> {
        return mainActivity
            .supportFragmentManager
            .fragments[0]
            .childFragmentManager
            .fragments[0]
            .javaClass
    }

    fun getSpinnerText(id: Int): String {
        var text = ""
        element(id)?.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(Spinner::class.java)
            }

            override fun getDescription(): String {
                return "Text of the view"
            }

            override fun perform(uiController: UiController?, view: View?) {
                text = "${(view as Spinner).selectedItem}"
            }
        })
        return text
    }

    fun getText(id: Int): String {
        var text = ""
        element(id)?.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "Text of the view"
            }

            override fun perform(uiController: UiController?, view: View?) {
                text = (view as TextView).text.toString()
            }
        })
        return text
    }

    fun hasMinimumRows(expected: Int): Matcher<View> {
        return hasMinimumChildCount(expected)
    }

    fun <T> hasRow(expectedRow: T): Matcher<View> {
        return object : FeatureMatcher<View, List<T>?>(
            hasItem(expectedRow), "list", "list"
        ) {
            override fun featureValueOf(view: View?): List<T> {
                return actualListViewItemsFor(view)
            }
        }
    }

    fun <T> hasRows(expectedRows: List<T>): Matcher<View> {
        return object : FeatureMatcher<View, List<T>?>(
            equalTo(expectedRows), "list", "list"
        ) {
            override fun featureValueOf(view: View?): List<T> {
                return actualListViewItemsFor(view)
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

    fun longClick(id: Int) {
        assertThat("Cannot long click button $id", id, isEnabled())
        element(id)?.perform(longClick())
    }

    fun selectListViewItem(id: Int, value: String) {
        poll {
            onData(hasToString(startsWith(value)))
                .inAdapterView(withId(id))
                .atPosition(0)
                .perform(click())

            sleep(PAUSE)
        }
    }

    fun selectListViewItem(id: Int, fieldName: String, value: Any) {
        poll {
            onData(hasEntry(equalTo(fieldName), `is`(value)))
                .inAdapterView(withId(id))
                .perform(click())

            sleep(PAUSE)
        }
    }

    fun selectSpinnerItem(id: Int, value: String) {
        click(id)

        poll {
            onData(allOf(`is`(instanceOf(String::class.java)), `is`(value)))
                .perform(click())

            sleep(PAUSE)
        }
    }

    fun setText(id: Int, newText: String) {
        assertThat("Cannot set text for $id", id, isEnabled())
        element(id)?.perform(replaceText(newText))
    }

    fun <T : Activity> start(activityTestRule: ActivityTestRule<T>): T? {
        activityTestRule.launchActivity(Intent())
        return activityTestRule.activity
    }

    fun <T : Activity> stop(activityTestRule: ActivityTestRule<T>) {
        activityTestRule.finishActivity()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> actualListViewItemsFor(view: View?): ArrayList<T> {
        val adapter = (view as AdapterView<*>).adapter

        val actualItems = ArrayList<T>()
        for (i in 0 until adapter.count) {
            actualItems.add(adapter.getItem(i) as T)
        }

        return actualItems
    }

    private fun element(id: Int): ViewInteraction? {
        return onView(withId(id))
    }

    private fun poll(callback: () -> Unit) {
        poll("", callback)
    }

    private fun poll(message: String, callback: () -> Unit) {

        var exception: Throwable? = null
        val start = currentTimeMillis()

        do {
            try {
                callback()
                return

            } catch (e: Throwable) {
                exception = e
                sleep(INTERVAL)
            }

            val elapsed = currentTimeMillis() - start

        } while (elapsed < TIMEOUT)

        throw when {
            !isBlank(message) -> AssertionError(message, exception!!)
            else -> exception!!
        }
    }
}
