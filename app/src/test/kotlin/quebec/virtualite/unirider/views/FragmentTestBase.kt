package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import com.google.android.material.switchmaterial.SwitchMaterial
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains.containsString
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.lenient
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.commons.android.views.CommonFragmentServices
import quebec.virtualite.commons.android.views.CommonWidgetServices
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.BluetoothServices
import quebec.virtualite.unirider.bluetooth.WheelInfo
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.services.ExternalServices
import quebec.virtualite.unirider.test.domain.TestConstants.ITEM_SOLD
import quebec.virtualite.unirider.test.domain.TestConstants.LABEL_KM

open class FragmentTestBase(fragmentType: Class<*>) {

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

    @Suppress("UNCHECKED_CAST")
    fun mockExternal() {
        lenient().doReturn(mockedConnector)
            .`when`(mockedExternal).bluetooth()

        lenient().doReturn(mockedDb)
            .`when`(mockedExternal).db()

        lenient().doAnswer { (it.arguments[0] as (WheelDb) -> Unit).invoke(mockedDb) }
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
        lenient().`when`(mockedFragments.string(R.string.label_km))
            .thenReturn(LABEL_KM)
        lenient().`when`(mockedFragments.string(R.string.label_wheel_sold))
            .thenReturn(ITEM_SOLD)
    }

    fun verifyConnectorGetDeviceInfo(expectedDeviceAddress: String, wheelInfo: WheelInfo) {
        argumentCaptor<(WheelInfo) -> Unit>().apply {
            verify(mockedConnector).getDeviceInfo(eq(expectedDeviceAddress), capture())
            firstValue.invoke(wheelInfo)
        }
    }

    fun verifyConnectorScanWith(device: BluetoothDevice) {
        argumentCaptor<(BluetoothDevice) -> Unit>().apply {
            verify(mockedConnector).scan(capture())
            firstValue.invoke(device)
        }
    }

    fun verifyDoneWaiting(connectionPayload: Any) {
        argumentCaptor<() -> Unit>().apply {
            verify(mockedFragments).doneWaiting(eq(connectionPayload), capture())
            firstValue.invoke()
        }
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
        argumentCaptor<(View, T) -> Unit>().apply {
            verify(mockedWidgets).multifieldListAdapter(
                eq(mockedField),
                eq(mockedView),
                eq(expectedId),
                eq(expectedData),
                capture()
            )
            assertThat(firstValue.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
        }
    }

    fun verifyOnClick(mockedField: View, methodName: String) {
        argumentCaptor<(View) -> Unit>().apply {
            verify(mockedWidgets).setOnClickListener(eq(mockedField), capture())
            assertThat(firstValue.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
        }
    }

    fun verifyOnItemClick(mockedField: ListView, methodName: String) {
        argumentCaptor<(View, Int) -> Unit>().apply {
            verify(mockedWidgets).setOnItemClickListener(eq(mockedField), capture())
            assertThat(firstValue.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
        }
    }

    fun verifyOnItemSelected(mockedField: Spinner, methodName: String) {
        argumentCaptor<(View, Int, String) -> Unit>().apply {
            verify(mockedWidgets).setOnItemSelectedListener(eq(mockedField), capture())
            assertThat(firstValue.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
        }
    }

    fun verifyOnLongClick(mockedField: View, methodName: String) {
        argumentCaptor<(View) -> Unit>().apply {
            verify(mockedWidgets).setOnLongClickListener(eq(mockedField), capture())
            assertThat(firstValue.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
        }
    }

    fun verifyOnToggleSwitch(mockedField: SwitchMaterial, methodName: String) {
        argumentCaptor<(Boolean) -> Unit>().apply {
            verify(mockedWidgets).setOnCheckedChangeListener(eq(mockedField), capture())
            assertThat(firstValue.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
        }
    }

    fun verifyOnUpdateText(mockedField: EditText, methodName: String) {
        argumentCaptor<(String) -> Unit>().apply {
            verify(mockedWidgets).addTextChangedListener(eq(mockedField), capture())
            assertThat(firstValue.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
        }
    }

    fun verifyRunWithWaitDialog() {
        argumentCaptor<() -> Unit>().apply {
            verify(mockedFragments).runWithWait(capture())
            firstValue.invoke()
        }
    }

    fun verifyRunWithWaitDialogAndBack() {
        argumentCaptor<() -> Unit>().apply {
            verify(mockedFragments).runWithWaitAndBack(capture())
            firstValue.invoke()
        }
    }

    fun verifyStringListAdapter(mockedField: ListView, expectedData: List<String>) {
        verify(mockedWidgets).stringListAdapter(mockedField, mockedView, expectedData)
    }

    fun verifyStringListAdapter(mockedField: Spinner, expectedData: List<String>) {
        verify(mockedWidgets).stringListAdapter(mockedField, mockedView, expectedData)
    }
}
