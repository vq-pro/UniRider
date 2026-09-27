package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.database.WheelEntity

interface Calculator
{
    fun voltage(wheel: WheelEntity, soE: Float): Float
    fun soE(wheel: WheelEntity, voltage: Float): Float
}
