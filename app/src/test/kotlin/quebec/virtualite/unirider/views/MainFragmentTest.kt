package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.set
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.commons.views.NavigatedTo
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class MainFragmentTest {

    private val DONT_ATTACH_TO_ROOT = false
    private val SAVED_INSTANCE_STATE: Bundle? = null

    private val MILEAGE_A = 123
    private val MILEAGE_B = 456
    private val MILEAGE_C = 123
    private val WHEEL_A = "A"
    private val WHEEL_B = "B"
    private val WHEEL_C = "C"
    private val WHEEL_ITEM_A_123 = WheelRow(WHEEL_A, MILEAGE_A)
    private val WHEEL_ITEM_B_456 = WheelRow(WHEEL_B, MILEAGE_B)
    private val WHEEL_ITEM_C_123 = WheelRow(WHEEL_C, MILEAGE_C)

    @Mock
    lateinit var mockedDb: WheelDb

    @Mock
    lateinit var mockedTotalMileage: TextView

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedWheelMileage: TextView

    @Mock
    lateinit var mockedWheelName: TextView

    @Mock
    lateinit var mockedWheels: ListView

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @Captor
    lateinit var captorOnDisplay: ArgumentCaptor<(View, WheelRow) -> Unit>

    @Captor
    lateinit var captorOnSelect: ArgumentCaptor<(View, Int) -> Unit>

    @InjectMocks
    var fragment: MainFragment = TestableMainFragment(this)

    lateinit var navigatedTo: NavigatedTo

    @Before
    fun init() {
        given(mockedDb.getWheelList())
            .willReturn(
                listOf(
                    WheelEntity(0, WHEEL_C, MILEAGE_C, 0f, 0f),
                    WheelEntity(0, WHEEL_B, MILEAGE_B, 0f, 0f),
                    WheelEntity(0, WHEEL_A, MILEAGE_A, 0f, 0f),
                )
            )
    }

    @Test
    fun onCreateView() {
        // Given
        val mockedInflater = mock(LayoutInflater::class.java)
        val mockedContainer = mock(ViewGroup::class.java)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verify(mockedInflater).inflate(R.layout.main_fragment, mockedContainer, DONT_ATTACH_TO_ROOT)
    }

    @Test
    fun onViewCreated() {
        // Given
        given<Any>(mockedView.findViewById(R.id.wheels))
            .willReturn(mockedWheels)

        given<Any>(mockedView.findViewById(R.id.total_mileage))
            .willReturn(mockedTotalMileage)

        set(fragment.wheelList, listOf(WheelRow("some previous content", 999)))

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        val expectedWheels = listOf(WHEEL_ITEM_B_456, WHEEL_ITEM_A_123, WHEEL_ITEM_C_123)

        verify(mockedDb).getWheelList()

        verify(mockedWidgets).multifieldListAdapter(
            eq(mockedWheels), eq(mockedView), eq(R.layout.wheels_item), eq(expectedWheels),
            captorOnDisplay.capture()
        )
        assertThat(captorOnDisplay.value.javaClass.name, containsString("MainFragment\$onDisplayWheel\$"))
        verify(mockedWheels).isEnabled = true

        verify(mockedWidgets).setOnItemClickListener(eq(mockedWheels), captorOnSelect.capture())
        assertThat(captorOnSelect.value.javaClass.name, containsString("MainFragment\$onSelectWheel\$"))

        verify(mockedTotalMileage).text = (MILEAGE_A + MILEAGE_B + MILEAGE_C).toString()
    }

    @Test
    fun onDisplayItem() {
        // Given
        given(mockedView.findViewById<TextView>(R.id.row_name))
            .willReturn(mockedWheelName)

        given(mockedView.findViewById<TextView>(R.id.row_mileage))
            .willReturn(mockedWheelMileage)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WHEEL_ITEM_A_123)

        // Then
        verify(mockedWheelName).text = WHEEL_A
        verify(mockedWheelMileage).text = MILEAGE_A.toString()
    }

    @Test
    fun onSelectWheel() {
        // Given
        fragment.wheelList += listOf(WHEEL_ITEM_A_123, WHEEL_ITEM_B_456)

        // When
        fragment.onSelectWheel().invoke(mockedView, 1)

        // Then
        assertThat(
            navigatedTo, equalTo(
                NavigatedTo(
                    R.id.action_MainFragment_to_WheelFragment,
                    Pair(WheelFragment.PARAMETER_WHEEL_NAME, WHEEL_B)
                )
            )
        )
    }

    class TestableMainFragment(val test: MainFragmentTest) : MainFragment() {

        override fun connectDb(function: () -> Unit) {
            db = test.mockedDb
            function()
        }

        override fun navigateTo(id: Int, parms: Pair<String, String>) {
            test.navigatedTo = NavigatedTo(id, parms)
        }
    }
}
