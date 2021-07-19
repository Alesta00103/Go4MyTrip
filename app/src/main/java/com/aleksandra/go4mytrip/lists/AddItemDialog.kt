package com.aleksandra.go4mytrip.lists

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import com.aleksandra.go4mytrip.R
import java.util.*

class AddItemDialog : AppCompatDialogFragment() {
    private lateinit var nameItem: EditText
    private lateinit var selection: String
    private lateinit var addItemDialogListener: AddItemDialogListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = Objects.requireNonNull(activity)?.layoutInflater
        val view = inflater?.inflate(R.layout.layout_dialog, null)
        val categories = activity?.resources?.getStringArray(R.array.categories)
        categories?.let {
            builder.setView(view)
                    .setTitle("Add item")
                    .setMessage("Select category")
                    .setSingleChoiceItems(R.array.categories, -1) { dialog, which -> selection = it[which] }
                    .setMessage("Enter name")
                    .setNegativeButton("cancel") { dialog, which -> }
                    .setPositiveButton("Ok") { dialog, which ->
                        val name = nameItem.text.toString()
                        addItemDialogListener.applyTexts(name, selection)
                    }
            if (view != null) {
                nameItem = view.findViewById(R.id.et_itemName)
            }
        }
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addItemDialogListener = try {
            context as AddItemDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "must implement AddItemDialogListener")
        }
    }

    interface AddItemDialogListener {
        fun applyTexts(name: String, selection: String)
    }
}