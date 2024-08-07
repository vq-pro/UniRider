package quebec.virtualite.commons.android.views

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.switchmaterial.SwitchMaterial
import quebec.virtualite.commons.android.utils.CollectionUtils.setList
import quebec.virtualite.commons.android.views.impl.CustomListAdapter

open class CommonWidgetServices {

    private val POST_DELAY = 10L

    open fun <T> addListViewEntry(listView: ListView, items: ArrayList<T>, entry: T) {
        val updatedList = ArrayList<T>()
        setList(updatedList, items)
        updatedList.add(entry)

        setListViewEntries(listView, items, updatedList.sortedBy { it.toString() })
    }

    open fun addTextChangedListener(widget: EditText?, callback: ((text: String) -> Unit)?) {
        widget?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(field: Editable?) {}

            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                callback?.invoke(text.toString())
            }
        })
    }

    open fun clearSelection(spinner: Spinner) {
        notifyOnChanged(spinner)
    }

    open fun disable(vararg widgets: View) {
        for (widget in widgets) {
            widget.postDelayed({
                widget.isEnabled = false
                widget.alpha = 1f
            }, POST_DELAY)
        }
    }

    open fun enable(vararg widgets: View) {
        for (widget in widgets) {
            widget.postDelayed({
                widget.isEnabled = true
                widget.alpha = 1f
            }, POST_DELAY)
        }
    }

    open fun getText(edit: EditText): String {
        return edit.text.toString().trim()
    }

    open fun hide(vararg widgets: View) {
        for (widget in widgets) {
            widget.isVisible = false
        }
    }

    open fun <T> multifieldListAdapter(
        listView: ListView?, view: View?, id: Int?, items: List<T>?, display: ((View, T) -> Unit)?
    ) {
        listView!!.adapter = CustomListAdapter(view!!.context, id!!, items!!, display!!)
    }

    open fun onItemSelectedListener(callback: (index: Int) -> Unit): OnItemSelectedListener {
        return object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                callback(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    open fun <T> setListViewEntries(listView: ListView, listViewItems: ArrayList<T>, newItems: List<T>) {
        setList(listViewItems, newItems)
        notifyOnChanged(listView)
    }

    open fun setOnCheckedChangeListener(widget: SwitchMaterial?, callback: ((value: Boolean) -> Unit)?) {
        widget?.setOnCheckedChangeListener { buttonView, isChecked ->
            callback?.invoke(isChecked)
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

    open fun setOnItemSelectedListener(spinner: Spinner?, callback: ((View?, Int, String) -> Unit)?) {
        spinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val text = spinner.getItemAtPosition(position) as String
                callback!!(view, position, text)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    open fun setOnLongClickListener(widget: View?, callback: ((View) -> Unit)?) {
        widget!!.setOnLongClickListener { view ->
            callback!!(view)
            true
        }
    }

    open fun setText(edit: EditText, value: String) {
        edit.setText(value)
    }

    open fun setSelection(spinner: Spinner, index: Int) {
        notifyOnChanged(spinner)
        spinner.setSelection(index, true)
    }

    open fun show(vararg widgets: View) {
        for (widget in widgets) {
            widget.isVisible = true
        }
    }

    open fun stringListAdapter(listView: ListView, view: View?, contents: List<String>?) {
        listView.adapter = ArrayAdapter(view!!.context, android.R.layout.simple_list_item_1, contents!!)
    }

    open fun stringListAdapter(spinner: Spinner?, view: View?, contents: List<String>?) {
        val spinnerAdapter = object : ArrayAdapter<String>(view!!.context, android.R.layout.simple_spinner_item, contents!!) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getView(position, convertView, parent) as TextView
                textView.ellipsize = null
                return textView
            }
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner!!.adapter = spinnerAdapter
    }

    private fun notifyOnChanged(listView: ListView) {
        @Suppress("UNCHECKED_CAST") val listViewAdapter: ArrayAdapter<String> = listView.adapter as ArrayAdapter<String>
        listViewAdapter.notifyDataSetChanged()
    }

    private fun notifyOnChanged(spinner: Spinner) {
        @Suppress("UNCHECKED_CAST") val spinnerAdapter: ArrayAdapter<String> = spinner.adapter as ArrayAdapter<String>
        spinnerAdapter.notifyDataSetChanged()
    }
}
