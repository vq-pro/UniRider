package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains.containsString
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.lenient
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.commons.android.views.CommonFragmentServices
import quebec.virtualite.commons.android.views.CommonWidgetServices
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain
import quebec.virtualite.unirider.bluetooth.BluetoothServices
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.services.ExternalServices

open class BaseFragmentTest(fragmentType: Class<*>) {

    internal val fragmentClass: String = fragmentType.simpleName

    internal val DONT_ATTACH_TO_ROOT = false
    internal val SAVED_INSTANCE_STATE: Bundle? = null

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedConnector: BluetoothServices

    @Mock
    lateinit var mockedContainer: ViewGroup

    @Mock
    lateinit var mockedDb: WheelDb

    @Mock
    lateinit var mockedExternal: ExternalServices

    @Mock
    lateinit var mockedFragments: CommonFragmentServices

    @Mock
    lateinit var mockedInflater: LayoutInflater

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedWidgets: CommonWidgetServices

    @Captor
    private lateinit var captorOnClick: ArgumentCaptor<(View) -> Unit>

    @Captor
    private lateinit var captorOnDisplay: ArgumentCaptor<(View, Any) -> Unit>

    @Captor
    private lateinit var captorOnFoundDevice: ArgumentCaptor<(BluetoothDevice) -> Unit>

    @Captor
    private lateinit var captorOnGotDeviceInfo: ArgumentCaptor<(WheelInfo?) -> Unit>

    @Captor
    private lateinit var captorOnItemClick: ArgumentCaptor<(View, Int) -> Unit>

    @Captor
    private lateinit var captorOnItemSelected: ArgumentCaptor<(View?, Int, String) -> Unit>

    @Captor
    private lateinit var captorOnUpdateCheckBox: ArgumentCaptor<(Boolean) -> Unit>

    @Captor
    private lateinit var captorOnUpdateText: ArgumentCaptor<(String) -> Unit>

    @Captor
    private lateinit var captorRunWithWaitDialog: ArgumentCaptor<() -> Unit>

    fun mockArgument(fragment: BaseFragment, param: String, value: Float) {
        given(mockedBundle.getFloat(param))
            .willReturn(value)

        fragment.arguments = mockedBundle
    }

    fun mockArgument(fragment: BaseFragment, param: String, value: Int) {
        given(mockedBundle.getInt(param))
            .willReturn(value)

        fragment.arguments = mockedBundle
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

    fun mockArgument(fragment: BaseFragment, param: String, value: List<String>) {
        given(mockedBundle.getStringArrayList(param))
            .willReturn(ArrayList(value))

        fragment.arguments = mockedBundle
    }

    @Suppress("UNCHECKED_CAST")
    fun mockExternal() {
        lenient().doReturn(mockedConnector)
            .`when`(mockedExternal).bluetooth()

        lenient().doReturn(mockedDb)
            .`when`(mockedExternal).db()

        lenient().doAnswer { (it.arguments[0] as ((WheelDb) -> Unit)).invoke(mockedDb) }
            .`when`(mockedExternal).runDB(any())
    }

    fun mockField(id: Int, mockedField: View) {
        given<Any>(mockedView.findViewById(id))
            .willReturn(mockedField)
    }

    @Suppress("UNCHECKED_CAST")
    fun mockFragments() {
        lenient().doAnswer { (it.arguments[0] as (() -> Unit)).invoke() }
            .`when`(mockedFragments).runBackground(any())

        lenient().doAnswer { (it.arguments[0] as (() -> Unit)).invoke() }
            .`when`(mockedFragments).runUI(any())
    }

    fun mockStrings() {
        given(mockedFragments.string(R.string.label_wheel_sold))
            .willReturn(TestDomain.ITEM_SOLD)
    }

    fun verifyConnectorGetDeviceInfo(expectedDeviceAddress: String, wheelInfo: WheelInfo) {
        verify(mockedConnector).getDeviceInfo(eq(expectedDeviceAddress), captorOnGotDeviceInfo.capture())
        captorOnGotDeviceInfo.value.invoke(wheelInfo)
    }

    fun verifyConnectorScanWith(device: BluetoothDevice) {
        verify(mockedConnector).scan(captorOnFoundDevice.capture())
        captorOnFoundDevice.value.invoke(device)
    }

    fun verifyDoneWaiting(connectionPayload: Any) {
        verify(mockedFragments).doneWaiting(eq(connectionPayload), captorRunWithWaitDialog.capture())
        captorRunWithWaitDialog.value.invoke()
    }

    fun <T : View?> verifyFieldAssignment(id: Int, field: T, mock: T) {
        verify(mockedView).findViewById<T>(id)
        assertThat(mock, equalTo(field))
    }

    fun verifyInflate(expectedId: Int) {
        verify(mockedInflater).inflate(expectedId, mockedContainer, DONT_ATTACH_TO_ROOT)
    }

    fun <T> verifyMultiFieldListAdapter(
        mockedField: ListView, expectedId: Int, methodName: String
    ) {
        verifyMultiFieldListAdapter(mockedField, expectedId, emptyList<T>(), methodName)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> verifyMultiFieldListAdapter(
        mockedField: ListView, expectedId: Int, expectedData: List<T>, methodName: String
    ) {
        verify(mockedWidgets).multifieldListAdapter(
            eq(mockedField), eq(mockedView), eq(expectedId), eq(expectedData),
            (captorOnDisplay as ArgumentCaptor<(View, T) -> Unit>).capture()
        )
        assertThat(captorOnDisplay.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnClick(mockedField: View, methodName: String) {
        verify(mockedWidgets).setOnClickListener(eq(mockedField), captorOnClick.capture())
        assertThat(captorOnClick.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnItemClick(mockedField: ListView, methodName: String) {
        verify(mockedWidgets).setOnItemClickListener(eq(mockedField), captorOnItemClick.capture())
        assertThat(captorOnItemClick.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnItemSelected(mockedField: Spinner, methodName: String) {
        verify(mockedWidgets).setOnItemSelectedListener(eq(mockedField), captorOnItemSelected.capture())
        assertThat(captorOnItemSelected.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnLongClick(mockedField: View, methodName: String) {
        verify(mockedWidgets).setOnLongClickListener(eq(mockedField), captorOnClick.capture())
        assertThat(captorOnClick.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnUpdateCheckbox(mockedField: CheckBox, methodName: String) {
        verify(mockedWidgets).setOnCheckedChangeListener(eq(mockedField), captorOnUpdateCheckBox.capture())
        assertThat(captorOnUpdateCheckBox.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnUpdateText(mockedField: EditText, methodName: String) {
        verify(mockedWidgets).addTextChangedListener(eq(mockedField), captorOnUpdateText.capture())
        assertThat(captorOnUpdateText.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyRunWithWaitDialog() {
        verify(mockedFragments).runWithWait(captorRunWithWaitDialog.capture())
        captorRunWithWaitDialog.value.invoke()
    }

    fun verifyRunWithWaitDialogAndBack() {
        verify(mockedFragments).runWithWaitAndBack(captorRunWithWaitDialog.capture())
        captorRunWithWaitDialog.value.invoke()
    }

    fun verifyStringListAdapter(mockedField: ListView, expectedData: List<String>) {
        verify(mockedWidgets).stringListAdapter(mockedField, mockedView, expectedData)
    }

    fun verifyStringListAdapter(mockedField: Spinner, expectedData: List<String>) {
        verify(mockedWidgets).stringListAdapter(eq(mockedField), eq(mockedView), eq(expectedData))
    }
}
