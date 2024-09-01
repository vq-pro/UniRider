package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.S18_1

@RunWith(MockitoJUnitRunner::class)
class WheelDeleteConfirmationFragmentTest : FragmentTestBase(WheelDeleteConfirmationFragment::class.java) {

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
        doReturn(ID).`when`(mockedSharedPreferences).getLong(PARAMETER_WHEEL_ID, 0)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_delete_confirmation_fragment)

        assertThat(fragment.parmWheelId, equalTo(ID))
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

        verifyFieldAssignment(R.id.button_delete_cancel, fragment.buttonDeleteCancel, mockedButtonDeleteCancel)
        verifyFieldAssignment(R.id.button_delete_confirmation, fragment.buttonDeleteConfirmation, mockedButtonDeleteConfirmation)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)

        verify(fragment.textName).text = NAME

        verifyOnClick(mockedButtonDeleteConfirmation, "onDelete")
        verifyOnClick(mockedButtonDeleteCancel, "onCancel")
    }

    @Test
    fun onViewCreated_whenWheelIsntFound() {
        // Given
        given(mockedDb.getWheel(anyLong())).willReturn(null)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedFragments).navigateBack()
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
        verify(mockedFragments).navigateBack(3)
    }
}