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
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import quebec.virtualite.commons.android.views.WidgetUtils
import quebec.virtualite.commons.views.NavigatedTo
import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.bluetooth.DeviceInfo
import quebec.virtualite.unirider.bluetooth.WheelScanner
import quebec.virtualite.unirider.database.WheelDb

open class BaseFragmentTest(fragmentType: Class<*>) {

    internal val fragmentClass: String = fragmentType.simpleName

    internal val DONT_ATTACH_TO_ROOT = false
    internal val SAVED_INSTANCE_STATE: Bundle? = null

    @Mock
    lateinit var mockedBundle: Bundle

    @Mock
    lateinit var mockedContainer: ViewGroup

    @Mock
    lateinit var mockedDb: WheelDb

    @Mock
    lateinit var mockedInflater: LayoutInflater

    @Mock
    lateinit var mockedScanner: WheelScanner

    @Mock
    lateinit var mockedView: View

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @Captor
    lateinit var lambdaFoundDevice: ArgumentCaptor<(Device) -> Unit>

    @Captor
    lateinit var lambdaGotDeviceInfo: ArgumentCaptor<(DeviceInfo) -> Unit>

    @Captor
    private lateinit var lambdaOnClick: ArgumentCaptor<(View) -> Unit>

    @Captor
    private lateinit var lambdaOnDisplay: ArgumentCaptor<(View, Any) -> Unit>

    @Captor
    private lateinit var lambdaOnItemClick: ArgumentCaptor<(View, Int) -> Unit>

    @Captor
    private lateinit var lambdaOnUpdateText: ArgumentCaptor<(String) -> Unit>

    private var navigatedBack: Int = 0
    private var navigatedTo: NavigatedTo? = null

    fun connectDb(fragment: BaseFragment, function: () -> Unit) {
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

    fun navigateBack(nb: Int) {
        navigatedBack += nb
    }

    fun navigateTo(id: Int, parms: Pair<String, Any>) {
        navigatedTo = NavigatedTo(id, parms)
    }

    fun runDb(function: () -> Unit) {
        function()
    }

    fun subThread(function: () -> Unit) {
        function()
    }

    fun uiThread(function: () -> Unit) {
        function()
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

    fun verifyNavigatedBack(nb: Int = 1) {
        assertThat("Didn't navigate back $nb times as expected", navigatedBack, equalTo(nb))
    }

    fun verifyNavigatedTo(id: Int, param: Pair<String, Any>) {
        assertThat("Didn't navigate to where we expected to", navigatedTo, equalTo(NavigatedTo(id, param)))
    }
}
