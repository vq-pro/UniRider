package quebec.virtualite.unirider.views

import android.widget.ListView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.commons.android.utils.CollectionUtils.setList
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.DEVICE3
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR2
import quebec.virtualite.unirider.TestDomain.DEVICE_ADDR3
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME
import quebec.virtualite.unirider.TestDomain.DEVICE_NAME2
import quebec.virtualite.unirider.TestDomain.KM_NEW_RAW
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW
import quebec.virtualite.unirider.TestDomain.MILEAGE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.SHERMAN_MAX_3
import quebec.virtualite.unirider.TestDomain.TEMPERATURE_NEW_RAW
import quebec.virtualite.unirider.TestDomain.VOLTAGE_NEW_RAW
import quebec.virtualite.unirider.bluetooth.WheelInfo

@RunWith(MockitoJUnitRunner::class)
class WheelScanFragmentTest : FragmentTestBase(WheelScanFragment::class.java) {

    private val DEVICE = BluetoothDevice(DEVICE_NAME, DEVICE_ADDR)
    private val DEVICE2 = BluetoothDevice(DEVICE_NAME2, DEVICE_ADDR2)

    @InjectMocks
    lateinit var fragment: WheelScanFragment

    @Mock
    lateinit var mockedLvDevices: ListView

    @Before
    fun before() {
        BaseFragment.wheel = S18_1

        mockField(R.id.devices, mockedLvDevices)

        mockExternal()
        mockFragments()
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
        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        assertThat(fragment.devices, equalTo(emptyList()))

        verifyFieldAssignment(R.id.devices, fragment.lvDevices, mockedLvDevices)

        verifyMultiFieldListAdapter<BluetoothDevice>(mockedLvDevices, android.R.layout.simple_list_item_1, "onDisplayDevice")
        verifyOnItemClick(mockedLvDevices, "onSelectDevice")

        val connectionPayload = DEVICE
        verifyRunWithWaitDialogAndBack()
        verifyConnectorScanWith(connectionPayload)
        verifyDoneWaiting(connectionPayload)

        verify(mockedWidgets).addListViewEntry(mockedLvDevices, fragment.devices, DEVICE)
    }

    @Test
    fun onViewCreated_whenDiscovering2ndDevice_addItToTheDeviceList() {
        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        val connectionPayload = DEVICE2

        verifyRunWithWaitDialogAndBack()
        verifyConnectorScanWith(connectionPayload)
        verifyDoneWaiting(connectionPayload)

        verify(mockedWidgets).addListViewEntry(mockedLvDevices, fragment.devices, DEVICE2)
    }

    @Test
    fun onDestroyView() {
        // When
        fragment.onDestroyView()

        // Then
        verify(mockedConnector).stopScanning()
    }

    @Test
    fun onSelectDevice() {
        // Given
        setList(fragment.devices, listOf(DEVICE, DEVICE2, DEVICE3))
        val selectedDevice = 2

        BaseFragment.wheel = SHERMAN_MAX_3

        // When
        fragment.onSelectDevice().invoke(mockedView, selectedDevice)

        // Then
        val connectionPayload = WheelInfo(KM_NEW_RAW, MILEAGE_NEW_RAW, TEMPERATURE_NEW_RAW, VOLTAGE_NEW_RAW)

        verifyRunWithWaitDialogAndBack()
        verifyConnectorGetDeviceInfo(DEVICE_ADDR3, connectionPayload)
        verifyDoneWaiting(connectionPayload)

        assertThat(
            BaseFragment.wheel, equalTo(SHERMAN_MAX_3.copy(mileage = MILEAGE_NEW))
        )

        verify(mockedDb).saveWheel(BaseFragment.wheel)
        verify(mockedFragments).navigateBack()
    }
}
