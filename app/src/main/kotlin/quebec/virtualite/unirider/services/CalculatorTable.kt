package quebec.virtualite.unirider.services

import quebec.virtualite.commons.android.utils.NumberUtils.round

private const val DECIMALS_KM = 2
private const val DECIMALS_VOLTAGE = 3

class CalculatorTable : Calculator
{
    data class SoeVoltage(
        val soe: Float, val voltage: Float
    )

    private val SOE_VOLTAGE = arrayOf(
        SoeVoltage(100.0f, 4.2f),
        SoeVoltage(98.9f, 4.187f),
        SoeVoltage(97.8f, 4.174f),
        SoeVoltage(96.7f, 4.162f),
        SoeVoltage(95.6f, 4.150f),
        SoeVoltage(94.5f, 4.137f),
        SoeVoltage(93.4f, 4.125f),
        SoeVoltage(92.3f, 4.114f),
        SoeVoltage(91.2f, 4.102f),
        SoeVoltage(90.2f, 4.091f),
        SoeVoltage(89.1f, 4.080f),
        SoeVoltage(88.0f, 4.069f),
        SoeVoltage(86.9f, 4.058f),
        SoeVoltage(85.9f, 4.048f),
        SoeVoltage(84.8f, 4.037f),
        SoeVoltage(83.7f, 4.027f),
        SoeVoltage(82.7f, 4.017f),
        SoeVoltage(81.6f, 4.007f),
        SoeVoltage(80.6f, 3.997f),
        SoeVoltage(79.5f, 3.988f),
        SoeVoltage(78.5f, 3.980f),
        SoeVoltage(77.4f, 3.972f),
        SoeVoltage(76.4f, 3.964f),
        SoeVoltage(75.3f, 3.956f),
        SoeVoltage(74.3f, 3.949f),
        SoeVoltage(73.2f, 3.942f),
        SoeVoltage(72.2f, 3.935f),
        SoeVoltage(71.1f, 3.929f),
        SoeVoltage(70.1f, 3.922f),
        SoeVoltage(69.1f, 3.916f),
        SoeVoltage(68.0f, 3.910f),
        SoeVoltage(67.0f, 3.904f),
        SoeVoltage(66.0f, 3.899f),
        SoeVoltage(65.0f, 3.893f),
        SoeVoltage(63.9f, 3.888f),
        SoeVoltage(62.9f, 3.883f),
        SoeVoltage(61.9f, 3.878f),
        SoeVoltage(60.9f, 3.874f),
        SoeVoltage(59.8f, 3.869f),
        SoeVoltage(58.8f, 3.865f),
        SoeVoltage(57.8f, 3.860f),
        SoeVoltage(56.8f, 3.856f),
        SoeVoltage(55.8f, 3.852f),
        SoeVoltage(54.7f, 3.848f),
        SoeVoltage(53.7f, 3.844f),
        SoeVoltage(52.7f, 3.840f),
        SoeVoltage(51.7f, 3.836f),
        SoeVoltage(50.7f, 3.832f),
        SoeVoltage(49.7f, 3.828f),
        SoeVoltage(48.7f, 3.824f),
        SoeVoltage(47.7f, 3.819f),
        SoeVoltage(46.7f, 3.814f),
        SoeVoltage(45.7f, 3.809f),
        SoeVoltage(44.6f, 3.803f),
        SoeVoltage(43.6f, 3.798f),
        SoeVoltage(42.6f, 3.792f),
        SoeVoltage(41.6f, 3.786f),
        SoeVoltage(40.6f, 3.780f),
        SoeVoltage(39.6f, 3.773f),
        SoeVoltage(38.7f, 3.767f),
        SoeVoltage(37.7f, 3.760f),
        SoeVoltage(36.7f, 3.753f),
        SoeVoltage(35.7f, 3.746f),
        SoeVoltage(34.7f, 3.738f),
        SoeVoltage(33.7f, 3.731f),
        SoeVoltage(32.7f, 3.723f),
        SoeVoltage(31.7f, 3.714f),
        SoeVoltage(30.8f, 3.706f),
        SoeVoltage(29.8f, 3.698f),
        SoeVoltage(28.8f, 3.689f),
        SoeVoltage(27.8f, 3.680f),
        SoeVoltage(26.9f, 3.671f),
        SoeVoltage(25.9f, 3.662f),
        SoeVoltage(24.9f, 3.652f),
        SoeVoltage(24.0f, 3.642f),
        SoeVoltage(23.0f, 3.633f),
        SoeVoltage(22.1f, 3.622f),
        SoeVoltage(21.1f, 3.612f),
        SoeVoltage(20.1f, 3.602f),
        SoeVoltage(19.2f, 3.591f),
        SoeVoltage(18.3f, 3.580f),
        SoeVoltage(17.3f, 3.569f),
        SoeVoltage(16.4f, 3.558f),
        SoeVoltage(15.4f, 3.546f),
        SoeVoltage(14.5f, 3.534f),
        SoeVoltage(13.6f, 3.522f),
        SoeVoltage(12.6f, 3.510f),
        SoeVoltage(11.7f, 3.497f),
        SoeVoltage(10.8f, 3.485f),
        SoeVoltage(9.9f, 3.472f),
        SoeVoltage(9.0f, 3.460f),
        SoeVoltage(8.0f, 3.447f),
        SoeVoltage(7.1f, 3.435f),
        SoeVoltage(6.2f, 3.422f),
        SoeVoltage(5.3f, 3.409f),
        SoeVoltage(4.4f, 3.396f),
        SoeVoltage(3.5f, 3.383f),
        SoeVoltage(2.7f, 3.370f),
        SoeVoltage(1.8f, 3.357f),
        SoeVoltage(0.9f, 3.343f),
        SoeVoltage(0.0f, 3.330f)
    )

    fun distanceRemaining(initialVoltage: Float, actualVoltage: Float, distance: Float): Float
    {
        val initialSoe = soE(initialVoltage)
        val actualSoe = soE(actualVoltage)

        val usedSoe = initialSoe - actualSoe
        val remainingUsableSoe = actualSoe

        return round(distance * remainingUsableSoe / usedSoe, DECIMALS_KM)
    }

    override fun soE(voltage: Float): Float
    {
        if (voltage <= 4.2f) SOE_VOLTAGE.forEachIndexed { index, soe ->
            when
            {
                soe.voltage == voltage -> return soe.soe
                soe.voltage < voltage ->
                {
                    val higherVoltage = SOE_VOLTAGE[index - 1].voltage
                    val lowerVoltage = SOE_VOLTAGE[index].voltage
                    val voltageSpan = higherVoltage - lowerVoltage
                    val proportion = voltage - lowerVoltage
                    val percentage = proportion / voltageSpan

                    val higherSoe = SOE_VOLTAGE[index - 1].soe
                    val lowerSoe = SOE_VOLTAGE[index].soe
                    val soeSpan = higherSoe - lowerSoe
                    val resultingSoe = lowerSoe + (percentage * soeSpan)

                    return round(resultingSoe, DECIMALS_VOLTAGE)
                }
            }
        }

        return 0.0f
    }

    override fun voltage(soE: Float): Float
    {
        TODO("Not yet implemented")
    }
}