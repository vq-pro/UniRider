package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.services.GreetingResponse
import quebec.virtualite.unirider.services.SimulatedBackend
import quebec.virtualite.unirider.services.client.SimulatedBackendClient

class GreetingsFragment : Fragment()
{
    private lateinit var backendClient: SimulatedBackend

    private lateinit var buttonOk: Button
    private lateinit var fieldName: EditText
    private lateinit var fieldTitleMessage: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        : View?
    {
        return inflater.inflate(R.layout.greetings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        backendClient = SimulatedBackendClient()

        fieldTitleMessage = view.findViewById(R.id.title_message)
        fieldName = view.findViewById(R.id.name)
        buttonOk = view.findViewById(R.id.send)

        buttonOk.setOnClickListener(onOkButton())
    }

    internal fun onOkButton(): (View) -> Unit
    {
        return {
            val name = fieldName.text.toString()

            backendClient.greet(name)
            { response: GreetingResponse ->

                fieldTitleMessage.text = when (response.nb)
                {
                    1 -> response.content
                    else -> response.content + " (" + response.nb + ")"
                }

                fieldName.setText("")
            }
        }
    }
}
