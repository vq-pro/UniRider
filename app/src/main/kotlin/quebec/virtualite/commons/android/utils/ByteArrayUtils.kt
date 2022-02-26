package quebec.virtualite.commons.android.utils

object ByteArrayUtils {

    fun byteArrayInt2(high: Byte, low: Byte): Int {
        return ((high.toUByte() * 0x100u) + low.toUByte()).toInt()
    }

    fun byteArrayInt4(high1: Byte, low1: Byte, high2: Byte, low2: Byte): Long {
        return (byteArrayInt2(high1, low1).toULong() * 0x10000u
                + byteArrayInt2(high2, low2).toULong()).toLong()
    }

    fun byteArrayToString(data: ByteArray): String {
        val string = StringBuilder()
        for (byte in data) {
            if (string.isNotEmpty())
                string.append("-")
            val value = byte.toUByte().toString(16).uppercase()
            string.append(if (value.length == 1) "0$value" else value)
        }

        return string.toString()
    }
}