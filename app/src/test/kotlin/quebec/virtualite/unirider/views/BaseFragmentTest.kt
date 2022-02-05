package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
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

open class BaseFragmentTest {

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

    lateinit var navigatedTo: NavigatedTo

    fun mockArgument(fragment: BaseFragment, param: String, value: String) {
        given(mockedBundle.getString(param))
            .willReturn(value)

        fragment.arguments = mockedBundle
    }

    fun mockField(id: Int, mockedField: View) {
        given<Any>(mockedView.findViewById(id))
            .willReturn(mockedField)
    }

    fun verifyInflate(expectedId: Int) {
        verify(mockedInflater).inflate(expectedId, mockedContainer, DONT_ATTACH_TO_ROOT)
    }

    fun verifyOnClick(mockedField: View, className: String, methodName: String) {
        verify(mockedWidgets).setOnClickListener(eq(mockedField), lambdaOnClick.capture())
        assertThat(lambdaOnClick.value.javaClass.name, containsString("$className\$$methodName\$"))
    }

    fun verifyOnSelectItem(mockedField: ListView, className: String, methodName: String) {
        verify(mockedWidgets).setOnItemClickListener(eq(mockedField), lambdaOnSelect.capture())
        assertThat(lambdaOnSelect.value.javaClass.name, containsString("$className\$$methodName\$"))
    }

    fun verifyOnUpdateText(mockedField: EditText, className: String, methodName: String) {
        verify(mockedWidgets).addTextChangedListener(eq(mockedField), lambdaOnUpdateText.capture())
        assertThat(lambdaOnUpdateText.value.javaClass.name, containsString("$className\$$methodName\$"))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> verifyMultiFieldListAdapter(
        mockedField: ListView, expectedId: Int, expectedData: List<T>, className: String, methodName: String
    ) {
        verify(mockedWidgets).multifieldListAdapter(
            eq(mockedField), eq(mockedView), eq(expectedId), eq(expectedData),
            (lambdaOnDisplay as ArgumentCaptor<(View, T) -> Unit>).capture()
        )
        assertThat(lambdaOnDisplay.value.javaClass.name, containsString("$className\$$methodName\$"))
    }
}
