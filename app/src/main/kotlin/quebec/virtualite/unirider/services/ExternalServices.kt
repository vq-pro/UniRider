package quebec.virtualite.unirider.services

import quebec.virtualite.commons.android.external.CommonExternalServices
import quebec.virtualite.commons.android.views.CommonFragment
import quebec.virtualite.unirider.bluetooth.BluetoothServices
import quebec.virtualite.unirider.bluetooth.BluetoothServicesFactory
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.impl.WheelDbImpl

open class ExternalServices(val fragment: CommonFragment<ExternalServices>) : CommonExternalServices() {

    companion object {
        private var bluetooth: BluetoothServices? = null
        private var db: WheelDb? = null
    }

    override fun init() {
        if (bluetooth == null)
            bluetooth = BluetoothServicesFactory.getBluetoothServices(fragment.activity!!)

        if (db == null)
            db = WheelDbImpl(fragment.activity?.applicationContext!!)
    }

    open fun bluetooth(): BluetoothServices {
        return bluetooth!!
    }

    open fun db(): WheelDb {
        return db!!
    }

    open fun runDB(function: ((WheelDb) -> Unit)?) {
        fragment.fragments.runBackground {
            function!!(db!!)
        }
    }
}
