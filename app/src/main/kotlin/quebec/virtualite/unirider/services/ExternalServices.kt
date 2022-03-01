package quebec.virtualite.unirider.services

import quebec.virtualite.commons.android.external.CommonExternalServices
import quebec.virtualite.commons.android.views.CommonFragment
import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.bluetooth.WheelConnectorFactory
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.impl.WheelDbImpl

open class ExternalServices(val fragment: CommonFragment<ExternalServices>) : CommonExternalServices() {

    companion object {
        private var connector: WheelConnector? = null
        private var db: WheelDb? = null
    }

    override fun init() {
        if (connector == null)
            connector = WheelConnectorFactory.getConnector(fragment.activity!!)

        if (db == null)
            db = WheelDbImpl(fragment.activity?.applicationContext!!)
    }

    open fun connector(): WheelConnector {
        return connector!!
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
