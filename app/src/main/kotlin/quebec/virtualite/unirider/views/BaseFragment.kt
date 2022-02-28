package quebec.virtualite.unirider.views

import androidx.fragment.app.Fragment
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.commons.views.FragmentServices
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.database.WheelDb

open class BaseFragment : Fragment() {

    internal lateinit var connector: WheelConnector
    internal lateinit var db: WheelDb

    internal var services = FragmentServices(this, R.string.dialog_wait)
    internal var widgets = WidgetUtils()

    internal open fun initConnector() {
        connector = MainActivity.connector
    }

    internal open fun initDB(function: () -> Unit) {
        db = MainActivity.db
        services.runDB(function)
    }
}
