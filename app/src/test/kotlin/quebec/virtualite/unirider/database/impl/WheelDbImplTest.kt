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
import quebec.virtualite.unirider.database.WheelDistance

private const val DISTANCE1 = 123
private const val DISTANCE2 = 456
private const val ID = 0L
private const val NAME1 = "name1"
private const val NAME2 = "name2"

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
    fun getWheelList() {
        // Given
        given(mockedDao.getAllWheels())
            .willReturn(
                listOf(
                    WheelDistance(ID, NAME1, DISTANCE1),
                    WheelDistance(ID, NAME2, DISTANCE2)
                )
            )

        // When
        val results = dbImpl.getWheelList()

        // Then
        verify(mockedDao).getAllWheels()

        assertThat(results, equalTo(listOf(NAME1, NAME2)))
    }

    @Test
    fun saveWheels() {
        // When
        dbImpl.saveWheels(listOf(NAME1, NAME2))

        // Then
        verify(mockedDao).saveWheel(WheelDistance(0, NAME1, 0))
        verify(mockedDao).saveWheel(WheelDistance(0, NAME2, 0))
    }
}