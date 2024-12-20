package quebec.virtualite.unirider.views

import android.widget.ListView
import android.widget.TextView
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.TestDomain.ABRAMS_4
import quebec.virtualite.unirider.TestDomain.ID2
import quebec.virtualite.unirider.TestDomain.LABEL_KM
import quebec.virtualite.unirider.TestDomain.MILEAGE
import quebec.virtualite.unirider.TestDomain.MILEAGE2
import quebec.virtualite.unirider.TestDomain.MILEAGE3
import quebec.virtualite.unirider.TestDomain.MILEAGE4
import quebec.virtualite.unirider.TestDomain.NAME
import quebec.virtualite.unirider.TestDomain.NAME_NEW
import quebec.virtualite.unirider.TestDomain.NAME_SOLD
import quebec.virtualite.unirider.TestDomain.PREMILEAGE
import quebec.virtualite.unirider.TestDomain.PREMILEAGE2
import quebec.virtualite.unirider.TestDomain.PREMILEAGE3
import quebec.virtualite.unirider.TestDomain.PREMILEAGE4
import quebec.virtualite.unirider.TestDomain.S18_1
import quebec.virtualite.unirider.TestDomain.S20_2
import quebec.virtualite.unirider.TestDomain.SHERMAN_MAX_3
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_ABRAMS_4
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_NEW
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_S18_1_123
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_S20_2_123
import quebec.virtualite.unirider.TestDomain.WHEEL_ROW_SHERMAN_MAX_3

private const val LIST_ENTRY2 = 1
private const val LIST_ENTRY3 = 2
private const val LIST_ENTRY4 = 3

@RunWith(MockitoJUnitRunner::class)
class MainFragmentTest : FragmentTestBase(MainFragment::class.java) {

    @InjectMocks
    @Spy
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
        mockStrings()

        fragment.labelKm = LABEL_KM
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
        fragment.labelKm = ""

        mockField(R.id.wheels, mockedLVWheels)
        mockField(R.id.total_mileage, mockedTextTotalMileage)

        doNothing().`when`(fragment).showWheels()

        // When
        fragment.onViewCreated(mockedView, SAVED_INSTANCE_STATE)

        // Then
        verifyFieldAssignment(R.id.wheels, fragment.lvWheels, mockedLVWheels)
        verifyFieldAssignment(R.id.total_mileage, fragment.textTotalMileage, mockedTextTotalMileage)

        verify(mockedWidgets).enable(mockedLVWheels)
        verifyMultiFieldListAdapter<WheelRow>(mockedLVWheels, R.layout.wheels_item, "onDisplayWheel")
        verifyOnItemClick(mockedLVWheels, "onSelectWheel")

        verify(fragment).showWheels()

