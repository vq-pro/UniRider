package quebec.virtualite.commons.android.utils

object ArrayListUtils {

    fun <T> set(list: ArrayList<T>, newValues: List<T>) {
        list.clear()
        list.addAll(newValues)
    }
}