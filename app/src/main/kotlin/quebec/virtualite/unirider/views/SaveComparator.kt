package quebec.virtualite.unirider.views

import quebec.virtualite.unirider.database.WheelEntity
import java.util.Objects.equals

open class SaveComparator {

    open fun canSave(updatedWheel: WheelEntity?, initialWheel: WheelEntity?): Boolean {
        if (equals(updatedWheel, initialWheel))
            return false

        if (updatedWheel!!.name != ""
            && updatedWheel.name != initialWheel!!.name
        )
            return true

        return false
    }
}
