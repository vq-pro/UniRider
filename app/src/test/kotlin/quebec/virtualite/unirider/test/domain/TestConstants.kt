package quebec.virtualite.unirider.test.domain

import quebec.virtualite.commons.android.bluetooth.BluetoothDevice
import quebec.virtualite.unirider.database.WheelEntity
import quebec.virtualite.unirider.views.WheelRow

object TestConstants {
    const val CHARGE_AMPERAGE = 2f
    const val CHARGE_AMPERAGE2 = 1.5f
    const val CHARGE_AMPERAGE3 = 2.5f
    const val CHARGE_AMPERAGE4 = 5f
    const val CHARGE_AMPERAGE5 = 1.1f
    const val CHARGE_AMPERAGE_NEW = 2.2f
    const val CHARGE_RATE = 4f
    const val CHARGE_RATE2 = 6f
    const val CHARGE_RATE3 = 8f
    const val CHARGE_RATE4 = 10f
    const val CHARGE_RATE5 = 21f
    const val CHARGE_RATE_NEW = 10f
    const val CHARGER_OFFSET = 1.4f
    const val DEVICE_ADDR = "AA:BB:CC:DD:EE:FF"
    const val DEVICE_ADDR2 = "BB:CC:DD:EE:FF:GG"
    const val DEVICE_ADDR3 = "CC:DD:EE:FF:GG:HH"
    const val DEVICE_ADDR4 = "DD:EE:FF:GG:HH:II"
    const val DEVICE_ADDR5 = "EE:FF:GG:HH:II:JJ"
    const val DEVICE_NAME = "KS-S18"
    const val DEVICE_NAME2 = "KS-S20"
    const val DEVICE_NAME3 = "LK2000"
    const val DEVICE_NAME4 = "LK2001"
    const val DEVICE_NAME5 = "LK2002"
    const val DISTANCE_OFFSET = 1.2f
    const val DISTANCE_OFFSET2 = 1.0f
    const val DISTANCE_OFFSET3 = 1.0f
    const val DISTANCE_OFFSET4 = 1.0f
    const val DISTANCE_OFFSET5 = 1.0181f
    const val DISTANCE_OFFSET_NEW = 1.5f
    const val ID = 1111L
    const val ID2 = 1112L
    const val ID3 = 1113L
    const val ID4 = 1114L
    const val ID5 = 1115L
    const val ITEM_SOLD = "Sold"
    const val KM = 4444f
    const val KM_NEW = 3704.2f
    const val KM_NEW_RAW = 4445.012f
    const val KM_REQUESTED = 10f
    const val LABEL_KM = "km"
    const val MILEAGE = 2222
    const val MILEAGE2 = 2222
    const val MILEAGE3 = 2223
    const val MILEAGE4 = 2224
    const val MILEAGE5 = 2225
    const val MILEAGE_NEW = 2224
    const val MILEAGE_NEW_RAW = 2223.617f
    const val NAME = "S18"
    const val NAME2 = "S20"
    const val NAME3 = "Sherman Max"
    const val NAME4 = "Abrams"
    const val NAME5 = "Sherman L"
    const val NAME_NEW = "<New>"
    const val NAME_SOLD = "<$ITEM_SOLD>"
    const val NOT_SOLD = false
    const val PERCENTAGE = 100.0f
    const val PREMILEAGE = 10000
    const val PREMILEAGE2 = 10000
    const val PREMILEAGE3 = 200
    const val PREMILEAGE4 = 0
    const val PREMILEAGE5 = 0
    const val PREMILEAGE_NEW = 20000
    const val REMAINING_RANGE = 5555f
    const val SOLD = true
    const val TEMPERATURE_NEW_RAW = 3333f
    const val TOTAL_RANGE = 6666f
    const val VOLTAGE = 79f
    const val VOLTAGE_FULL = 81.5f
    const val VOLTAGE_FULL2 = 123f
    const val VOLTAGE_FULL3 = 99.5f
    const val VOLTAGE_FULL4 = 99.5f
    const val VOLTAGE_FULL5 = 149.3f
    const val VOLTAGE_FULL_NEW = 80.7f
    const val VOLTAGE_MAX = 84.0f
    const val VOLTAGE_MAX2 = 126.0f
    const val VOLTAGE_MAX3 = 100.8f
    const val VOLTAGE_MAX4 = 100.8f
    const val VOLTAGE_MAX5 = 151.2f
    const val VOLTAGE_MAX_NEW = 100.9f
    const val VOLTAGE_NEW = 80.9f
    const val VOLTAGE_NEW5 = 146.1f
    const val VOLTAGE_NEW_RAW = 80.9001f
    const val VOLTAGE_MIN = 60.0f
    const val VOLTAGE_MIN2 = 90.0f
    const val VOLTAGE_MIN3 = 75.6f
    const val VOLTAGE_MIN4 = 75.6f
    const val VOLTAGE_MIN5 = 104.4f
    const val VOLTAGE_MIN_NEW = 62.0f
    const val WH = 1110
    const val WH2 = 2220
    const val WH3 = 3600
    const val WH4 = 2700
    const val WH5 = 4000
    const val WH_NEW = 9999

