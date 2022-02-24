package quebec.virtualite.commons.android.utils

object ByteArrayUtils {

    fun byteArrayInt2(low: Byte, high: Byte): Int {

        return ((high.toUByte() * 0x100u) + low.toUByte()).toInt()
    }

    fun byteArrayInt4(low1: Byte, high1: Byte, low2: Byte, high2: Byte): Long {

        return (byteArrayInt2(low1, high1).toULong() * 0x10000u
                + byteArrayInt2(low2, high2).toULong()).toLong()
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