package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.utils.WidgetUtils

@RunWith(MockitoJUnitRunner::class)
class MainFragmentTest {

    val DONT_ATTACH_TO_ROOT = false

    val WHEEL_A = "A"
    val WHEEL_B = "B"

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedDb: WheelDb

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedWheels: ListView

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @InjectMocks
    var fragment: TestableMainFragment = TestableMainFragment()

    @Before
    fun init() {
        fragment.mockedDb = mockedDb

        given(mockedDb.getWheelList())
            .willReturn(listOf(WHEEL_B, WHEEL_A))
    }

    @Test
    fun onCreateView() {
        // Given
        val mockedInflater = mock(LayoutInflater::class.java)
        val mockedContainer = mock(ViewGroup::class.java)
        val mockedInstance = mock(Bundle::class.java)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, mockedInstance)

        // Then
        verify(mockedInflater).inflate(R.layout.main_fragment, mockedContainer, DONT_ATTACH_TO_ROOT)
    }

    @Test
    fun onViewCreated() {
        // Given
        given<Any>(mockedView.findViewById(R.id.wheels))
            .willReturn(mockedWheels)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheelList()
        verify(mockedWidgets).listAdapter(
            mockedView, R.layout.wheels_item,
            listOf(WHEEL_A, WHEEL_B)
        )

        verify(mockedWheels).isEnabled = true

        assertThat(fragment.wheelList, equalTo(listOf(WHEEL_A, WHEEL_B)))
    }

    @Test
    fun onSelectWheel() {
        // Given
        fragment.wheelList.clear()
        fragment.wheelList.addAll(listOf(WHEEL_A, WHEEL_B))

        // When
        fragment.onSelectWheel().invoke(mockedView, 1)

        // Then
        assertThat(fragment.navigateToId, equalTo(R.id.action_MainFragment_to_CalculatorFragment))
        assertThat(fragment.navigateToWith, equalTo(Pair("wheel", WHEEL_B)))
    }

    class TestableMainFragment : MainFragment() {

        lateinit var mockedDb: WheelDb
        var navigateToId: Int = -1
        var navigateToWith: Pair<String, String>? = null

        override fun connectDb(): WheelDb {
            return mockedDb
        }

        override fun navigateTo(id: Int, parms: Pair<String, String>?) {
            navigateToId = id
            navigateToWith = parms
        }

        override fun subThread(function: () -> Unit) {
            function()
        }
    }
}
