package quebec.virtualite.unirider.views

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Wheel(

    val name: String,
    val highest: Float,
    val lowest: Float

) : Parcelable