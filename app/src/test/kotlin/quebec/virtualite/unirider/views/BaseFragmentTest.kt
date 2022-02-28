package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains.containsString
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.doAnswer
import org.mockito.BDDMockito.given
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.commons.views.FragmentServices
import quebec.virtualite.commons.views.NavigatedTo
import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.database.WheelDb

open class BaseFragmentTest(fragmentType: Class<*>) {

    internal val fragmentClass: String = fragmentType.simpleName

    internal val DONT_ATTACH_TO_ROOT = false
    internal val SAVED_INSTANCE_STATE: Bundle? = null

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedConnector: WheelConnector

    @Mock
    lateinit var mockedContainer: ViewGroup

    @Mock
    lateinit var mockedDb: WheelDb

    @Mock
    lateinit var mockedInflater: LayoutInflater

    @Mock
    lateinit var mockedServices: FragmentServices

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @Captor
    private lateinit var lambdaOnClick: ArgumentCaptor<(View) -> Unit>

    @Captor
    private lateinit var lambdaOnDisplay: ArgumentCaptor<(View, Any) -> Unit>

    @Captor
    private lateinit var lambdaOnFoundDevice: ArgumentCaptor<(Device) -> Unit>

    @Captor
    private lateinit var lambdaOnGotDeviceInfo: ArgumentCaptor<(DeviceInfo) -> Unit>

    @Captor
    private lateinit var lambdaOnItemClick: ArgumentCaptor<(View, Int) -> Unit>

    @Captor
    private lateinit var lambdaOnUpdateText: ArgumentCaptor<(String) -> Unit>

    @Captor
    private lateinit var lambdaRunWithWaitDialog: ArgumentCaptor<() -> Unit>

    private var navigatedBack: Int = 0
    private var navigatedTo: NavigatedTo? = null

    fun initDB(fragment: BaseFragment, function: () -> Unit) {
        fragment.db = mockedDb
        function()
    }

    fun mockArgument(fragment: BaseFragment, param: String, value: Long) {
        given(mockedBundle.getLong(param))
            .willReturn(value)

        fragment.arguments = mockedBundle
    }

    fun mockArgument(fragment: BaseFragment, param: String, value: String) {
        given(mockedBundle.getString(param))
            .willReturn(value)

        fragment.arguments = mockedBundle
    }

    fun mockField(id: Int, mockedField: View) {
        given<Any>(mockedView.findViewById(id))
            .willReturn(mockedField)
    }

    @Suppress("UNCHECKED_CAST")
    fun mockServices() {
        doAnswer { (it.arguments[0] as (() -> Unit)).invoke() }
            .`when`(mockedServices).runUI(any())
    }

    fun navigateTo(id: Int, parms: Pair<String, Any>) {
        navigatedTo = NavigatedTo(id, parms)
    }

    fun runBackground(function: () -> Unit) {
        function()
    }

    fun runDB(function: () -> Unit) {
        function()
    }

    fun verifyConnectorGetDeviceInfo(expectedDeviceAddress: String, deviceInfo: DeviceInfo) {
        verify(mockedConnector).getDeviceInfo(eq(expectedDeviceAddress), lambdaOnGotDeviceInfo.capture())
        lambdaOnGotDeviceInfo.value.invoke(deviceInfo)
    }

    fun verifyConnectorScanWith(device: Device) {
        verify(mockedConnector).scan(lambdaOnFoundDevice.capture())
        lambdaOnFoundDevice.value.invoke(device)
    }

    fun verifyInflate(expectedId: Int) {
        verify(mockedInflater).inflate(expectedId, mockedContainer, DONT_ATTACH_TO_ROOT)
    }

    fun verifyOnClick(mockedField: View, methodName: String) {
        verify(mockedWidgets).setOnClickListener(eq(mockedField), lambdaOnClick.capture())
        assertThat(lambdaOnClick.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnItemClick(mockedField: ListView, methodName: String) {
        verify(mockedWidgets).setOnItemClickListener(eq(mockedField), lambdaOnItemClick.capture())
        assertThat(lambdaOnItemClick.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnLongClick(mockedField: View, methodName: String) {
        verify(mockedWidgets).setOnLongClickListener(eq(mockedField), lambdaOnClick.capture())
        assertThat(lambdaOnClick.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnUpdateText(mockedField: EditText, methodName: String) {
        verify(mockedWidgets).addTextChangedListener(eq(mockedField), lambdaOnUpdateText.capture())
        assertThat(lambdaOnUpdateText.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyRunWithWaitDialog() {
        verify(mockedServices).runWithWaitDialog(lambdaRunWithWaitDialog.capture())
        lambdaRunWithWaitDialog.value.invoke()
    }

    fun verifyRunWithWaitDialogAndBack() {
        verify(mockedServices).runWithWaitDialogAndBack(lambdaRunWithWaitDialog.capture())
        lambdaRunWithWaitDialog.value.invoke()
    }

    fun verifyStringListAdapter(mockedField: ListView, expectedData: List<String>) {
        verify(mockedWidgets).stringListAdapter(mockedField, mockedView, expectedData)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> verifyMultiFieldListAdapter(
        mockedField: ListView, expectedId: Int, expectedData: List<T>, methodName: String
    ) {
        verify(mockedWidgets).multifieldListAdapter(
            eq(mockedField), eq(mockedView), eq(expectedId), eq(expectedData),
            (lambdaOnDisplay as ArgumentCaptor<(View, T) -> Unit>).capture()
        )
        assertThat(lambdaOnDisplay.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyNavigatedTo(id: Int, param: Pair<String, Any>) {
        assertThat("Didn't navigate to where we expected to", navigatedTo, equalTo(NavigatedTo(id, param)))
    }
}
