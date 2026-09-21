package quebec.virtualite.commons.android.views

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.WindowCompat

open class CommonActivity(
    private val idMainLayout: Int,
    private val idToolbar: Int,
    private val idMenu: Int?,
    private val idActionSettings: Int,
    private vararg val requiredPermissions: String

) : AppCompatActivity()
{
    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(idMainLayout)
        setSupportActionBar(findViewById(idToolbar))

        for (permission in requiredPermissions)
        {
            requestPermissionIfNotGranted(permission)
        }

        // Force status bar icons (time, battery, etc.) to be dark/visible
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        return if (idMenu != null)
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(idMenu, menu)
            true

        } else super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId)
        {
            idActionSettings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun requestPermissionIfNotGranted(permission: String)
    {
        if (checkSelfPermission(this, permission) != PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(this, permission))
            {
                requestPermissions(this, arrayOf(permission), 1)
            }
        }
    }

    companion object
    {
        val NO_MENU: Int? = null
    }
}