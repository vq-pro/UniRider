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

private const val MILEAGE1 = 111
private const val MILEAGE2 = 222
private const val ID = 333L
private const val NAME1 = "name1"
private const val NAME2 = "name2"
private const val VMAX1 = 100.8f
private const val VMIN1 = 75.6f
private const val VMAX2 = 84.0f
private const val VMIN2 = 60.0f

private val EXISTING_WHEEL = WheelEntity(ID, NAME1, MILEAGE1, VMIN1, VMAX1)
private val NEW_WHEEL = WheelEntity(0, NAME1, MILEAGE1, VMIN1, VMAX1)
private val NEW_WHEEL2 = WheelEntity(0, NAME2, MILEAGE2, VMIN2, VMAX2)

@RunWith(MockitoJUnitRunner::class)
class WheelDbImplTest {
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
    fun getWheelList() {
        // Given
        val wheels = listOf(
            WheelEntity(0, NAME1, MILEAGE1, VMIN1, VMAX1),
            WheelEntity(0, NAME2, MILEAGE2, VMIN2, VMAX2)
        )

        given(mockedDao.getAllWheels())
            .willReturn(wheels)

        // When
        val results = dbImpl.getWheelList()

        // Then
        verify(mockedDao).getAllWheels()

        assertThat(results, equalTo(wheels))
    }

    @Test
    fun saveWheels_whenExisting_update() {
        // When
        dbImpl.saveWheels(listOf(EXISTING_WHEEL))

        // Then
        verify(mockedDao).updateWheel(EXISTING_WHEEL)
    }

    @Test
    fun saveWheels_whenNew_insert() {
        // When
        dbImpl.saveWheels(listOf(NEW_WHEEL, NEW_WHEEL2))

        // Then
        verify(mockedDao).insertWheel(NEW_WHEEL)
        verify(mockedDao).insertWheel(NEW_WHEEL2)
    }
}
