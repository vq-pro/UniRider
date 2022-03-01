package quebec.virtualite.commons.android.views.impl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class CustomListAdapter<T>(context: Context, val id: Int, items: List<T>, val display: (View, T) -> Unit) :
    ArrayAdapter<T>(context, id, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view: View? = convertView

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(id, parent, false)
        }

        // Update view
        display.invoke(view!!, getItem(position)!!)

        return view
    }
}