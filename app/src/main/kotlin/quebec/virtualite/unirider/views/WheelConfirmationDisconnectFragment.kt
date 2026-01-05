package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import quebec.virtualite.unirider.R

open class WheelConfirmationDisconnectFragment : BaseFragment() {

    internal lateinit var buttonDisconnectCancel: Button
    internal lateinit var buttonDisconnectConfirmation: Button
    internal lateinit var textName: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wheel_confirmation_disconnect_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textName = view.findViewById(R.id.view_name)
        buttonDisconnectConfirmation = view.findViewById(R.id.button_disconnect_confirmation)
        buttonDisconnectCancel = view.findViewById(R.id.button_disconnect_cancel)

        widgets.setOnClickListener(buttonDisconnectConfirmation, onDisconnect())
        widgets.setOnClickListener(buttonDisconnectCancel, onCancel())

        textName.text = wheel!!.name
    }

    fun onCancel(): (View) -> Unit = {
        fragments.navigateBack()
    }

    fun onDisconnect(): (View) -> Unit = {
        external.runDB { db ->
            wheel = wheel!!.copy(btName = null, btAddr = null)
            db.saveWheel(wheel)
        }

        fragments.navigateBack()
    }
}
