package quebec.virtualite.unirider.views

import android.widget.EditText
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.ID
import quebec.virtualite.unirider.TestDomain.KM
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.VOLTAGE_REQUIRED
import quebec.virtualite.unirider.TestDomain.WH_PER_KM
import quebec.virtualite.unirider.services.CalculatorService
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_WHEEL_ID
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_WH_PER_KM

@RunWith(MockitoJUnitRunner::class)
class WheelChargeFragmentTest : BaseFragmentTest(WheelChargeFragment::class.java) {

    @InjectMocks
    lateinit var fragment: WheelChargeFragment

    @Mock
    lateinit var mockedCalculatorService: CalculatorService

    @Mock
    lateinit var mockedEditKm: EditText

    @Mock
    lateinit var mockedTextName: TextView

    @Mock
    lateinit var mockedTextVoltageRequired: TextView

    @Mock
    lateinit var mockedTextWhPerKm: TextView

    @Before
    fun before() {
        fragment.parmWheelId = ID
        fragment.parmWhPerKm = WH_PER_KM
        fragment.wheel = S18_1

        mockExternal()
        mockFields()
        mockFragments()
    }

    @Test
    fun onCreateView() {
        // Given
        mockArgument(fragment, PARAMETER_WHEEL_ID, ID)
        mockArgument(fragment, PARAMETER_WH_PER_KM, WH_PER_KM)

        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.wheel_charge_fragment)

        assertThat(fragment.parmWheelId, equalTo(ID))
        assertThat(fragment.parmWhPerKm, equalTo(WH_PER_KM))
    }

    @Test
    fun onViewCreated() {
        // Given
        fragment.wheel = null
        given(mockedDb.getWheel(anyLong()))
            .willReturn(S18_1)

        // When
        fragment.onViewCreated(mockedView, mockedBundle)

        // Then
        verify(mockedDb).getWheel(ID)

        assertThat(fragment.wheel, equalTo(S18_1))

        verifyFieldAssignment(R.id.edit_km, fragment.editKm, mockedEditKm)
        verifyFieldAssignment(R.id.view_name, fragment.textName, mockedTextName)
        verifyFieldAssignment(R.id.view_required_voltage, fragment.textVoltageRequired, mockedTextVoltageRequired)
        verifyFieldAssignment(R.id.view_wh_per_km, fragment.textWhPerKm, mockedTextWhPerKm)

        verify(mockedTextName).text = NAME
        verify(mockedTextWhPerKm).text = "$WH_PER_KM"

        verifyOnUpdateText(mockedEditKm, "onUpdateKm")
    }

    @Test
    fun onUpdateKm() {
        // Given
        injectMocks()

        given(mockedCalculatorService.requiredVoltage(any(), anyFloat(), anyFloat()))
            .willReturn(VOLTAGE_REQUIRED)

        // When
        fragment.onUpdateKm().invoke("$KM ")

        // Then
        verify(mockedCalculatorService).requiredVoltage(fragment.wheel, WH_PER_KM, KM)

        verify(mockedTextVoltageRequired).text = "$VOLTAGE_REQUIRED"
    }

    private fun injectMocks() {
        fragment.textVoltageRequired = mockedTextVoltageRequired
    }

    private fun mockFields() {
        mockField(R.id.edit_km, mockedEditKm)
        mockField(R.id.view_name, mockedTextName)
        mockField(R.id.view_required_voltage, mockedTextVoltageRequired)
        mockField(R.id.view_wh_per_km, mockedTextWhPerKm)
    }
}
