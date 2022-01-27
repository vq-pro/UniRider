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

private const val DISTANCE1 = 123
private const val DISTANCE2 = 456
private const val NAME1 = "name1"
private const val NAME2 = "name2"
private const val VMAX1 = 100.8f
private const val VMIN1 = 75.6f
private const val VMAX2 = 84.0f
private const val VMIN2 = 60.0f

private val WHEEL1 = WheelEntity(0, NAME1, DISTANCE1, VMAX1, VMIN1)
private val WHEEL2 = WheelEntity(0, NAME2, DISTANCE2, VMAX2, VMIN2)

@RunWith(MockitoJUnitRunner::class)
class WheelDbImplTest {
    @Mock
    lateinit var mockedDb: WheelDatabase

    @Mock
    lateinit var mockedDao: WheelDistanceDao

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
    fun getWheelList() {
        // Given
        val wheels = listOf(
            WheelEntity(0, NAME1, DISTANCE1, VMAX1, VMIN1),
            WheelEntity(0, NAME2, DISTANCE2, VMAX2, VMIN2)
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
    fun saveWheels() {
        // When
        dbImpl.saveWheels(listOf(WHEEL1, WHEEL2))

        // Then
        verify(mockedDao).saveWheel(WHEEL1)
        verify(mockedDao).saveWheel(WHEEL2)
    }
}