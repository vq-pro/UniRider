package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.database.WheelDatabase

class MainActivity : AppCompatActivity() {

    companion object {
//        var connector: DeviceConnector = DeviceConnectorImpl()
//        var scanner: DeviceScanner = DeviceScannerImpl()

        lateinit var db: WheelDatabase
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
//        db = Room.databaseBuilder(
//            applicationContext,
//            WheelDatabase::class.java,
//            "wheel_database"
//        ).build()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

//        connector.init(this)
//        scanner.init(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
