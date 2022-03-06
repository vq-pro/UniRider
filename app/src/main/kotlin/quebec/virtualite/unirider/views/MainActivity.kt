package quebec.virtualite.unirider.views

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import quebec.virtualite.commons.android.views.CommonActivity
import quebec.virtualite.unirider.R

class MainActivity : CommonActivity(
    R.layout.activity_main,
    R.id.toolbar,
    R.menu.menu_main,
    R.id.action_settings,
    ACCESS_COARSE_LOCATION
)
