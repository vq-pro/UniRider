package quebec.virtualite.unirider

import quebec.virtualite.unirider.bluetooth.Device
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.WheelRow
import java.lang.Float.parseFloat

object TestDomain {
    val DEVICE_ADDR = "AA:BB:CC:DD:EE:FF"
    val DEVICE_ADDR2 = "BB:CC:DD:EE:FF:GG"
    val DEVICE_ADDR3 = "CC:DD:EE:FF:GG:HH"
    val DEVICE_NAME = "KS-S18"
    val DEVICE_NAME2 = "KS-S20"
    val DEVICE_NAME3 = "LK2000"
    val ID = 1111L
    val ID2 = 1112L
    val ID3 = 1113L
    val MILEAGE = 2222
    val MILEAGE2 = 2223
    val MILEAGE3 = 2223
    val MILEAGE_NEW = 2224
    val NAME = "S18"
    val NAME2 = "S20"
    val NAME3 = "Sherman"
    val NAME_NEW = "<New>"
    val PERCENTAGE = 100.0f
    val PERCENTAGE_S = "100.0%"
    val VOLTAGE_S = "100.8"
    val VOLTAGE = parseFloat(VOLTAGE_S)
    val VOLTAGE_MAX = 84.0f
    val VOLTAGE_MAX2 = 126.0f
    val VOLTAGE_MAX3 = 100.8f
    val VOLTAGE_MAX_NEW = 100.9f
    val VOLTAGE_MIN = 60.0f
    val VOLTAGE_MIN2 = 90.0f
    val VOLTAGE_MIN3 = 75.6f
    val VOLTAGE_MIN_NEW = 74.5f
    val WHEEL_ROW_1_123 = WheelRow(ID, NAME, MILEAGE)
    val WHEEL_ROW_2_456 = WheelRow(ID2, NAME2, MILEAGE2)
    val WHEEL_ROW_3_123 = WheelRow(ID3, NAME3, MILEAGE3)
    val WHEEL_ROW_NEW = WheelRow(0, NAME_NEW, 0)

    val DEVICE = Device(DEVICE_NAME, DEVICE_ADDR)
    val DEVICE2 = Device(DEVICE_NAME2, DEVICE_ADDR2)
    val DEVICE3 = Device(DEVICE_NAME3, DEVICE_ADDR3)

    val S18_1 = WheelEntity(ID, NAME, DEVICE_NAME, DEVICE_ADDR, MILEAGE, VOLTAGE_MIN, VOLTAGE_MAX)
    val S20_2 = WheelEntity(ID2, NAME2, DEVICE_NAME2, DEVICE_ADDR2, MILEAGE2, VOLTAGE_MIN2, VOLTAGE_MAX2)
    val SHERMAN_3 = WheelEntity(ID3, NAME3, DEVICE_NAME3, DEVICE_ADDR3, MILEAGE3, VOLTAGE_MIN3, VOLTAGE_MAX3)
}
