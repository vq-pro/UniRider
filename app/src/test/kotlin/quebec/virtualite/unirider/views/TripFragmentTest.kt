package quebec.virtualite.unirider.views

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.SpinnerAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.utils.WidgetUtils

@RunWith(MockitoJUnitRunner::class)
class TripFragmentTest {

    @Mock
    lateinit var mockedAdapter: SpinnerAdapter

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedContext: Context

    @Mock
    lateinit var mockedButtonCalculator: Button

    @Mock
    lateinit var mockedSpinnerWheel: Spinner

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @InjectMocks
    var fragment = TripFragment()

    @Before
    fun init() {
        fragment.widgets = mockedWidgets
    }

    @Test
    fun onViewCreated() {
        // Given
        given(mockedView.context)
            .willReturn(mockedContext)

        given<Any>(mockedView.findViewById(R.id.button_calculator))
            .willReturn(mockedButtonCalculator)

        given<Any>(mockedView.findViewById(R.id.wheel_selector))
            .willReturn(mockedSpinnerWheel)

        given<Any>(mockedWidgets.arrayAdapter(any(), any(), any()))
            .willReturn(mockedAdapter)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedWidgets).arrayAdapter(
            mockedContext, R.layout.wheel_item, listOf(
                // FIXME 0 Refactor this
                "<Select Model>", "Gotway Nikola+", "Inmotion V10F", "KingSong 14D", "KingSong S18", "Veteran Sherman"
            )
        )

        verify(mockedButtonCalculator).isEnabled = false
        verify(mockedSpinnerWheel).isEnabled = true
        verify(mockedSpinnerWheel).adapter = mockedAdapter
    }
}