        assertThat(fragment.labelKm, equalTo(LABEL_KM))
    }

    @Test
    fun onDisplayWheel() {
        // Given
        mockField(R.id.row_name, mockedTextName)
        mockField(R.id.row_mileage, mockedTextMileage)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WHEEL_ROW_S18_1_123)

        // Then
        verify(mockedTextName).text = NAME
        verify(mockedTextMileage).text = "${PREMILEAGE + MILEAGE} $LABEL_KM"
    }

    @Test
    fun onDisplayWheel_newItem_hideMileageZero() {
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
    fun onDisplayWheel_soldItemWhenCollapsed_showMileage() {
        // Given
        mockField(R.id.row_name, mockedTextName)
        mockField(R.id.row_mileage, mockedTextMileage)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WheelRow(0, NAME_SOLD, 1))

        // Then
        verify(mockedTextName).text = NAME_SOLD
        verify(mockedTextMileage).text = "1 $LABEL_KM"
    }

    @Test
    fun onDisplayWheel_soldItemWhenOpened_hideMileageZero() {
        // Given
        mockField(R.id.row_name, mockedTextName)
        mockField(R.id.row_mileage, mockedTextMileage)

        // When
        fragment.onDisplayWheel().invoke(mockedView, WheelRow(0, NAME_SOLD, 0))

        // Then
        verify(mockedTextName).text = NAME_SOLD
        verify(mockedTextMileage).text = ""
    }

    @Test
    fun onSelectWheel() {
        // Given
        fragment.wheelList += listOf(WHEEL_ROW_S18_1_123, WHEEL_ROW_S20_2_123, WHEEL_ROW_NEW)

        given(mockedDb.getWheel(ID2)).willReturn(S20_2)

        // When
        fragment.onSelectWheel().invoke(mockedView, LIST_ENTRY2)

        // Then
        verify(mockedDb).getWheel(ID2)
        verify(mockedFragments).navigateTo(R.id.action_MainFragment_to_WheelViewFragment)

        assertThat(BaseFragment.wheel, equalTo(S20_2))
    }

    @Test
    fun onSelectWheel_withNewEntry_straightToEditInAddMode() {
        // Given
        fragment.wheelList += listOf(
            WHEEL_ROW_S18_1_123, WHEEL_ROW_S20_2_123, WheelRow(0, NAME_SOLD, 1), WHEEL_ROW_NEW
        )

        // When
        fragment.onSelectWheel().invoke(mockedView, LIST_ENTRY4)

        // Then
        verify(mockedFragments).navigateTo(R.id.action_MainFragment_to_WheelEditFragment)

        assertThat(BaseFragment.wheel, equalTo(null))
    }

    @Test
    fun onSelectWheel_withSoldEntry_withShowSoldWheelsCollapsed_open() {
        // Given
        fragment.showSoldWheels = false
        fragment.textTotalMileage = mockedTextTotalMileage

        fragment.wheelList += listOf(
            WHEEL_ROW_S18_1_123, WHEEL_ROW_S20_2_123, WheelRow(0, NAME_SOLD, 1), WHEEL_ROW_NEW
        )

        // When
        fragment.onSelectWheel().invoke(mockedView, LIST_ENTRY3)

        // Then
        assertThat(fragment.showSoldWheels, equalTo(true))
        assertThat(BaseFragment.wheel, equalTo(null))
    }

    @Test
    fun onSelectWheel_withSoldEntry_withShowSoldWheelsExpanded_close() {
        // Given
        fragment.showSoldWheels = true
        fragment.textTotalMileage = mockedTextTotalMileage

        fragment.wheelList += listOf(
            WHEEL_ROW_S18_1_123, WHEEL_ROW_S20_2_123, WheelRow(0, NAME_SOLD, 1), WHEEL_ROW_NEW
        )

        // When
        fragment.onSelectWheel().invoke(mockedView, LIST_ENTRY3)

        // Then
        assertThat(fragment.showSoldWheels, equalTo(false))
        assertThat(BaseFragment.wheel, equalTo(null))
    }

    @Test
    fun onSoldWheelClose() {
        // Given
        fragment.showSoldWheels = true
        doNothing().`when`(fragment).showWheels()

        fragment.wheelList += listOf(
            WHEEL_ROW_S18_1_123,
            WHEEL_ROW_S20_2_123,
            WheelRow(0, NAME_SOLD, 0),
            WHEEL_ROW_SHERMAN_MAX_3,
            WHEEL_ROW_NEW
        )

        // When
        fragment.onSelectWheel().invoke(mockedView, 2)

        // Then
        verify(fragment).showWheels()

        assertThat(fragment.showSoldWheels, equalTo(false))
    }

    @Test
    fun onSoldWheelOpen() {
        // Given
        fragment.showSoldWheels = false
        doNothing().`when`(fragment).showWheels()

        fragment.wheelList += listOf(
            WHEEL_ROW_S18_1_123,
            WHEEL_ROW_S20_2_123,
            WheelRow(0, NAME_SOLD, 1),
            WHEEL_ROW_NEW
        )

        // When
        fragment.onSelectWheel().invoke(mockedView, 2)

        // Then
        verify(fragment).showWheels()

        assertThat(fragment.showSoldWheels, equalTo(true))
    }

    @Test
    fun showWheels_whenSoldWheelsCollapsed() {
        // Given
        fragment.showSoldWheels = false
        fragment.textTotalMileage = mockedTextTotalMileage

        given(mockedDb.getWheels())
            .willReturn(
                listOf(
                    ABRAMS_4,
                    S20_2,
                    SHERMAN_MAX_3,
                    S18_1,
                )
            )

        // When
        fragment.showWheels()

        // Then
        verify(mockedDb).getWheels()
        verify(mockedWidgets).setListViewEntries(
            mockedLVWheels,
            fragment.wheelList,
            listOf(
                WHEEL_ROW_S18_1_123,
                WHEEL_ROW_S20_2_123,
                WheelRow(0, NAME_SOLD, PREMILEAGE3 + MILEAGE3 + PREMILEAGE4 + MILEAGE4),
                WHEEL_ROW_NEW
            )
        )
        verify(mockedTextTotalMileage).text =
            "${PREMILEAGE + PREMILEAGE2 + PREMILEAGE3 + PREMILEAGE4 + MILEAGE + MILEAGE2 + MILEAGE3 + MILEAGE4} km"
    }

    @Test
    fun showWheels_whenSoldWheelsExpanded() {
        // Given
        fragment.showSoldWheels = true
        fragment.textTotalMileage = mockedTextTotalMileage

        given(mockedDb.getWheels())
            .willReturn(
                listOf(
                    ABRAMS_4,
                    S20_2,
                    SHERMAN_MAX_3,
                    S18_1,
                )
            )

        // When
        fragment.showWheels()

        // Then
        verify(mockedDb).getWheels()
        verify(mockedWidgets).setListViewEntries(
            mockedLVWheels,
            fragment.wheelList,
            listOf(
                WHEEL_ROW_S18_1_123,
                WHEEL_ROW_S20_2_123,
                WheelRow(0, NAME_SOLD, 0),
                WHEEL_ROW_SHERMAN_MAX_3,
                WHEEL_ROW_ABRAMS_4,
                WHEEL_ROW_NEW
            )
        )
        verify(mockedTextTotalMileage).text =
            "${PREMILEAGE + PREMILEAGE2 + PREMILEAGE3 + PREMILEAGE4 + MILEAGE + MILEAGE2 + MILEAGE3 + MILEAGE4} km"
    }
}
