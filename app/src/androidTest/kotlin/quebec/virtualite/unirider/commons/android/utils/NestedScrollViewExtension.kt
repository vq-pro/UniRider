package quebec.virtualite.unirider.commons.android.utils

import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ListView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anyOf

/**
 * @see <a href="https://medium.com/@devasierra/espresso-nestedscrollview-scrolling-via-kotlin-delegation-5e7f0aa64c09">View original article</a>
 */
class NestedScrollViewExtension(scrolltoAction: ViewAction = scrollTo()) : ViewAction by scrolltoAction {
    override fun getConstraints(): Matcher<View> {
        return allOf(
            withEffectiveVisibility(Visibility.VISIBLE),
            isDescendantOfA(
                anyOf(
                    isAssignableFrom(NestedScrollView::class.java),
                    isAssignableFrom(ScrollView::class.java),
                    isAssignableFrom(HorizontalScrollView::class.java),
                    isAssignableFrom(ListView::class.java)
                )
            )
        )
    }
}
