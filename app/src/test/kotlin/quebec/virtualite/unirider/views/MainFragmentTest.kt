package quebec.virtualite.unirider.views

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.SpinnerAdapter
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
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
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.utils.WidgetUtils

@RunWith(MockitoJUnitRunner::class)
class MainFragmentTest {

    val SELECT_WHEEL = "<Select Model>"
    val WHEEL_A = "A"
    val WHEEL_B = "B"

    @Mock
    lateinit var mockedAdapter: SpinnerAdapter

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedButtonCalculator: Button

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedContext: Context

    @Mock
    lateinit var mockedSpinnerWheel: Spinner

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @InjectMocks
    var fragment = MainFragment()

    @Before
    fun init() {
        fragment.widgets = mockedWidgets

        given(mockedCalculatorService.wheels())
            .willReturn(listOf(WHEEL_A, WHEEL_B))
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
        verify(mockedWidgets)
            .arrayAdapter(
                mockedContext, R.layout.wheel_item, listOf(
                    SELECT_WHEEL, WHEEL_A, WHEEL_B
                )
            )

        verify(mockedButtonCalculator).isEnabled = false
        verify(mockedSpinnerWheel).isEnabled = true
        verify(mockedSpinnerWheel).adapter = mockedAdapter
    }

    @Test
    fun onSelectWheel_whenActualWheel() {
        // When
        fragment.onSelectWheel().invoke(1)

        // Then
        verify(mockedButtonCalculator).isEnabled = true
        assertThat(fragment.wheel, equalTo(WHEEL_A))
    }

    @Test
    fun onSelectWheel_whenSelectModel() {
        // When
        fragment.onSelectWheel().invoke(0)

        // Then
        verify(mockedButtonCalculator).isEnabled = false
        assertThat(fragment.wheel, equalTo(SELECT_WHEEL))
    }
}
