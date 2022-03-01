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
import quebec.virtualite.unirider.TestDomain.ID2
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE2
import quebec.virtualite.unirider.TestDomain.MILEAGE3
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.NAME_NEW
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.S20_2
import quebec.virtualite.unirider.TestDomain.SHERMAN_3
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_1_123
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_2_456
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_3_123
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_NEW
import quebec.virtualite.unirider.views.WheelViewFragment.Companion.PARAMETER_WHEEL_ID

@RunWith(MockitoJUnitRunner::class)
class MainFragmentTest : BaseFragmentTest(MainFragment::class.java) {

    @InjectMocks
    val fragment: MainFragment = TestableMainFragment(this)

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
        mockExternalServices()
        mockServices()
    }

    @Test
    fun onCreateView() {
        // When
        fragment.onCreateView(mockedInflater, mockedContainer, SAVED_INSTANCE_STATE)

        // Then
        verifyInflate(R.layout.main_fragment)
    }

    @Test
    fun onViewCreated() {
        // Given
        given(mockedDb.getWheels())
            .willReturn(
                listOf(
                    SHERMAN_3,
                    S18_1,
                    S20_2,
                )
            )

        mockField(R.id.wheels, mockedLVWheels)
        mockField(R.id.total_mileage, mockedTextTotalMileage)

        setList(fragment.wheelList, listOf(WheelRow(999, "some previous content", 999)))

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        val expectedEntries = listOf(
            WHEEL_ROW_2_456,
            WHEEL_ROW_3_123,
            WHEEL_ROW_1_123,
            WHEEL_ROW_NEW
        )

        verify(mockedWidgets).enable(mockedLVWheels)
        verifyMultiFieldListAdapter(mockedLVWheels, R.layout.wheels_item, expectedEntries, "onDisplayWheel")
        verifyOnItemClick(mockedLVWheels, "onSelectWheel")

        verify(mockedDb).getWheels()
        verify(mockedTextTotalMileage).text = "${MILEAGE + MILEAGE2 + MILEAGE3}"
    }

    @Test
    fun onDisplayItem() {
        // Given
        mockField(R.id.row_name, mockedTextName)
        mockField(R.id.row_mileage, mockedTextMileage)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WHEEL_ROW_1_123)

        // Then
        verify(mockedTextName).text = NAME
        verify(mockedTextMileage).text = "$MILEAGE"
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
        fragment.wheelList += listOf(WHEEL_ROW_1_123, WHEEL_ROW_2_456, WHEEL_ROW_NEW)

        // When
        fragment.onSelectWheel().invoke(mockedView, 1)

        // Then
        verify(mockedServices).navigateTo(
            R.id.action_MainFragment_to_WheelViewFragment,
            Pair(PARAMETER_WHEEL_ID, ID2)
        )
    }

    @Test
    fun onSelectWheel_withNewEntry_straightToEditInAddMode() {
        // Given
        fragment.wheelList += listOf(WHEEL_ROW_1_123, WHEEL_ROW_2_456, WHEEL_ROW_NEW)

        // When
        fragment.onSelectWheel().invoke(mockedView, 2)

        // Then
        verify(mockedServices).navigateTo(
            R.id.action_MainFragment_to_WheelEditFragment,
            Pair(PARAMETER_WHEEL_ID, 0L)
        )
    }

    class TestableMainFragment(val test: MainFragmentTest) : MainFragment() {

        override fun initDB(function: () -> Unit) {
            test.initDB(this, function)
        }
    }
}
