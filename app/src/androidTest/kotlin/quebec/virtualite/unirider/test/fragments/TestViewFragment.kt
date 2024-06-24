package quebec.virtualite.unirider.test.fragments

import org.hamcrest.Matchers.equalTo
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.assertThat
import quebec.virtualite.unirider.commons.android.utils.StepsUtils.hasText
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.views.WheelViewFragment

class TestViewFragment(val app: TestApp) {

    fun validateViewing(wheel: WheelEntity) {
        assertThat(app.activeFragment(), equalTo(WheelViewFragment::class.java))
        assertThat(R.id.view_name, hasText(wheel.name))
    }
}
