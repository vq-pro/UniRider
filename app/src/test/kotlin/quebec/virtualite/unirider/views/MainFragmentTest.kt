package quebec.virtualite.unirider.views

import android.widget.ListView
import android.widget.TextView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.commons.android.utils.ArrayListUtils.setList
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.ABRAMS_4
import quebec.virtualite.unirider.TestDomain.ID2
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE2
import quebec.virtualite.unirider.TestDomain.MILEAGE3
import quebec.virtualite.unirider.TestDomain.MILEAGE4
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.NAME_DELETED
import quebec.virtualite.unirider.TestDomain.NAME_NEW
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.PREMILEAGE2
import quebec.virtualite.unirider.TestDomain.PREMILEAGE3
import quebec.virtualite.unirider.TestDomain.PREMILEAGE4
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.S20_2
import quebec.virtualite.unirider.TestDomain.SHERMAN_MAX_3
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_NEW
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_S18_1_123
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_S20_2_123
import quebec.virtualite.unirider.views.BaseFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class MainFragmentTest : BaseFragmentTest(MainFragment::class.java) {

    @InjectMocks
    lateinit var fragment: MainFragment

    @Mock
    lateinit var mockedLVWheels: ListView

    @Mock
    lateinit var mockedTextMileage: TextView

    @Mock
    lateinit var mockedTextName: TextView

    @Mock
    lateinit var mockedTextTotalMileage: TextView

    @Before
    fun before() {
        mockExternal()
        mockFragments()
    }

    @Test
    fun onCreateView() {
        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.main_fragment)
    }

    @Test
    fun onViewCreated_withDeletedWheels() {
        // Given
        given(mockedDb.getWheels())
            .willReturn(
                listOf(
                    SHERMAN_MAX_3,
                    S20_2,
                    ABRAMS_4,
                    S18_1,
                )
            )

        mockField(R.id.wheels, mockedLVWheels)
        mockField(R.id.total_mileage, mockedTextTotalMileage)

        setList(fragment.wheelList, listOf(WheelRow(999, "some previous content", 999)))

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        val expectedEntries = listOf(
            WHEEL_ROW_S18_1_123,
            WHEEL_ROW_S20_2_123,
            WheelRow(0, NAME_DELETED, PREMILEAGE3 + MILEAGE3 + PREMILEAGE4 + MILEAGE4),
            WHEEL_ROW_NEW
        )

        verifyFieldAssignment(R.id.wheels, fragment.lvWheels, mockedLVWheels)
        verifyFieldAssignment(R.id.total_mileage, fragment.textTotalMileage, mockedTextTotalMileage)

        verify(mockedWidgets).enable(mockedLVWheels)
        verifyMultiFieldListAdapter(mockedLVWheels, R.layout.wheels_item, expectedEntries, "onDisplayWheel")
        verifyOnItemClick(mockedLVWheels, "onSelectWheel")

        verify(mockedDb).getWheels()
        verify(mockedTextTotalMileage).text =
            "${PREMILEAGE + PREMILEAGE2 + PREMILEAGE3 + PREMILEAGE4 + MILEAGE + MILEAGE2 + MILEAGE3 + MILEAGE4}"
    }

    @Test
    fun onViewCreated_withoutDeletedWheels() {
        // Given
        given(mockedDb.getWheels())
            .willReturn(
                listOf(
                    S20_2,
                    S18_1,
                )
            )

        mockField(R.id.wheels, mockedLVWheels)
        mockField(R.id.total_mileage, mockedTextTotalMileage)

        setList(fragment.wheelList, listOf(WheelRow(999, "some previous content", 999)))

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        val expectedEntries = listOf(
            WHEEL_ROW_S18_1_123,
            WHEEL_ROW_S20_2_123,
            WHEEL_ROW_NEW
        )

        verifyFieldAssignment(R.id.wheels, fragment.lvWheels, mockedLVWheels)
        verifyFieldAssignment(R.id.total_mileage, fragment.textTotalMileage, mockedTextTotalMileage)

        verify(mockedWidgets).enable(mockedLVWheels)
        verifyMultiFieldListAdapter(mockedLVWheels, R.layout.wheels_item, expectedEntries, "onDisplayWheel")
        verifyOnItemClick(mockedLVWheels, "onSelectWheel")

        verify(mockedDb).getWheels()
        verify(mockedTextTotalMileage).text =
            "${PREMILEAGE + PREMILEAGE2 + MILEAGE + MILEAGE2}"
    }

    @Test
    fun onDisplayItem() {
        // Given
        mockField(R.id.row_name, mockedTextName)
        mockField(R.id.row_mileage, mockedTextMileage)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WHEEL_ROW_S18_1_123)

        // Then
        verify(mockedTextName).text = NAME
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE}"
    }

    @Test
    fun onDisplayItem_newItem_hideMileageZero() {
        // Given
        mockField(R.id.row_name, mockedTextName)
        mockField(R.id.row_mileage, mockedTextMileage)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WHEEL_ROW_NEW)

        // Then
        verify(mockedTextName).text = NAME_NEW
        verify(mockedTextMileage).text = ""
    }

    @Test
    fun onSelectWheel() {
        // Given
        fragment.wheelList += listOf(WHEEL_ROW_S18_1_123, WHEEL_ROW_S20_2_123, WHEEL_ROW_NEW)

        // When
        fragment.onSelectWheel().invoke(mockedView, 1)

        // Then
        verify(mockedFragments).navigateTo(
            R.id.action_MainFragment_to_WheelViewFragment,
            Pair(PARAMETER_WHEEL_ID, ID2)
        )
    }

    @Test
    fun onSelectWheel_withNewEntry_straightToEditInAddMode() {
        // Given
        fragment.wheelList += listOf(WHEEL_ROW_S18_1_123, WHEEL_ROW_S20_2_123, WHEEL_ROW_NEW)

        // When
        fragment.onSelectWheel().invoke(mockedView, 2)

        // Then
        verify(mockedFragments).navigateTo(
            R.id.action_MainFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_ID, 0L)
        )
    }
}
