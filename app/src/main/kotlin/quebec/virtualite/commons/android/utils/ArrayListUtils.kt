package quebec.virtualite.commons.android.utils

object ArrayListUtils {

    fun <T> addTo(list: List<T>, newEntry: T): ArrayList<T> {
        val newList = ArrayList<T>()
        newList.addAll(list)
        newList.add(newEntry)

        return newList
    }

    fun <T> indexOf(list: List<T>, item: T): Int {
        for (i in list.indices) {
            if (list[i]!!.equals(item)) {
                return i
            }
        }
        return -1
    }

    fun <T> setList(list: ArrayList<T>, newValues: List<T>) {
        list.clear()
        list.addAll(newValues)
    }
}