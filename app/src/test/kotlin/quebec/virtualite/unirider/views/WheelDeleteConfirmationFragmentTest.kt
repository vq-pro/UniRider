package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.test.domain.TestConstants.SHERMAN_MAX_3

@RunWith(MockitoJUnitRunner::class)
class WheelDeleteConfirmationFragmentTest : FragmentTestBase(WheelDeleteConfirmationFragment::class.java) {

    @InjectMocks
    private lateinit var fragment: WheelDeleteConfirmationFragment

    @Mock
    private lateinit var mockedButtonDeleteConfirmation: Button

    @Mock
    private lateinit var mockedButtonDeleteCancel: Button

    @Mock
    private lateinit var mockedTextName: TextView

    @Before
    fun before() {
        BaseFragment.wheel = SHERMAN_MAX_3

        mockField(R.id.button_delete_confirmation, mockedButtonDeleteConfirmation)
        mockField(R.id.button_delete_cancel, mockedButtonDeleteCancel)
        mockField(R.id.view_name, mockedTextName)

        mockExternal()
        mockFragments()
    }

    @Test
    fun onCreateView() {
        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_delete_confirmation_fragment)
    }

    @Test
    fun onViewCreated() {
        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyFieldAssignment(R.id.button_delete_cancel, fragment.buttonDeleteCancel, mockedButtonDeleteCancel)
        verifyFieldAssignment(R.id.button_delete_confirmation, fragment.buttonDeleteConfirmation, mockedButtonDeleteConfirmation)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)

        verify(fragment.textName).text = BaseFragment.wheel!!.name

        verifyOnClick(mockedButtonDeleteConfirmation, "onDelete")
        verifyOnClick(mockedButtonDeleteCancel, "onCancel")
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
        val expectedIdToDelete = BaseFragment.wheel!!.id

        // When
        fragment.onDelete().invoke(mockedView)

        // Then
        verify(mockedDb).deleteWheel(expectedIdToDelete)
        verify(mockedFragments).navigateBack(3)

        assertThat(BaseFragment.wheel, equalTo(null))
    }
}