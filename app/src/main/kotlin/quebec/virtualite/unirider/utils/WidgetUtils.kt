package quebec.virtualite.unirider.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter

open class WidgetUtils {

    fun addTextChangedListener(callback: (text: String) -> Unit): TextWatcher {

        return object : TextWatcher {

            override fun afterTextChanged(field: Editable?) {}

            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                callback(text.toString())
            }
        }
    }

    open fun arrayAdapter(context: Context?, id: Int?, contents: List<String>?): SpinnerAdapter {
        return ArrayAdapter(context!!, id!!, contents!!)
    }

    fun onClickListener(callback: (View) -> Unit): View.OnClickListener {
        return View.OnClickListener { view -> callback(view) }
    }

    fun onItemSelectedListener(callback: (index: Int) -> Unit): AdapterView.OnItemSelectedListener {

        return object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                callback(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}