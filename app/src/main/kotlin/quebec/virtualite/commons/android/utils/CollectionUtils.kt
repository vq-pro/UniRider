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

    fun <T> setList(list: ArrayList<T>, newValues: List<T>) {
        list.clear()
        list.addAll(newValues)
    }
}
