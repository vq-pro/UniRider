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
import androidx.core.view.isVisible
import quebec.virtualite.commons.android.views.impl.CustomListAdapter

open class CommonWidgetServices {

    private val POST_DELAY = 10L

    open fun addTextChangedListener(widget: EditText?, callback: ((text: String) -> Unit)?) {

        widget?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(field: Editable?) {}

            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                callback?.invoke(text.toString())
            }
        })
    }

    open fun disable(widget: View) {
        widget.postDelayed({ widget.isEnabled = false }, POST_DELAY)
    }

    open fun enable(widget: View) {
        widget.postDelayed({ widget.isEnabled = true }, POST_DELAY)
    }

    open fun hide(widget: View) {
        widget.isVisible = false
    }

    open fun <T> multifieldListAdapter(
        listView: ListView?, view: View?, id: Int?, items: List<T>?, display: ((View, T) -> Unit)?
    ) {
        listView!!.adapter = CustomListAdapter(view!!.context, id!!, items!!, display!!)
    }

    open fun onItemSelectedListener(callback: (index: Int) -> Unit): AdapterView.OnItemSelectedListener {

        return object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                callback(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    open fun setOnClickListener(widget: View?, callback: ((View) -> Unit)?) {
        widget!!.setOnClickListener { view -> callback!!(view) }
    }

    open fun setOnItemClickListener(listView: ListView?, callback: ((View, Int) -> Unit)?) {
        listView!!.setOnItemClickListener { _: AdapterView<*>, view: View, position: Int, _: Long ->
//            val item = listView.getItemAtPosition(position) as String
            callback!!(view, position)
        }
    }

    open fun setOnLongClickListener(widget: View?, callback: ((View) -> Unit)?) {
        widget!!.setOnLongClickListener { view ->
            callback!!(view)
            true
        }
    }

    open fun show(widget: View) {
        widget.isVisible = true
    }

    open fun text(edit: EditText): String {
        return edit.text.toString().trim()
    }

    open fun stringListAdapter(listView: ListView, view: View?, contents: List<String>?) {
        listView.adapter = arrayAdapter(view!!, android.R.layout.simple_list_item_1, contents!!) as ListAdapter
    }

    private fun <T> arrayAdapter(view: View, id: Int, contents: List<T>): Adapter {
        return ArrayAdapter(view.context, id, contents)
    }

    private fun spinnerAdapter(view: View?, id: Int?, contents: List<String>?): SpinnerAdapter {
        return arrayAdapter(view!!, id!!, contents!!) as SpinnerAdapter
    }
}
