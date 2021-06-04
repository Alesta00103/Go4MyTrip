package com.aleksandra.go4mytrip.lists;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aleksandra.go4mytrip.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingList extends AppCompatActivity implements AddItemDialog.AddItemDialogListener {

    List<PackingModel> shoppingList;

    FirebaseUser user;
    String uid;
    String idTrip;
    DatabaseReference referencePackingList;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    FloatingActionButton fab;
    ImageView backBtn;

    CharSequence[] categories;
    String id;
    String v;
    String nameOfItem;
    String categoryOfItem;
    String selectedCategory;


    Boolean isPackingList = false;
    TextView textOfItem;
    TextView numberOfItems;
    int numberOfShoppingItems = 0;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        categories = new CharSequence[]{

                "Essentials",
                "Clothes",
                "Toiletries",
                "Others"
        };
        shoppingList = new ArrayList<PackingModel>();
        idTrip = getIntent().getStringExtra("idTripToChange");
        referencePackingList = FirebaseDatabase.getInstance().getReference("packingList");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        recyclerView = findViewById(R.id.shoppingRecyclerView);
        textOfItem = findViewById(R.id.text_item);
        relativeLayout = findViewById(R.id.relativeLayout);
        fab = findViewById(R.id.fab);
        numberOfItems = findViewById(R.id.numberOfShoppingItems);
        numberOfItems.setText("Items: " + 0);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiverCB,
                new IntentFilter("checkboxS"));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();
            }
        });

    }

    public BroadcastReceiver messageReceiverCB = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intentCheckBoxS) {
            // Get extra data included in the Intent
            String idItem = intentCheckBoxS.getStringExtra("idPckItem");
            Boolean isCheck = intentCheckBoxS.getBooleanExtra("isCheckS", false);

            // Toast.makeText(context, "isCheck: " + isCheck, Toast.LENGTH_SHORT).show();
            assert idItem != null;
            Map<String, Object> updates = new HashMap<>();

            updates.put("checkedS", isCheck);


            referencePackingList.child(uid).child("trips").child(idTrip).child(idItem).updateChildren(updates);
            //Toast.makeText(context, "updated idTrip: " + idTrip, Toast.LENGTH_SHORT).show();

        }
    };

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showDialog() {
        // final String[] categories = getApplicationContext().getResources().getStringArray(R.array.categories);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ShoppingList.this, R.style.AlertDialogTheme);
        final EditText input = new EditText(this);
        input.setHint("Enter Name");
        input.setHintTextColor(getResources().getColor(R.color.textHint));
        input.setTextColor(getResources().getColor(R.color.white));
        input.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        input.setLinkTextColor(getResources().getColor(R.color.white));
        input.setHighlightColor(getResources().getColor(R.color.white));

        builder.setTitle("Add item");

        builder.setIcon(R.drawable.ic_baseline_list_add_24);
        //builder.setMessage("Select Category");

        builder.setSingleChoiceItems(categories, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                v = null;
                v = categories[which].toString();


            }
        });
        builder.setView(input);
        //builder.setMessage("Enter Name");
        //builder.setView(input);
        builder.setBackground(getResources().getDrawable(R.drawable.background_dialog, null));
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                id = referencePackingList.push().getKey();
                // packingModel=null;


                if (!name.equals("") && v != null) {
                    PackingModel packingModel = new PackingModel(id, name, v, false, true, false);
                    assert id != null;
                    referencePackingList.child(uid).child("trips").child(idTrip).child(id).setValue(packingModel);


                    Snackbar.make(relativeLayout, "Item added", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    clearPackingItem();
                    dialog.dismiss();
                } else {
                    Snackbar.make(relativeLayout, "Select category or enter name of item!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiverCB);

    }

    public void onStart() {
        super.onStart();
        referencePackingList.child(uid).child("trips").child(idTrip).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingList.clear();
                numberOfShoppingItems = 0;
                for (DataSnapshot packingSnapshot : dataSnapshot.getChildren()) {
                    PackingModel packingModel = packingSnapshot.getValue(PackingModel.class);
                    if (packingModel.getToBuy()) {
                        shoppingList.add(packingModel);
                        numberOfShoppingItems = numberOfShoppingItems + 1;


                    }
                }
                numberOfItems.setText("Items: " + numberOfShoppingItems);
                PackingListAdapter myAdapter = new PackingListAdapter(ShoppingList.this, shoppingList, isPackingList);
                recyclerView.setLayoutManager(new LinearLayoutManager(ShoppingList.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(myAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void applyTexts(String name, String selection) {
        nameOfItem = name;
        selectedCategory = selection;
        PackingModel packingModel = null;

        if (nameOfItem != null && selectedCategory != null) {
            packingModel = new PackingModel(id, nameOfItem, selectedCategory, false, true, false);
            assert id != null;
            referencePackingList.child(uid).child("trips").child(idTrip).child(id).setValue(packingModel);

        }
        Snackbar.make(relativeLayout, "Item added to category " + packingModel.getCategory().toUpperCase(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        clearPackingItem();

    }

    private void clearPackingItem() {
        nameOfItem = null;
        categoryOfItem = null;
    }

}