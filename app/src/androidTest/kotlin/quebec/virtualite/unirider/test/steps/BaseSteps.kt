package quebec.virtualite.unirider.test.steps

import quebec.virtualite.unirider.commons.android.utils.StepsUtils.applicationContext
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.test.app.TestApp
import quebec.virtualite.unirider.test.domain.TestDomain

abstract class BaseSteps {

    companion object {
        @JvmStatic
        internal val app = TestApp()

        @JvmStatic
        internal val domain = TestDomain(applicationContext())

        internal lateinit var selectedWheel: WheelEntity
        internal lateinit var updatedWheel: WheelEntity
    }

    open fun beforeScenario() {
        domain.clear()
    }

    open fun afterScenario() {
        app.stop()
    }
}