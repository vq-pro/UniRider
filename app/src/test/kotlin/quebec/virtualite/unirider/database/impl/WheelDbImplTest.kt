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

    private val MILEAGE1 = 111
    private val MILEAGE2 = 222
    private val ID = 333L
    private val NAME1 = "name1"
    private val NAME2 = "name2"
    private val VMAX1 = 100.8f
    private val VMIN1 = 75.6f
    private val VMAX2 = 84.0f
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
    fun findWheel() {
        // Given
        val wheel = WheelEntity(0, NAME1, 0, 0f, 0f)
        given(mockedDao.findWheel(NAME1))
            .willReturn(wheel)

        // When
        val result = dbImpl.findWheel(NAME1)

        // Then
        verify(mockedDao).findWheel(NAME1)

        assertThat(result, equalTo(wheel))
    }

    @Test
    fun getWheel() {
        // Given
        val wheel = WheelEntity(ID, NAME1, 0, 0f, 0f)
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
            WheelEntity(0, NAME1, MILEAGE1, VMIN1, VMAX1),
            WheelEntity(0, NAME2, MILEAGE2, VMIN2, VMAX2)
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
        val existingWheel = WheelEntity(ID, NAME1, MILEAGE1, 0f, 0f)

        // When
        dbImpl.saveWheel(existingWheel)

        // Then
        verify(mockedDao).updateWheel(existingWheel)
    }

    @Test
    fun saveWheel_whenNew_insert() {
        // Given
        val newWheel = WheelEntity(0, NAME1, MILEAGE1, 0f, 0f)

        // When
        dbImpl.saveWheel(newWheel)

        // Then
        verify(mockedDao).insertWheel(newWheel)
    }

    @Test
    fun saveWheels() {
        // Given
        val wheel1 = WheelEntity(0, NAME1, MILEAGE1, VMIN1, VMAX1)
        val wheel2 = WheelEntity(0, NAME2, MILEAGE2, VMIN2, VMAX2)

        // When
        dbImpl.saveWheels(listOf(wheel1, wheel2))

        // Then
        verify(mockedDao).insertWheel(wheel1)
        verify(mockedDao).insertWheel(wheel2)
    }
}
