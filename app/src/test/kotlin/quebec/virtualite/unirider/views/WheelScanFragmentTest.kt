package quebec.virtualite.unirider.views

import android.widget.ListView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.WheelScannerImpl
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class WheelScanFragmentTest :
    BaseFragmentTest(WheelScanFragment::class.java) {

    private val DEVICE_ADDR = "ABCDEF"
    private val DEVICE_NAME = "LK2000"
    private val DEVICE_NAME2 = "LK2002"
    private val ID = 1111L
    private val MILEAGE = 2222
    private val NAME = "Sherman"
    private val VOLTAGE_MAX = 100.8f
    private val VOLTAGE_MIN = 75.6f
    private val WHEEL = WheelEntity(ID, NAME, "", MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)

    private val DEVICE = Device(DEVICE_NAME, DEVICE_ADDR)

    @InjectMocks
    val fragment: WheelScanFragment = TestableWheelScanFragment(this)

    @Mock
    lateinit var mockedLvWheels: ListView

    @Mock
    lateinit var mockedScanner: WheelScannerImpl

    @Before
    fun before() {
        fragment.wheel = WHEEL

        mockField(R.id.devices, mockedLvWheels)
    }

    @Test
    fun onCreateView() {
        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_scan_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        fragment.parmWheelId = ID
        fragment.wheel = null

        given(mockedDb.getWheel(ID))
            .willReturn(WHEEL)

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.lvWheels, equalTo(mockedLvWheels))
        assertThat(fragment.wheel, equalTo(WHEEL))

        verify(mockedLvWheels).isEnabled = true
        verifyStringListAdapter(mockedLvWheels, listOf(DEVICE_NAME))
        verifyOnItemClick(mockedLvWheels, "onSelectDevice")
    }

    @Test
    fun onSelectDevice() {
        // Given
        setList(fragment.devices, listOf(DEVICE_NAME, DEVICE_NAME2))

        // When
        fragment.onSelectDevice().invoke(mockedView, 1)

        // Then
        verify(mockedDb).saveWheel(WheelEntity(ID, NAME, DEVICE_NAME2, 695, VOLTAGE_MIN, VOLTAGE_MAX))
        verifyNavigatedBack()
    }

    class TestableWheelScanFragment(val test: WheelScanFragmentTest) : WheelScanFragment() {

        override fun connectDb(function: () -> Unit) {
            test.connectDb(this, function)
        }

        override fun connectScanner(function: (Device) -> Unit) {
            function.invoke(test.DEVICE)
        }

        override fun navigateBack(nb: Int) {
            test.navigateBack(nb)
        }

        override fun runDb(function: () -> Unit) {
            test.runDb(function)
        }
    }
}