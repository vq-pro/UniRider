package quebec.virtualite.unirider.views

import android.widget.Button
import android.widget.TextView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1_CONNECTED
import quebec.virtualite.unirider.test.domain.TestConstants.S18_1_DISCONNECTED
import quebec.virtualite.unirider.views.BaseFragment.Companion.wheel

@RunWith(MockitoJUnitRunner::class)
class WheelConfirmationDisconnectFragmentTest : FragmentTestBase(WheelConfirmationDisconnectFragment::class.java) {

    @InjectMocks
    private lateinit var fragment: WheelConfirmationDisconnectFragment

    @Mock
    private lateinit var mockedButtonDisconnectConfirmation: Button

    @Mock
    private lateinit var mockedButtonDisconnectCancel: Button

    @Mock
    private lateinit var mockedTextName: TextView

    @Before
    fun before() {
        wheel = S18_1_CONNECTED

        mockField(R.id.button_disconnect_confirmation, mockedButtonDisconnectConfirmation)
        mockField(R.id.button_disconnect_cancel, mockedButtonDisconnectCancel)
        mockField(R.id.view_name, mockedTextName)

        mockExternal()
        mockFragments()
    }

    @Test
    fun onCreateView() {
        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_confirmation_disconnect_fragment)
    }

    @Test
    fun onViewCreated() {
        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verifyFieldAssignment(R.id.button_disconnect_cancel, fragment.buttonDisconnectCancel, mockedButtonDisconnectCancel)
        verifyFieldAssignment(R.id.button_disconnect_confirmation, fragment.buttonDisconnectConfirmation, mockedButtonDisconnectConfirmation)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)

        verify(fragment.textName).text = wheel!!.name

        verifyOnClick(mockedButtonDisconnectCancel, "onCancel")
        verifyOnClick(mockedButtonDisconnectConfirmation, "onDisconnect")
    }

    @Test
    fun onCancel() {
        // When
        fragment.onCancel().invoke(mockedView)

        // Then
        verify(mockedFragments).navigateBack()
    }

    @Test
    fun onDisconnect() {
        // Given
        wheel = S18_1_CONNECTED

        // When
        fragment.onDisconnect().invoke(mockedView)

        // Then
        verify(mockedDb).saveWheel(S18_1_DISCONNECTED)
        verify(mockedFragments).navigateBack()
    }
}