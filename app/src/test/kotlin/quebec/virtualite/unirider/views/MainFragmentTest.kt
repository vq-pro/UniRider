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
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class MainFragmentTest {

    private val DONT_ATTACH_TO_ROOT = false
    private val SAVED_INSTANCE_STATE: Bundle? = null

    private val DISTANCE_A = 123
    private val DISTANCE_B = 456
    private val WHEEL_A = "A"
    private val WHEEL_B = "B"
    private val WHEEL_ITEM_A = WheelRow(WHEEL_A, DISTANCE_A)
    private val WHEEL_ITEM_B = WheelRow(WHEEL_B, DISTANCE_B)

    @Mock
    lateinit var mockedDb: WheelDb

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedWheelDistance: TextView

    @Mock
    lateinit var mockedWheelName: TextView

    @Mock
    lateinit var mockedWheels: ListView

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @Captor
    lateinit var lambda: ArgumentCaptor<(View, WheelRow) -> Unit>

    @InjectMocks
    var fragment: MainFragment = TestableMainFragment(this)

    var navigateToId: Int = -1
    var navigateToWith: Pair<String, String>? = null

    @Before
    fun init() {
        given(mockedDb.getWheelList()).willReturn(
            listOf(
                WheelEntity(0, WHEEL_B, DISTANCE_B, 0f, 0f),
                WheelEntity(0, WHEEL_A, DISTANCE_A, 0f, 0f)
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

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // FIXME 1 verify setOnItemClickListener
        // Then
        verify(mockedDb).getWheelList()
        verify(mockedWidgets).customListAdapter(
            eq(mockedWheels), eq(mockedView), eq(R.layout.wheels_item), eq(listOf(WHEEL_ITEM_A, WHEEL_ITEM_B)),
            lambda.capture()
        )
        assertThat(lambda.value.javaClass.name, containsString("MainFragment\$onDisplayWheel\$"))
        verify(mockedWheels).isEnabled = true

        assertThat(fragment.wheelList, equalTo(listOf(WHEEL_ITEM_A, WHEEL_ITEM_B)))
    }

    @Test
    fun onDisplayItem() {
        // Given
        given(mockedView.findViewById<TextView>(R.id.row_name))
            .willReturn(mockedWheelName)

        given(mockedView.findViewById<TextView>(R.id.row_distance))
            .willReturn(mockedWheelDistance)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WHEEL_ITEM_A)

        // Then
        verify(mockedWheelName).text = WHEEL_A
        verify(mockedWheelDistance).text = DISTANCE_A.toString()
    }

    @Test
    fun onSelectWheel() {
        // Given
        fragment.wheelList.clear()
        fragment.wheelList.addAll(listOf(WHEEL_ITEM_A, WHEEL_ITEM_B))

        // When
        fragment.onSelectWheel().invoke(mockedView, 1)

        // Then
        assertThat(navigateToId, equalTo(R.id.action_MainFragment_to_WheelFragment))
        assertThat(navigateToWith, equalTo(Pair(WheelFragment.PARAMETER_WHEEL_NAME, WHEEL_B)))
    }

    class TestableMainFragment(val test: MainFragmentTest) : MainFragment() {

        override fun connectDb() {
            db = test.mockedDb
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T> navigateTo(id: Int, parms: Pair<String, T>?) {
            test.navigateToId = id
            test.navigateToWith = parms as Pair<String, String>
        }

        override fun subThread(function: () -> Unit) {
            function()
        }
    }
}
