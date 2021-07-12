package com.aleksandra.go4mytrip.lists;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.aleksandra.go4mytrip.R;

import java.util.Objects;

public class AddItemDialog extends AppCompatDialogFragment {

    EditText nameItem;
    String selection;
    AddItemDialogListener addItemDialogListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_dialog, container, false);
        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null); //layoutdialog
        final String[] categories = getActivity().getResources().getStringArray(R.array.categories);
        builder.setView(view)
                .setTitle("Add item")
                .setMessage("Select category")
                .setSingleChoiceItems(R.array.categories, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection = categories[which];
                    }
                })
                .setMessage("Enter name")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameItem.getText().toString();
                        addItemDialogListener.applyTexts(name, selection);
                    }
                });

        nameItem = view.findViewById(R.id.et_itemName);
        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addItemDialogListener = (AddItemDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddItemDialogListener");
        }
    }

    public interface AddItemDialogListener {
        void applyTexts(String name, String selection);
    }
}
