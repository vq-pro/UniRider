package quebec.virtualite.unirider.test.fragments

import org.hamcrest.Matchers.equalTo
import quebec.virtualite.unirider.commons.android.utils.StepUtils.assertThatPolling
import quebec.virtualite.unirider.test.steps.BaseSteps.Companion.app
import quebec.virtualite.unirider.views.BaseFragment
import kotlin.reflect.KClass

open class BaseFragmentObject
{
    fun <F: BaseFragment> validateView(expectedFragment: KClass<F>)
    {
        assertThatPolling({ app.activeFragment() }, equalTo(expectedFragment.java) )
    }
}