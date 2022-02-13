package quebec.virtualite.unirider.database.impl

import android.content.Context
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import quebec.virtualite.unirider.database.WheelEntity

@RunWith(MockitoJUnitRunner::class)
class WheelDbImplTest {

    private val BT_NAME = "btName"
    private val BT_NAME2 = "btName2"
    private val ID = 111L
    private val ID2 = 222L
    private val MILEAGE = 333
    private val MILEAGE2 = 444
    private val NAME = "name"
    private val NAME2 = "name2"
    private val VMAX = 100.8f
    private val VMAX2 = 84.0f
    private val VMIN = 75.6f
    private val VMIN2 = 60.0f

    @Mock
    lateinit var mockedDb: WheelDatabase

    @Mock
    lateinit var mockedDao: WheelMileageDao

    @InjectMocks
    var dbImpl = WheelDbImpl(mock(Context::class.java))

    @Before
    fun init() {
        dbImpl.db = mockedDb
        dbImpl.dao = mockedDao
    }

    @Test
    fun deleteAll() {
        // When
        dbImpl.deleteAll()

        // Then
        verify(mockedDb).clearAllTables()
    }

    @Test
    fun deleteWheel() {
        // When
        dbImpl.deleteWheel(ID)

        // Then
        verify(mockedDao).deleteWheel(ID)
    }

    @Test
    fun findDuplicate_whenFoundWithDifferentId_true() {
        // Given
        given(mockedDao.findWheel(NAME))
            .willReturn(WheelEntity(ID2, NAME, BT_NAME, MILEAGE, VMIN, VMAX))

        // When
        val result = dbImpl.findDuplicate(WheelEntity(ID, NAME, BT_NAME, 0, 0f, 0f))

        // Then
        verify(mockedDao).findWheel(NAME)

        assertThat(result, equalTo(true))
    }

    @Test
    fun findDuplicate_whenFoundWithSameId_false() {
        // Given
        given(mockedDao.findWheel(NAME))
            .willReturn(WheelEntity(ID, NAME, BT_NAME, MILEAGE, VMIN, VMAX))

        // When
        val result = dbImpl.findDuplicate(WheelEntity(ID, NAME, BT_NAME, 0, 0f, 0f))

        // Then
        verify(mockedDao).findWheel(NAME)

        assertThat(result, equalTo(false))
    }

    @Test
    fun findDuplicate_whenNotFound_false() {
        // Given
        given(mockedDao.findWheel(NAME))
            .willReturn(null)

        // When
        val result = dbImpl.findDuplicate(WheelEntity(ID, NAME, BT_NAME, 0, 0f, 0f))

        // Then
        assertThat(result, equalTo(false))
    }

    @Test
    fun findWheel() {
        // Given
        val wheel = WheelEntity(0, NAME, BT_NAME, 0, 0f, 0f)
        given(mockedDao.findWheel(NAME))
            .willReturn(wheel)

        // When
        val result = dbImpl.findWheel(NAME)

        // Then
        verify(mockedDao).findWheel(NAME)

        assertThat(result, equalTo(wheel))
    }

    @Test
    fun getWheel() {
        // Given
        val wheel = WheelEntity(ID, NAME, BT_NAME, 0, 0f, 0f)
        given(mockedDao.getWheel(ID))
            .willReturn(wheel)

        // When
        val result = dbImpl.getWheel(ID)

        // Then
        verify(mockedDao).getWheel(ID)

        assertThat(result, equalTo(wheel))
    }

    @Test
    fun getWheels() {
        // Given
        val wheels = listOf(
            WheelEntity(ID, NAME, BT_NAME, MILEAGE, VMIN, VMAX),
            WheelEntity(ID2, NAME2, BT_NAME2, MILEAGE2, VMIN2, VMAX2)
        )

        given(mockedDao.getAllWheels())
            .willReturn(wheels)

        // When
        val results = dbImpl.getWheels()

        // Then
        verify(mockedDao).getAllWheels()

        assertThat(results, equalTo(wheels))
    }

    @Test
    fun saveWheel_whenExisting_update() {
        // Given
        val existingWheel = WheelEntity(ID, NAME, BT_NAME, MILEAGE, 0f, 0f)

        // When
        dbImpl.saveWheel(existingWheel)

        // Then
        verify(mockedDao).updateWheel(existingWheel)
    }

    @Test
    fun saveWheel_whenNew_insert() {
        // Given
        val newWheel = WheelEntity(0, NAME, BT_NAME, MILEAGE, 0f, 0f)

        // When
        dbImpl.saveWheel(newWheel)

        // Then
        verify(mockedDao).insertWheel(newWheel)
    }

    @Test
    fun saveWheels() {
        // Given
        val wheel1 = WheelEntity(0, NAME, BT_NAME, MILEAGE, VMIN, VMAX)
        val wheel2 = WheelEntity(0, NAME2, BT_NAME2, MILEAGE2, VMIN2, VMAX2)

        // When
        dbImpl.saveWheels(listOf(wheel1, wheel2))

        // Then
        verify(mockedDao).insertWheel(wheel1)
        verify(mockedDao).insertWheel(wheel2)
    }
}
