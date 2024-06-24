package quebec.virtualite.unirider

import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.WheelRow

object TestDomain {
    const val CHARGE_RATE = 4f
    const val CHARGE_RATE2 = 6f
    const val CHARGE_RATE3 = 8f
    const val CHARGE_RATE4 = 10f
    const val CHARGE_RATE_NEW = 10f
    const val DEVICE_ADDR = "AA:BB:CC:DD:EE:FF"
    const val DEVICE_ADDR2 = "BB:CC:DD:EE:FF:GG"
    const val DEVICE_ADDR3 = "CC:DD:EE:FF:GG:HH"
    const val DEVICE_ADDR4 = "DD:EE:FF:GG:HH:II"
    const val DEVICE_NAME = "KS-S18"
    const val DEVICE_NAME2 = "KS-S20"
    const val DEVICE_NAME3 = "LK2000"
    const val DEVICE_NAME4 = "LK2001"
    const val ID = 1111L
    const val ID2 = 1112L
    const val ID3 = 1113L
    const val ID4 = 1114L
    const val ITEM_SOLD = "Sold"
    const val KM = 4444f
    const val KM_NEW = 4445f
    const val KM_NEW_RAW = 4445.012f
    const val MILEAGE = 2222
    const val MILEAGE2 = 2222
    const val MILEAGE3 = 2223
    const val MILEAGE4 = 2224
    const val MILEAGE_NEW = 2224
    const val MILEAGE_NEW_RAW = 2223.617f
    const val NAME = "S18"
    const val NAME2 = "S20"
    const val NAME3 = "Sherman Max"
    const val NAME4 = "Abrams"
    const val NAME_NEW = "<New>"
    const val NAME_SOLD = "<$ITEM_SOLD>"
    const val NOT_SOLD = false
    const val PERCENTAGE = 100.0f
    const val PREMILEAGE = 10000
    const val PREMILEAGE2 = 10000
    const val PREMILEAGE3 = 200
    const val PREMILEAGE4 = 0
    const val PREMILEAGE_NEW = 20000
    const val REMAINING_RANGE = 5555f
    const val REMAINING_RANGE_UP = 5554f
    const val REMAINING_RANGE_ZERO = 0
    const val SOLD = true
    const val TEMPERATURE_NEW_RAW = 3333f
    const val TOTAL_RANGE = 6666f
    const val VOLTAGE = 82f
    const val VOLTAGE_FULL = 81.5f
    const val VOLTAGE_FULL2 = 123f
    const val VOLTAGE_FULL3 = 99.5f
    const val VOLTAGE_FULL4 = 99.5f
    const val VOLTAGE_FULL_NEW = 80.7f
    const val VOLTAGE_MAX = 84.0f
    const val VOLTAGE_MAX2 = 126.0f
    const val VOLTAGE_MAX3 = 100.8f
    const val VOLTAGE_MAX4 = 100.8f
    const val VOLTAGE_MAX_NEW = 100.9f
    const val VOLTAGE_NEW = 84.6f
    const val VOLTAGE_NEW_RAW = 84.56f
    const val VOLTAGE_MIN = 60.0f
    const val VOLTAGE_MIN2 = 90.0f
    const val VOLTAGE_MIN3 = 75.6f
    const val VOLTAGE_MIN4 = 75.6f
    const val VOLTAGE_MIN_NEW = 62.0f
    const val VOLTAGE_REQUIRED = 90.5f
    const val VOLTAGE_RESERVE = 70.0f
    const val VOLTAGE_RESERVE2 = 95.0f
    const val VOLTAGE_RESERVE3 = 80.0f
    const val VOLTAGE_RESERVE4 = 82.0f
    const val VOLTAGE_RESERVE_NEW = 72.6f
    const val VOLTAGE_START = 83.8f
    const val VOLTAGE_START2 = 124.5f
    const val VOLTAGE_START3 = 100.4f
    const val VOLTAGE_START4 = 99.8f
    const val VOLTAGE_START_NEW = 83.1f
    const val WH = 1110
    const val WH2 = 2220
    const val WH3 = 3600
    const val WH4 = 2700
    const val WH_NEW = 9999
    const val WH_PER_KM = 30.3f
    const val WH_PER_KM_INDEX = 3
    const val WH_PER_KM_SMALL_INDEX = 2
    const val WH_PER_KM_SMALL = 18.7f
    const val WH_PER_KM_UP = 35f
    const val WH_PER_KM_UP_INDEX = 4
    val WHS_PER_KM = listOf("20", "25", "30", "30.3", "35", "40", "45")
    val WHS_PER_KM_SMALL = listOf("10", "15", "18.7", "20", "25", "30")
    val WHEEL_ROW_S18_1_123 = WheelRow(ID, NAME, PREMILEAGE + MILEAGE)
    val WHEEL_ROW_S20_2_123 = WheelRow(ID2, NAME2, PREMILEAGE2 + MILEAGE2)
    val WHEEL_ROW_SHERMAN_MAX_3 = WheelRow(ID3, "- $NAME3", PREMILEAGE3 + MILEAGE3)
    val WHEEL_ROW_ABRAMS_4 = WheelRow(ID4, "- $NAME4", PREMILEAGE4 + MILEAGE4)
    val WHEEL_ROW_NEW = WheelRow(0, NAME_NEW, 0)

    val DEVICE = BluetoothDevice(DEVICE_NAME, DEVICE_ADDR)
    val DEVICE2 = BluetoothDevice(DEVICE_NAME2, DEVICE_ADDR2)
    val DEVICE3 = BluetoothDevice(DEVICE_NAME3, DEVICE_ADDR3)

    val S18_1 = WheelEntity(
        ID, NAME, DEVICE_NAME, DEVICE_ADDR,
        PREMILEAGE, MILEAGE, WH,
        VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START,
        CHARGE_RATE, VOLTAGE_FULL, NOT_SOLD
    )
    val S20_2 = WheelEntity(
        ID2, NAME2, DEVICE_NAME2, DEVICE_ADDR2,
        PREMILEAGE2, MILEAGE2, WH2,
        VOLTAGE_MAX2, VOLTAGE_MIN2, VOLTAGE_RESERVE2, VOLTAGE_START2,
        CHARGE_RATE2, VOLTAGE_FULL2, NOT_SOLD
    )
    val SHERMAN_MAX_3 = WheelEntity(
        ID3, NAME3, DEVICE_NAME3, DEVICE_ADDR3,
        PREMILEAGE3, MILEAGE3, WH3,
        VOLTAGE_MAX3, VOLTAGE_MIN3, VOLTAGE_RESERVE3, VOLTAGE_START3,
        CHARGE_RATE3, VOLTAGE_FULL3, SOLD
    )
    val ABRAMS_4 = WheelEntity(
        ID4, NAME4, DEVICE_NAME4, DEVICE_ADDR4,
        PREMILEAGE4, MILEAGE4, WH4,
        VOLTAGE_MAX4, VOLTAGE_MIN4, VOLTAGE_RESERVE4, VOLTAGE_START4,
        CHARGE_RATE4, VOLTAGE_FULL4, SOLD
    )

    val S18_DISCONNECTED = WheelEntity(
        ID, NAME, null, null,
        PREMILEAGE, MILEAGE, WH,
        VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START,
        CHARGE_RATE, VOLTAGE_FULL, NOT_SOLD
    )
}
