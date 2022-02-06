package quebec.virtualite.commons.android.utils

object ArrayListUtils {

    fun <T> addTo(list: List<T>, newEntry: T): ArrayList<T> {
        val newList = ArrayList<T>()
        newList.addAll(list)
        newList.add(newEntry)

        return newList
    }

    fun <T> setList(list: ArrayList<T>, newValues: List<T>) {
        list.clear()
        list.addAll(newValues)
    }
}