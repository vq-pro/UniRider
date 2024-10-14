package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import quebec.virtualite.unirider.R

open class WheelDeleteConfirmationFragment : BaseFragment() {

    internal lateinit var buttonDeleteCancel: Button
    internal lateinit var buttonDeleteConfirmation: Button
    internal lateinit var textName: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wheel_delete_confirmation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textName = view.findViewById(R.id.view_name)
        buttonDeleteConfirmation = view.findViewById(R.id.button_delete_confirmation)
        buttonDeleteCancel = view.findViewById(R.id.button_delete_cancel)

        widgets.setOnClickListener(buttonDeleteConfirmation, onDelete())
        widgets.setOnClickListener(buttonDeleteCancel, onCancel())

        textName.text = wheel!!.name
    }

    fun onCancel(): (View) -> Unit = {
        fragments.navigateBack()
    }

    fun onDelete(): (View) -> Unit = {
        external.runDB { db ->
            db.deleteWheel(wheel!!.id)
            wheel = null
        }

        fragments.navigateBack(3)
    }
}
