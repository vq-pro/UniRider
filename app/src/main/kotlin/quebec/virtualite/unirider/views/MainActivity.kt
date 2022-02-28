package quebec.virtualite.unirider.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import quebec.virtualite.unirider.R
import quebec.virtualite.unirider.bluetooth.WheelConnector
import quebec.virtualite.unirider.bluetooth.WheelConnectorFactory
import quebec.virtualite.unirider.database.WheelDb
import quebec.virtualite.unirider.database.impl.WheelDbImpl

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var connector: WheelConnector
        lateinit var db: WheelDb
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        db = WheelDbImpl(applicationContext)
        connector = WheelConnectorFactory.getConnector(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
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
