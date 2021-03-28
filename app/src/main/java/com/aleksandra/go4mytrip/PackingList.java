package com.aleksandra.go4mytrip;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.aleksandra.go4mytrip.ui.main.SectionsPagerAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackingList extends AppCompatActivity implements AddItemDialog.AddItemDialogListener {
    List<PackingModel> packingList;

    FirebaseUser user;
    String uid;
    String idTrip;
    DatabaseReference referencePackingList;

    String id;
    String nameOfItem;
    String categoryOfItem;
    String selectedCategory;
    CoordinatorLayout mainLayout;

    CharSequence[] categories;
    ImageView backBtn;
    String v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packing_list);

        //---------------------------------------------------------
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        backBtn = findViewById(R.id.backBtn);


        categories = new CharSequence[]{

                "Essentials",
                "Clothes",
                "Toiletries",
                "Others"
        };
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();
            }
        });
        //---------------------------------------------------------
        packingList = new ArrayList<>();
        idTrip = getIntent().getStringExtra("idTripToChange");
        referencePackingList = FirebaseDatabase.getInstance().getReference("packingList");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        mainLayout = findViewById(R.id.mainLayout);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("delete-item"));
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiverCB,
                new IntentFilter("checkbox"));
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiverBP,
                new IntentFilter("toBuy"));

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intentPackingItemDelete) {
            // Get extra data included in the Intent
            String idPackingItemToDelete = intentPackingItemDelete.getStringExtra("idPackingItemDelete");

            Toast.makeText(PackingList.this, "Deleted item", Toast.LENGTH_SHORT).show();
            assert idPackingItemToDelete != null;
            referencePackingList.child(uid).child("trips").child(idTrip).child(idPackingItemToDelete).removeValue();

        }
    };

    public BroadcastReceiver messageReceiverCB = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intentCheckBox) {
            // Get extra data included in the Intent
            String idItem = intentCheckBox.getStringExtra("idPckItem");
            Boolean isCheck = intentCheckBox.getBooleanExtra("isCheck", false);
           /* String nameItem = intentCheckBox.getStringExtra("name");
            String categoryItem = intentCheckBox.getStringExtra("category");*/
            //  registerReceiver(messageReceiverCB, );
            // unregisterReceiver(messageReceiverCB);

            //Toast.makeText(context, "isCheck: " + isCheck, Toast.LENGTH_SHORT).show();
            assert idItem != null;
            Map<String, Object> updates = new HashMap<>();

            updates.put("checked", isCheck);
//etc
            // PackingModel packingModel = new PackingModel(idItem, nameItem, categoryItem, isCheck );
            referencePackingList.child(uid).child("trips").child(idTrip).child(idItem).updateChildren(updates);
            // Toast.makeText(context, "updated idTrip: " + idTrip, Toast.LENGTH_SHORT).show();


        }
    };
    public BroadcastReceiver messageReceiverBP = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intentToBuy) {
            // Get extra data included in the Intent
            String idItem = intentToBuy.getStringExtra("idPckItem");
            Boolean isPressed = intentToBuy.getBooleanExtra("isPressed", false);
           /* String nameItem = intentCheckBox.getStringExtra("name");
            String categoryItem = intentCheckBox.getStringExtra("category");*/
            //  registerReceiver(messageReceiverCB, );
            // unregisterReceiver(messageReceiverCB);

            //Toast.makeText(context, "isPressed: " + isPressed, Toast.LENGTH_SHORT).show();
            assert idItem != null;
            Map<String, Object> updates = new HashMap<>();

            updates.put("toBuy", isPressed);
//etc
            // PackingModel packingModel = new PackingModel(idItem, nameItem, categoryItem, isCheck );
            referencePackingList.child(uid).child("trips").child(idTrip).child(idItem).updateChildren(updates);
            //Toast.makeText(context, "updated idTrip: " + idTrip, Toast.LENGTH_SHORT).show();

        }
    };

    private void showDialog() {
        // final String[] categories = getApplicationContext().getResources().getStringArray(R.array.categories);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PackingList.this, R.style.AlertDialogTheme);
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
                    PackingModel packingModel = new PackingModel(id, name, v, false, false, false);
                    assert id != null;
                    referencePackingList.child(uid).child("trips").child(idTrip).child(id).setValue(packingModel);


                    Snackbar.make(mainLayout, "Item added to category " + packingModel.getCategory().toUpperCase(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    clearPackingItem();
                    dialog.dismiss();
                } else {
                    Snackbar.make(mainLayout, "Select category or enter name of item!", Snackbar.LENGTH_SHORT)
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

    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(messageReceiverCB, filter);
    }


    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiverCB);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiverBP);

    }

    @Override
    public void applyTexts(String name, String selection) {
        nameOfItem = name;
        selectedCategory = selection;
        PackingModel packingModel = null;

        if (nameOfItem != null && selectedCategory != null) {
            packingModel = new PackingModel(id, nameOfItem, selectedCategory, false, false, false);
            assert id != null;
            referencePackingList.child(uid).child("trips").child(idTrip).child(id).setValue(packingModel);

        }
        Snackbar.make(mainLayout, "Item added to category " + packingModel.getCategory().toUpperCase(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        clearPackingItem();


    }

    private void clearPackingItem() {
        nameOfItem = null;
        categoryOfItem = null;
    }


}