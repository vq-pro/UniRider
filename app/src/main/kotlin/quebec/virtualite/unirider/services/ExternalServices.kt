package quebec.virtualite.unirider.services

import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.bluetooth.WheelConnectorFactory
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.impl.WheelDbImpl
import quebec.virtualite.unirider.views.BaseFragment

open class ExternalServices(val fragment: BaseFragment) {
    companion object {
        private var connector: WheelConnector? = null
        private var db: WheelDb? = null
    }

    open fun connector(): WheelConnector {
        return connector!!
    }

    open fun init() {
        if (connector == null)
            connector = WheelConnectorFactory.getConnector(fragment.activity!!)

        if (db == null)
            db = WheelDbImpl(fragment.activity?.applicationContext!!)
    }

    open fun db(): WheelDb {
        return db!!
    }
}
