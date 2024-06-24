package quebec.virtualite.unirider.test.fragments

import org.hamcrest.Matchers.equalTo
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.views.WheelEditFragment

class TestEditFragment(val app: TestApp) {
    fun validateView() {
        assertThat(app.activeFragment(), equalTo(WheelEditFragment::class.java))
    }
}
