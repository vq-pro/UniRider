package quebec.virtualite.commons.android.views

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SpinnerAdapter

open class WidgetUtils {

    open fun addTextChangedListener(widget: EditText?, callback: ((text: String) -> Unit)?) {

        widget?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(field: Editable?) {}

            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                callback?.invoke(text.toString())
            }
        })
    }

    open fun <T> multifieldListAdapter(
        listView: ListView?, view: View?, id: Int?, items: List<T>?, display: ((View, T) -> Unit)?
    ) {
        listView!!.adapter = CustomListAdapter(view!!.context, id!!, items!!, display!!)
    }

    open fun listAdapter(listView: ListView, view: View?, id: Int?, contents: List<String>?) {
        listView.adapter = arrayAdapter(view!!, id!!, contents!!) as ListAdapter
    }

    open fun onItemSelectedListener(callback: (index: Int) -> Unit): AdapterView.OnItemSelectedListener {

        return object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                callback(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    open fun setOnClickListener(widget: View, callback: (View) -> Unit) {
        widget.setOnClickListener(View.OnClickListener { view -> callback(view) })
    }

    open fun setOnItemClickListener(listView: ListView?, callback: ((View, Int) -> Unit)?) {
        listView!!.setOnItemClickListener { _: AdapterView<*>, view: View, position: Int, _: Long ->
//            val item = listView.getItemAtPosition(position) as String
            callback!!(view, position)
        }
    }

    fun spinnerAdapter(view: View?, id: Int?, contents: List<String>?): SpinnerAdapter {
        return arrayAdapter(view!!, id!!, contents!!) as SpinnerAdapter
    }

    private fun <T> arrayAdapter(view: View, id: Int, contents: List<T>): Adapter {
        return ArrayAdapter(view.context, id, contents)
    }
}
