package quebec.virtualite.unirider.test.app

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import quebec.virtualite.unirider.views.MainActivity

class TestApp {
    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var mainActivity: MainActivity

    fun activeFragment(): Class<Fragment> {
        return mainActivity.supportFragmentManager.fragments[0].childFragmentManager.fragments[0].javaClass
    }

    fun back(nb: Int = 1) {
        var i = nb
        while (i-- > 0) {
            pressBackUnconditionally()
        }
    }

    fun stop() {
        activityTestRule.finishActivity()
    }

    fun start() {
        activityTestRule.launchActivity(Intent())
        mainActivity = activityTestRule.activity
    }
}
