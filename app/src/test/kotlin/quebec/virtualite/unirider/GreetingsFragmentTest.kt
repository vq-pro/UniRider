package quebec.virtualite.unirider

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.eq
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.services.GreetingResponse
import quebec.virtualite.unirider.services.SimulatedBackend
import quebec.virtualite.unirider.views.GreetingsFragment

private const val NAME = "Toto"

@Ignore("Not necessary to slow down build for this")
@RunWith(MockitoJUnitRunner::class)
class GreetingsFragmentTest {
    @InjectMocks
    var fragment = GreetingsFragment()

    @Mock
    lateinit var mockedBackend: SimulatedBackend

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedButtonOk: Button

    @Mock
    lateinit var mockedFieldName: EditText

    @Mock
    lateinit var mockedFieldTitleMessage: TextView

    @Mock
    lateinit var mockedView: View

    @Captor
    lateinit var captorBackendSuccess: ArgumentCaptor<(GreetingResponse) -> Unit>

    @Test
    fun onViewCreated() {
        // Given
        given<Any>(mockedView.findViewById(R.id.send))
            .willReturn(mockedButtonOk)

        given<Any>(mockedView.findViewById(R.id.title_message))
            .willReturn(mockedFieldTitleMessage)

        given<Any>(mockedView.findViewById(R.id.name))
            .willReturn(mockedFieldName)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedView).findViewById<Button>(R.id.send)
        verify(mockedButtonOk).setOnClickListener(any())
    }

    @Test
    fun onOkButton_whenFirstTime() {
        // Given
        mockFieldNameWith(NAME)

        // When
        fragment.onOkButton().invoke(mockedView)

        // Then
        verify(mockedFieldName).text
        verify(mockedBackend).greet(eq(NAME), captorBackendSuccess.capture())

        captorBackendSuccess.value!!.invoke(GreetingResponse("Hello $NAME", 1))
        verify(mockedFieldTitleMessage).text = "Hello $NAME"
        verify(mockedFieldName).setText("")
    }

    @Test
    fun onOkButton_whenRepeatedTime() {
        // Given
        mockFieldNameWith(NAME)

        // When
        fragment.onOkButton().invoke(mockedView)

        // Then
        verify(mockedFieldName).text
        verify(mockedBackend).greet(eq(NAME), captorBackendSuccess.capture())

        captorBackendSuccess.value!!.invoke(GreetingResponse("Hello $NAME", 3))
        verify(mockedFieldTitleMessage).text = "Hello $NAME (3)"
        verify(mockedFieldName).setText("")
    }

    private fun mockFieldNameWith(name: String) {
        val mockedEditable = Mockito.mock(Editable::class.java)
        given(mockedEditable.toString())
            .willReturn(name)

        given(mockedFieldName.text)
            .willReturn(mockedEditable)
    }
}