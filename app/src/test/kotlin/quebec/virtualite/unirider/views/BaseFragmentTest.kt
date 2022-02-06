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
import quebec.virtualite.unirider.database.WheelDb

open class BaseFragmentTest(val fragmentClass: String) {

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
    lateinit var mockedView: View

    @Mock
    lateinit var mockedWidgets: WidgetUtils

    @Captor
    private lateinit var lambdaOnClick: ArgumentCaptor<(View) -> Unit>

    @Captor
    private lateinit var lambdaOnDisplay: ArgumentCaptor<(View, Any) -> Unit>

    @Captor
    private lateinit var lambdaOnSelect: ArgumentCaptor<(View, Int) -> Unit>

    @Captor
    private lateinit var lambdaOnUpdateText: ArgumentCaptor<(String) -> Unit>

    private var navigatedBack = false
    private var navigatedTo: NavigatedTo? = null

    fun connectDb(fragment: BaseFragment, function: () -> Unit) {
        fragment.db = mockedDb
        function()
    }

    fun mockArgument(fragment: BaseFragment, param: String, value: Long) {
        mockArgument(fragment, param, value.toString())
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

    fun navigateBack() {
        navigatedBack = true
    }

    fun navigateTo(id: Int, parms: Pair<String, String>) {
        navigatedTo = NavigatedTo(id, parms)
    }

    fun runDb(function: () -> Unit) {
        function()
    }

    fun verifyInflate(expectedId: Int) {
        verify(mockedInflater).inflate(expectedId, mockedContainer, DONT_ATTACH_TO_ROOT)
    }

    fun verifyOnClick(mockedField: View, methodName: String) {
        verify(mockedWidgets).setOnClickListener(eq(mockedField), lambdaOnClick.capture())
        assertThat(lambdaOnClick.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnSelectItem(mockedField: ListView, methodName: String) {
        verify(mockedWidgets).setOnItemClickListener(eq(mockedField), lambdaOnSelect.capture())
        assertThat(lambdaOnSelect.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
    }

    fun verifyOnUpdateText(mockedField: EditText, methodName: String) {
        verify(mockedWidgets).addTextChangedListener(eq(mockedField), lambdaOnUpdateText.capture())
        assertThat(lambdaOnUpdateText.value.javaClass.name, containsString("$fragmentClass\$$methodName\$"))
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

    fun verifyNavigatedBack() {
        assertThat(navigatedBack, equalTo(true))
    }

    fun verifyNavigatedTo(id: Int, param: Pair<String, String>) {
        assertThat(navigatedTo, equalTo(NavigatedTo(id, param)))
    }
}
