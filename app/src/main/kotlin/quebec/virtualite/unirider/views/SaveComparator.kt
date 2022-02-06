package quebec.virtualite.unirider.views

import quebec.virtualite.unirider.database.WheelEntity
import java.util.Objects.equals

open class SaveComparator {

    open fun canSave(updatedWheel: WheelEntity?, initialWheel: WheelEntity?): Boolean {
        if (equals(updatedWheel, initialWheel)
            || updatedWheel!!.name == ""
            || updatedWheel.mileage == 0
            || updatedWheel.voltageMin == 0f
            || updatedWheel.voltageMax == 0f
        )
            return false

        return (updatedWheel.name != initialWheel!!.name) ||
                (updatedWheel.mileage != initialWheel.mileage) ||
                (updatedWheel.voltageMax != initialWheel.voltageMax) ||
                (updatedWheel.voltageMin != initialWheel.voltageMin)
    }
}
