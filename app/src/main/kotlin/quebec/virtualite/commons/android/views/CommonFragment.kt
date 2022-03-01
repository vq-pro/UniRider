package quebec.virtualite.commons.android.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import quebec.virtualite.unirider.services.ExternalServices

abstract class CommonFragment : Fragment() {

    internal var externalServices = getExternalService()
    internal var services = CommonFragmentServices(this, getWaitMessageString())
    internal var widgets = CommonWidgetServices()

    // FIXME-0 Generalize ExternalServices
    abstract fun getExternalService(): ExternalServices
    abstract fun getWaitMessageString(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        externalServices.init()
    }
}
