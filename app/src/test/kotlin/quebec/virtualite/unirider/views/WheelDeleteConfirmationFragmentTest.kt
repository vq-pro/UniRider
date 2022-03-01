package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.exceptions.WheelNotFoundException

@RunWith(MockitoJUnitRunner::class)
class WheelDeleteConfirmationFragmentTest : BaseFragmentTest(WheelDeleteConfirmationFragment::class.java) {

    @InjectMocks
    lateinit var fragment: WheelDeleteConfirmationFragment

    @Mock
    lateinit var mockedButtonDeleteConfirmation: Button

    @Mock
    lateinit var mockedButtonDeleteCancel: Button

    @Mock
    lateinit var mockedTextName: TextView

    @Before
    fun before() {
        fragment.parmWheelId = ID

        mockField(R.id.button_delete_confirmation, mockedButtonDeleteConfirmation)
        mockField(R.id.button_delete_cancel, mockedButtonDeleteCancel)
        mockField(R.id.view_name, mockedTextName)

        mockExternal()
        mockFragments()
    }

    @Test
    fun onCreateView() {
        // Given
        mockArgument(fragment, WheelViewFragment.PARAMETER_WHEEL_ID, ID)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_delete_confirmation_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        val wheel = S18_1
        given(mockedDb.getWheel(ID))
            .willReturn(wheel)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.wheel, equalTo(wheel))

        assertThat(fragment.textName, equalTo(mockedTextName))
        assertThat(fragment.buttonDeleteConfirmation, equalTo(mockedButtonDeleteConfirmation))
        assertThat(fragment.buttonDeleteCancel, equalTo(mockedButtonDeleteCancel))

        verify(fragment.textName).text = NAME

        verifyOnClick(mockedButtonDeleteConfirmation, "onDelete")
        verifyOnClick(mockedButtonDeleteCancel, "onCancel")
    }

    @Test
    fun onViewCreated_whenWheelIsntFound() {
        // Given
        given(mockedDb.getWheel(ID))
            .willReturn(null)

        // When
        val result = { fragment.onViewCreated(mockedView, mockedBundle) }

        // Then
        Assert.assertThrows(WheelNotFoundException::class.java, result)
    }

    @Test
    fun onCancel() {
        // When
        fragment.onCancel().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateBack()
    }

    @Test
    fun onDelete() {
        // Given
        fragment.wheel = S18_1

        // When
        fragment.onDelete().invoke(mockedView)

        // Then
        verify(mockedDb).deleteWheel(ID)
        verify(mockedFragments).navigateBack(2)
    }
}