package quebec.virtualite.unirider.commons.android.utils

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Checkable
import android.widget.Spinner
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import io.cucumber.datatable.DataTable
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasToString
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.isA
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.startsWith
import quebec.virtualite.commons.android.utils.StringUtils.isBlank
import quebec.virtualite.unirider.BuildConfig.BLUETOOTH_ACTUAL
import quebec.virtualite.unirider.views.WheelRow
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep

object StepsUtils {

    private const val INTERVAL = 250L
    private val TIMEOUT = if (BLUETOOTH_ACTUAL) 20000L else 5000L

    fun applicationContext(): Context = ApplicationProvider.getApplicationContext()!!

    fun <T> assertThat(actual: T, matcher: Matcher<T>) {
        MatcherAssert.assertThat(actual, matcher)
    }

    fun <T> assertThat(message: String, actual: T, matcher: Matcher<T>) {
        MatcherAssert.assertThat(message, actual, matcher)
    }

    fun assertThatField(id: Int, assertion: Matcher<View>) {
        poll {
            element(id)?.check(matches(assertion))
        }
    }

    fun assertThatField(message: String, id: Int, assertion: Matcher<View>) {
        poll(message) {
            element(id)?.check(matches(assertion))
        }
    }

    fun <T> assertThatPolling(getActual: () -> T, matcher: Matcher<T>) {
        poll {
            MatcherAssert.assertThat(getActual.invoke(), matcher)
        }
    }

    fun <T> assertThatPolling(message: String, getActual: () -> T, matcher: Matcher<T>) {
        poll(message) {
            MatcherAssert.assertThat(getActual.invoke(), matcher)
        }
    }

    fun click(id: Int) {
        element(id)?.perform(click())
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
                val spinner = view as Spinner
                text = if (spinner.selectedItemPosition == -1) ""
                else "${spinner.selectedItem}"
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

    fun hasMinimumRows(expected: Int) = hasMinimumChildCount(expected)

    fun <T> hasRow(expectedRow: T) =
        object : FeatureMatcher<View, List<T>?>(
            hasItem(expectedRow), "list", "list"
        ) {
            override fun featureValueOf(view: View?): List<T> {
                return actualListViewItemsFor(view)
            }
        }

    fun <T> hasRows(expectedRows: List<T>) =
        object : FeatureMatcher<View, List<T>?>(
            equalTo(expectedRows), "list", "list"
        ) {
            override fun featureValueOf(view: View?): List<T> {
                return actualListViewItemsFor(view)
            }
        }

    fun hasSelectedText(expected: String): Matcher<View> =
        allOf(isVisible(), isEnabled(), withText(equalTo(expected)))

    fun hasSpinnerText(expected: String): Matcher<View> =
        allOf(isVisible(), isEnabled(), withSpinnerText(equalTo(expected)))

    fun hasText(expected: String): Matcher<View> =
        withText(equalTo(expected))

    fun isDisabled(): Matcher<View> =
        not(isEnabled())

    fun isEmpty(): Matcher<View> =
        hasText("")

    fun isEmpty(shouldBeEmpty: Boolean): Matcher<View> =
        if (shouldBeEmpty) hasText("")
        else not(hasText(""))

    fun isInvisible(): Matcher<View> =
        isVisible(false)

    fun isVisible(): Matcher<View> =
        isVisible(true)

    fun isVisible(shouldDisplay: Boolean): Matcher<View> =
        if (shouldDisplay) ViewMatchers.isDisplayed()
        else not(ViewMatchers.isDisplayed())

    fun isEnabled(shouldBeEnabled: Boolean): Matcher<View> =
        if (shouldBeEnabled) isEnabled()
        else isDisabled()

    fun longClick(id: Int) {
        element(id)?.perform(longClick())
    }

    fun selectListViewItem(id: Int, value: String) {
        poll {
            onData(hasToString(startsWith(value)))
                .inAdapterView(withId(id))
                .atPosition(0)
                .perform(click())
        }
    }

    fun selectListViewItem(id: Int, fieldName: String, value: String) {
        poll {
            onData(hasEntry(equalTo(fieldName), startsWith(value)))
                .inAdapterView(withId(id))
                .perform(click())
        }
    }

    fun selectSpinnerItem(id: Int, value: String) {
        click(id)

        poll {
            onData(allOf(`is`(instanceOf(String::class.java)), `is`(value))).perform(click())
        }
    }

    fun setChecked(id: Int, checked: Boolean) {
        element(id)?.perform(internalSetChecked(checked))
    }

    fun setText(id: Int, newText: String) {
        element(id)?.perform(closeSoftKeyboard(), replaceText(newText))
    }

    fun string(id: Int): String =
        InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .getString(id)

    fun strip(value: String, stripValue: String): String =
        if (value.endsWith(stripValue)) value.dropLast(stripValue.length).trim()
        else value.trim()

    fun tableHeader(table: DataTable): List<String> =
        table
            .cells()
            .get(0)

    fun tableRows(table: DataTable): List<List<String>> =
        table
            .cells()
            .stream()
            .skip(1)
            .toList()

    fun <T> throwAssert(message: String): T {
        throw AssertionError(message)
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
        return try {
            onView(withId(id))

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun elementWith(text: String): ViewInteraction? {
        return try {
            onView(withText(text))

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun internalSetChecked(checked: Boolean): ViewAction =
        object : ViewAction {
            override fun getConstraints() =
                object : BaseMatcher<View>() {
                    override fun matches(item: Any) = isA(Checkable::class.java).matches(item)

                    override fun describeMismatch(item: Any, mismatchDescription: Description) {}

                    override fun describeTo(description: Description) {}
                }

            override fun getDescription() = ""

            override fun perform(uiController: UiController, view: View) {
                val checkableView = view as Checkable
                checkableView.isChecked = checked
            }
        }

    private fun poll(callback: () -> Unit) {
        poll("", callback)
    }

    private fun poll(message: String, callback: () -> Unit) {
        var exception: Throwable?
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
            !isBlank(message) -> AssertionError(message, exception)
            else -> exception
        }
    }
}
