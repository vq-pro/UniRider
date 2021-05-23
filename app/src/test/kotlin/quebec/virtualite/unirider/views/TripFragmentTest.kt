package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Spinner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R

@RunWith(MockitoJUnitRunner::class)
class TripFragmentTest {

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedButtonCalculator: Button

    @Mock
    lateinit var mockedSpinnerWheel: Spinner

    @Mock
    lateinit var mockedView: View

    @InjectMocks
    var fragment = TripFragment()

    @Test
    fun onViewCreated() {
        // Given
        given<Any>(mockedView.findViewById(R.id.button_calculator))
            .willReturn(mockedButtonCalculator)

        given<Any>(mockedView.findViewById(R.id.wheel_selector))
            .willReturn(mockedSpinnerWheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedButtonCalculator).isEnabled = false
        verify(mockedSpinnerWheel).isEnabled = true
//        verify(mockedSpinnerWheel).adapter = ArrayAdapter(mockedView.context, R.layout.wheel_item, arrayOf(""))
    }
}