    val DEVICE3 = BluetoothDevice(DEVICE_NAME3, DEVICE_ADDR3)

    val WHEEL_ROW_S18_1_123 = WheelRow(ID, NAME, PREMILEAGE + MILEAGE)
    val WHEEL_ROW_S20_2_123 = WheelRow(ID2, NAME2, PREMILEAGE2 + MILEAGE2)
    val WHEEL_ROW_SHERMAN_MAX_3 = WheelRow(ID3, "- $NAME3", PREMILEAGE3 + MILEAGE3)
    val WHEEL_ROW_ABRAMS_4 = WheelRow(ID4, "- $NAME4", PREMILEAGE4 + MILEAGE4)
    val WHEEL_ROW_NEW = WheelRow(0, NAME_NEW, 0)

    val S18_1_CONNECTED = WheelEntity(
        ID, NAME, DEVICE_NAME, DEVICE_ADDR,
        PREMILEAGE, MILEAGE, WH,
        VOLTAGE_MAX, VOLTAGE_MIN,
        CHARGE_AMPERAGE, CHARGE_RATE, VOLTAGE_FULL,
        DISTANCE_OFFSET, NOT_SOLD
    )
    val S18_1_DISCONNECTED = WheelEntity(
        ID, NAME, null, null,
        PREMILEAGE, MILEAGE, WH,
        VOLTAGE_MAX, VOLTAGE_MIN,
        CHARGE_AMPERAGE, CHARGE_RATE, VOLTAGE_FULL,
        DISTANCE_OFFSET, NOT_SOLD
    )
    val S20_2 = WheelEntity(
        ID2, NAME2, DEVICE_NAME2, DEVICE_ADDR2,
        PREMILEAGE2, MILEAGE2, WH2,
        VOLTAGE_MAX2, VOLTAGE_MIN2,
        CHARGE_AMPERAGE2, CHARGE_RATE2, VOLTAGE_FULL2,
        DISTANCE_OFFSET2, NOT_SOLD
    )
    val SHERMAN_MAX_3_SOLD = WheelEntity(
        ID3, NAME3, DEVICE_NAME3, DEVICE_ADDR3,
        PREMILEAGE3, MILEAGE3, WH3,
        VOLTAGE_MAX3, VOLTAGE_MIN3,
        CHARGE_AMPERAGE3, CHARGE_RATE3, VOLTAGE_FULL3, DISTANCE_OFFSET3, SOLD
    )
    val ABRAMS_4 = WheelEntity(
        ID4, NAME4, DEVICE_NAME4, DEVICE_ADDR4,
        PREMILEAGE4, MILEAGE4, WH4,
        VOLTAGE_MAX4, VOLTAGE_MIN4,
        CHARGE_AMPERAGE4, CHARGE_RATE4, VOLTAGE_FULL4, DISTANCE_OFFSET4, SOLD
    )

    val SHERMAN_L_5 = WheelEntity(
        ID5, NAME5, DEVICE_NAME5, DEVICE_ADDR5,
        PREMILEAGE5, MILEAGE5, WH5,
        VOLTAGE_MAX5, VOLTAGE_MIN5,
        CHARGE_AMPERAGE5, CHARGE_RATE5, VOLTAGE_FULL5, DISTANCE_OFFSET5, NOT_SOLD
    )

    val EMPTY_WHEEL = WheelEntity(0L, "", null, null, 0, 0, 0, 0f, 0f, 0f, 0f, 0f, 0f, false)
}
