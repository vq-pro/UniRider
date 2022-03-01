package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.commons.views.FragmentServices
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.services.ExternalServices

open class BaseFragment : Fragment() {

    internal lateinit var connector: WheelConnector
    internal lateinit var db: WheelDb

    internal var externalServices = ExternalServices(this)
    internal var services = FragmentServices(this, R.string.dialog_wait)
    internal var widgets = WidgetUtils()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        externalServices.init()

        connector = externalServices.connector()
        db = externalServices.db()
    }

    // FIXME-0 Cleanup
    internal open fun initConnector() {
    }

    // FIXME-0 Cleanup
    internal open fun initDB(function: () -> Unit) {
        services.runDB(function)
    }
}
