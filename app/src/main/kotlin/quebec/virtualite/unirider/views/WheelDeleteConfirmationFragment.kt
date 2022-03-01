package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.exceptions.WheelNotFoundException
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID

open class WheelDeleteConfirmationFragment : BaseFragment() {

    internal lateinit var buttonDeleteCancel: Button
    internal lateinit var buttonDeleteConfirmation: Button
    internal lateinit var textName: TextView

    internal lateinit var wheel: WheelEntity

    internal var parmWheelId: Long? = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parmWheelId = arguments?.getLong(PARAMETER_WHEEL_ID)
        return inflater.inflate(R.layout.wheel_delete_confirmation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textName = view.findViewById(R.id.view_name)
        buttonDeleteConfirmation = view.findViewById(R.id.button_delete_confirmation)
        buttonDeleteCancel = view.findViewById(R.id.button_delete_cancel)

        widgets.setOnClickListener(buttonDeleteConfirmation, onDelete())
        widgets.setOnClickListener(buttonDeleteCancel, onCancel())

        services.runDB {
            wheel = db.getWheel(parmWheelId!!)
                ?: throw WheelNotFoundException()

            textName.text = wheel.name
        }
    }

    fun onCancel() = { _: View ->
        services.navigateBack()
    }

    fun onDelete() = { _: View ->
        services.runDB { db.deleteWheel(wheel.id) }
        services.navigateBack(2)
    }
}
