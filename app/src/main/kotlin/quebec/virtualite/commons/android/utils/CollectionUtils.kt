package quebec.virtualite.commons.android.utils

object CollectionUtils {

    fun <T> addTo(list: List<T>, vararg newEntries: T): ArrayList<T> {
        val newList = ArrayList<T>()
        newList.addAll(list)
        for (newEntry in newEntries) {
            newList.add(newEntry)
        }

        return newList
    }

    fun deserialize(input: String): List<String> = input.split("|")

    fun <T> indexOf(list: List<T>, item: T): Int {
        for (i in list.indices) {
            if (item!! == list[i]!!) {
                return i
            }
        }
        return -1
    }

    fun <T> setList(list: ArrayList<T>, newValues: List<T>) {
        list.clear()
        list.addAll(newValues)
    }

    fun <T> serialize(list: List<T>): String {
        val buffer = StringBuffer()
        for (item in list) {
            if (buffer.length > 0) {
                buffer.append("|")
            }
            buffer.append(item)
        }
        return buffer.toString()
    }
}
