package quebec.virtualite.unirider.views

import android.app.Dialog
import android.widget.ListView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.DEVICE
import quebec.virtualite.unirider.TestDomain.DEVICE2
import quebec.virtualite.unirider.TestDomain.DEVICE3
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR2
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR3
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME2
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME3
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.ID3
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.NAME3
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.SHERMAN_3
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MAX3
import quebec.virtualite.unirider.TestDomain.VOLTAGE_MIN3
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class WheelScanFragmentTest : BaseFragmentTest(WheelScanFragment::class.java) {

    @InjectMocks
    val fragment: WheelScanFragment = TestableWheelScanFragment(this)

    @Mock
    lateinit var mockedLvWheels: ListView

    @Mock
    lateinit var mockedWaitDialog: Dialog

    @Before
    fun before() {
        fragment.wheel = S18_1

        mockField(R.id.devices, mockedLvWheels)

        given(mockedWidgets.showWaitDialog(any()))
            .willReturn(mockedWaitDialog)
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
            .willReturn(SHERMAN_3)

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        assertThat(fragment.lvWheels, equalTo(mockedLvWheels))
        assertThat(fragment.wheel, equalTo(SHERMAN_3))

        verifyOnItemClick(mockedLvWheels, "onSelectDevice")

        verify(mockedWidgets).showWaitDialog(any())
        verify(mockedDb).getWheel(ID)
        verifyConnectorScanWith(Device(DEVICE_NAME, DEVICE_ADDR))

        verifyStringListAdapter(mockedLvWheels, listOf(DEVICE_NAME))
        verify(mockedWaitDialog).hide()
    }

    @Test
    fun onViewCreated_whenDiscovering2ndDevice_addItToTheDeviceList() {
        // Given
        setList(fragment.devices, listOf(DEVICE))

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        verifyConnectorScanWith(Device(DEVICE_NAME2, DEVICE_ADDR2))
        verifyStringListAdapter(mockedLvWheels, listOf(DEVICE_NAME, DEVICE_NAME2))
    }

    @Test
    fun onSelectDevice() {
        // Given
        setList(fragment.devices, listOf(DEVICE, DEVICE2, DEVICE3))
        val selectedDevice = 2

        fragment.wheel = SHERMAN_3

        // When
        fragment.onSelectDevice().invoke(mockedView, selectedDevice)

        // Then
        val ordered = inOrder(mockedWidgets, mockedWaitDialog, mockedDb)
        ordered.verify(mockedWidgets).showWaitDialog(any())
        verifyConnectorGetDeviceInfo(DEVICE_ADDR3, DeviceInfo(MILEAGE_NEW_RAW, VOLTAGE_NEW_RAW))
        ordered.verify(mockedDb).saveWheel(
            WheelEntity(ID3, NAME3, DEVICE_NAME3, DEVICE_ADDR3, MILEAGE_NEW, VOLTAGE_MIN3, VOLTAGE_MAX3)
        )
        ordered.verify(mockedWaitDialog).hide()
        verifyNavigatedBack()
    }

    class TestableWheelScanFragment(val test: WheelScanFragmentTest) : WheelScanFragment() {

        override fun initConnector() {
        }

        override fun initDB(function: () -> Unit) {
            test.initDB(this, function)
        }

        override fun navigateBack(nb: Int) {
            test.navigateBack(nb)
        }

        override fun runBackground(function: () -> Unit) {
            test.runBackground(function)
        }

        override fun runDB(function: () -> Unit) {
            test.runDB(function)
        }

        override fun runUI(function: () -> Unit) {
            test.runUI(function)
        }
    }
}