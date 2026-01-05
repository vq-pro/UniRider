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
import quebec.virtualite.unirider.test.domain.TestConstants.CHARGE_RATE
import quebec.virtualite.unirider.test.domain.TestConstants.CHARGE_RATE2
import quebec.virtualite.unirider.test.domain.TestConstants.DEVICE_ADDR
import quebec.virtualite.unirider.test.domain.TestConstants.DEVICE_ADDR2
import quebec.virtualite.unirider.test.domain.TestConstants.DEVICE_NAME
import quebec.virtualite.unirider.test.domain.TestConstants.DEVICE_NAME2
import quebec.virtualite.unirider.test.domain.TestConstants.DISTANCE_OFFSET
import quebec.virtualite.unirider.test.domain.TestConstants.DISTANCE_OFFSET2
import quebec.virtualite.unirider.test.domain.TestConstants.ID
import quebec.virtualite.unirider.test.domain.TestConstants.ID2
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE
import quebec.virtualite.unirider.test.domain.TestConstants.MILEAGE2
import quebec.virtualite.unirider.test.domain.TestConstants.NAME
import quebec.virtualite.unirider.test.domain.TestConstants.NAME2
import quebec.virtualite.unirider.test.domain.TestConstants.NOT_SOLD
import quebec.virtualite.unirider.test.domain.TestConstants.PREMILEAGE
import quebec.virtualite.unirider.test.domain.TestConstants.S20_2
import quebec.virtualite.unirider.test.domain.TestConstants.SHERMAN_MAX_3_SOLD
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_FULL
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_FULL2
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MAX
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MAX2
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MIN
import quebec.virtualite.unirider.test.domain.TestConstants.VOLTAGE_MIN2
import quebec.virtualite.unirider.test.domain.TestConstants.WH
import quebec.virtualite.unirider.test.domain.TestConstants.WH2

@RunWith(MockitoJUnitRunner::class)
class WheelDbImplTest {

    @Mock
    lateinit var mockedDb: WheelDatabase

    @Mock
    lateinit var mockedDao: WheelDao

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
        given(mockedDao.findWheel(NAME)).willReturn(
            WheelEntity(
                ID2, NAME, DEVICE_NAME, DEVICE_ADDR,
                PREMILEAGE, MILEAGE, WH,
                VOLTAGE_MAX, VOLTAGE_MIN,
                CHARGE_RATE, VOLTAGE_FULL, DISTANCE_OFFSET, NOT_SOLD
            )
        )

        // When
        val result = dbImpl.findDuplicate(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, false))

        // Then
        verify(mockedDao).findWheel(NAME)

        assertThat(result, equalTo(true))
    }

    @Test
    fun findDuplicate_whenFoundWithSameId_false() {
        // Given
        given(mockedDao.findWheel(NAME)).willReturn(
            WheelEntity(
                ID, NAME, DEVICE_NAME, DEVICE_ADDR,
                PREMILEAGE, MILEAGE, WH,
                VOLTAGE_MAX, VOLTAGE_MIN,
                CHARGE_RATE, VOLTAGE_FULL, DISTANCE_OFFSET, NOT_SOLD
            )
        )

        // When
        val result = dbImpl.findDuplicate(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, false))

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
        val result = dbImpl.findDuplicate(WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, false))

        // Then
        assertThat(result, equalTo(false))
    }

    @Test
    fun findWheel() {
        // Given
        val wheel = WheelEntity(0, NAME, DEVICE_NAME, DEVICE_ADDR, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, false)
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
        val wheel = WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, false)
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
        val wheels = listOf(SHERMAN_MAX_3_SOLD, S20_2)

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
        val existingWheel = WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, WH, 0f, 0f, 0f, 0f, 0f, false)

        // When
        dbImpl.saveWheel(existingWheel)

        // Then
        verify(mockedDao).updateWheel(existingWheel)
    }

    @Test
    fun saveWheel_whenNew_insert() {
        // Given
        val newWheel = WheelEntity(0, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, WH, 0f, 0f, 0f, 0f, 0f, false)

        // When
        dbImpl.saveWheel(newWheel)

        // Then
        verify(mockedDao).insertWheel(newWheel)
    }

    @Test
    fun saveWheels() {
        // Given
        val wheel1 =
            WheelEntity(
                0, NAME, DEVICE_NAME, DEVICE_ADDR,
                PREMILEAGE, MILEAGE, WH,
                VOLTAGE_MAX, VOLTAGE_MIN,
                CHARGE_RATE, VOLTAGE_FULL, DISTANCE_OFFSET, NOT_SOLD
            )
        val wheel2 =
            WheelEntity(
                0, NAME2, DEVICE_NAME2, DEVICE_ADDR2,
                PREMILEAGE, MILEAGE2, WH2,
                VOLTAGE_MAX2, VOLTAGE_MIN2,
                CHARGE_RATE2, VOLTAGE_FULL2, DISTANCE_OFFSET2, NOT_SOLD
            )

        // When
        dbImpl.saveWheels(listOf(wheel1, wheel2))

        // Then
        verify(mockedDao).insertWheel(wheel1)
        verify(mockedDao).insertWheel(wheel2)
    }
}
