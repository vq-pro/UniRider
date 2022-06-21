package quebec.virtualite.unirider

import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.WheelRow

object TestDomain {
    const val DEVICE_ADDR = "AA:BB:CC:DD:EE:FF"
    const val DEVICE_ADDR2 = "BB:CC:DD:EE:FF:GG"
    const val DEVICE_ADDR3 = "CC:DD:EE:FF:GG:HH"
    const val DEVICE_NAME = "KS-S18"
    const val DEVICE_NAME2 = "KS-S20"
    const val DEVICE_NAME3 = "LK2000"
    const val ID = 1111L
    const val ID2 = 1112L
    const val ID3 = 1113L
    const val LABEL_KM = "kilometers"
    const val LABEL_WH_PER_KM = "watt-hours per kilometer"
    const val KM = 4444f
    const val KM_NEW = 4445f
    const val KM_NEW_RAW = 4445.012f
    const val MILEAGE = 2222
    const val MILEAGE2 = 2223
    const val MILEAGE3 = 2223
    const val MILEAGE_NEW = 2224
    const val MILEAGE_NEW_RAW = 2223.617f
    const val NAME = "S18"
    const val NAME2 = "S20"
    const val NAME3 = "Sherman"
    const val NAME_NEW = "<New>"
    const val PERCENTAGE = 100.0f
    const val PREMILEAGE = 1
    const val PREMILEAGE2 = 10000
    const val PREMILEAGE3 = 10000
    const val PREMILEAGE_NEW = 20000
    const val REMAINING_RANGE = 5555f
    const val REMAINING_RANGE_ZERO = 0
    const val TEMPERATURE_NEW_RAW = 3333f
    const val TOTAL_RANGE = 6666f
    const val VOLTAGE = 84f
    const val VOLTAGE_MAX = 84.0f
    const val VOLTAGE_MAX2 = 126.0f
    const val VOLTAGE_MAX3 = 100.8f
    const val VOLTAGE_MAX_NEW = 100.9f
    const val VOLTAGE_NEW = 72.6f
    const val VOLTAGE_NEW_RAW = 72.56f
    const val VOLTAGE_MIN = 60.0f
    const val VOLTAGE_MIN2 = 90.0f
    const val VOLTAGE_MIN3 = 75.6f
    const val VOLTAGE_MIN_NEW = 74.5f
    const val VOLTAGE_RESERVE = 70.0f
    const val VOLTAGE_RESERVE2 = 95.0f
    const val VOLTAGE_RESERVE3 = 80.0f
    const val VOLTAGE_RESERVE_NEW = 70.1f
    const val VOLTAGE_START = 83.8f
    const val VOLTAGE_START2 = 124.5f
    const val VOLTAGE_START3 = 100.4f
    const val WH = 1110
    const val WH2 = 2220
    const val WH3 = 3600
    const val WH_NEW = 9999
    const val WH_PER_KM = 7777f
    val WHEEL_ROW_1_123 = WheelRow(ID, NAME, PREMILEAGE + MILEAGE)
    val WHEEL_ROW_2_456 = WheelRow(ID2, NAME2, PREMILEAGE2 + MILEAGE2)
    val WHEEL_ROW_3_123 = WheelRow(ID3, NAME3, PREMILEAGE3 + MILEAGE3)
    val WHEEL_ROW_NEW = WheelRow(0, NAME_NEW, 0)

    val DEVICE = BluetoothDevice(DEVICE_NAME, DEVICE_ADDR)
    val DEVICE2 = BluetoothDevice(DEVICE_NAME2, DEVICE_ADDR2)
    val DEVICE3 = BluetoothDevice(DEVICE_NAME3, DEVICE_ADDR3)

    val S18_1 =
        WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, PREMILEAGE, MILEAGE, WH, VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START)
    val S20_2 =
        WheelEntity(ID2, NAME2, DEVICE_NAME2, DEVICE_ADDR2, PREMILEAGE2, MILEAGE2, WH2, VOLTAGE_MAX2, VOLTAGE_MIN2, VOLTAGE_RESERVE2, VOLTAGE_START2)
    val SHERMAN_MAX_3 =
        WheelEntity(ID3, NAME3, DEVICE_NAME3, DEVICE_ADDR3, PREMILEAGE3, MILEAGE3, WH3, VOLTAGE_MAX3, VOLTAGE_MIN3, VOLTAGE_RESERVE3, VOLTAGE_START3)

    val S18_DISCONNECTED = WheelEntity(ID, NAME, null, null, PREMILEAGE, MILEAGE, WH, VOLTAGE_MAX, VOLTAGE_MIN, VOLTAGE_RESERVE, VOLTAGE_START)
}
