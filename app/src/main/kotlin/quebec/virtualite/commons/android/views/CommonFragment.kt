package quebec.virtualite.commons.android.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import quebec.virtualite.commons.android.external.CommonExternalServices

abstract class CommonFragment<E : CommonExternalServices> : Fragment() {

    internal var external: E = getExternalServices()
    internal var fragments = CommonFragmentServices(this, getWaitMessageStringId())
    internal var widgets = CommonWidgetServices()

    abstract fun getExternalServices(): E
    abstract fun getWaitMessageStringId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        external.init()
    }
}
