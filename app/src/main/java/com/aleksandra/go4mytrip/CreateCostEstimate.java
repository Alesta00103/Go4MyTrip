package com.aleksandra.go4mytrip;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateCostEstimate extends AppCompatActivity implements CostEstimateListener {
    ImageView imageBack, addBtn;
    AlertDialog dialogAddItem;
    FirebaseUser user;
    String uid;
    RecyclerView costEstimateRecyclerView;
    DatabaseReference referenceExpenses;
    List<CostEstimateModel> costEstimatesList;
    String idOfClickedCost;
    String tripId;
    TextView totalCost;
    EditText inputName;
    EditText inputCost;

    String titleCost;
    String dateCost;
    private int costClickedPosition = -1;
    int totalSum;
    int amountCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cost_estimate);

        imageBack = findViewById(R.id.backBtn);

        addBtn = findViewById(R.id.addBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        tripId = getIntent().getStringExtra("tripId");
        referenceExpenses = FirebaseDatabase.getInstance().getReference("expenses");
        uid = user.getUid();
        costEstimatesList = new ArrayList<>();
        costEstimateRecyclerView = findViewById(R.id.expenseRecyclerView);
        totalCost = findViewById(R.id.showTotalCost);


        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        referenceExpenses.child(uid).child(tripId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                costEstimatesList.clear();
                totalSum = 0;
                for (DataSnapshot costSnapshot : dataSnapshot.getChildren()) {
                    CostEstimateModel costEstimate = costSnapshot.getValue(CostEstimateModel.class);
                    assert costEstimate != null;
                    int cost = costEstimate.getAmount();
                    totalSum = totalSum + cost;
                    costEstimatesList.add(costEstimate);
                }
                totalCost.setText(String.valueOf(totalSum));

                CostEstimateAdapter myAdapter = new CostEstimateAdapter(CreateCostEstimate.this, costEstimatesList, CreateCostEstimate.this);
                costEstimateRecyclerView.setLayoutManager(new LinearLayoutManager(CreateCostEstimate.this));
                costEstimateRecyclerView.setHasFixedSize(true);
                costEstimateRecyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void showAddItemDialog() {
        if (dialogAddItem == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateCostEstimate.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.create_cost_estimate_layout,
                    (ViewGroup) findViewById(R.id.layoutAddExpenseContainer)
            );
            builder.setView(view);

            dialogAddItem = builder.create();
            if (dialogAddItem.getWindow() != null) {
                dialogAddItem.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            inputName = view.findViewById(R.id.inputNameExpanse);

            inputName.requestFocus();



            inputCost = view.findViewById(R.id.inputAmount);

            inputCost.requestFocus();





            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputName.getText().toString().trim().isEmpty() || inputCost.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateCostEstimate.this, "Enter name and amount", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //if(!isEdit){
                            String id = referenceExpenses.push().getKey();
                            String title = inputName.getText().toString();
                            String stringAmount = inputCost.getText().toString();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm a", Locale.getDefault());
                            String noteDate = sdf.format(new Date());
                            int amount = Integer.parseInt(stringAmount);

                            CostEstimateModel costEstimate = new CostEstimateModel(id, title, noteDate, amount);
                            assert id != null;
                            referenceExpenses.child(uid).child(tripId).child(id).setValue(costEstimate);

                            Toast.makeText(CreateCostEstimate.this, "Expense added", Toast.LENGTH_SHORT).show();
                            inputName.setText("");
                            inputCost.setText("");


                            dialogAddItem.dismiss();
                        //}
//                        else{
//
//                            String title = inputName.getText().toString();
//                            String stringAmount = inputCost.getText().toString();
//                            int amount = Integer.parseInt(stringAmount);
//
//                            Map<String, Object> setEditCost= new HashMap<>();
//                            setEditCost.put("title", title);
//                            setEditCost.put("amount", amount);
//
//                            referenceExpenses.child(uid).child(tripId).child(idOfClickedCost).updateChildren(setEditCost);
//                            Toast.makeText(CreateCostEstimate.this, "Expense updated", Toast.LENGTH_SHORT).show();
//                            inputName.setText("");
//                            inputCost.setText("");
//                            isEdit=false;
//                            dialogAddItem.dismiss();
//
//
//                        }

                    }
//                    clear();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddItem.dismiss();
                }
            });
        }
        dialogAddItem.show();
    }

    @Override
    public void onDeleteClicked(CostEstimateModel costEstimateModel, int position) {
        String idOfDeletedCost = costEstimateModel.getId();
        referenceExpenses.child(uid).child(tripId).child(idOfDeletedCost).removeValue();
    }

//    @Override
//    public void onEditClicked(CostEstimateModel costEstimateModel, int position) {
//        costClickedPosition =position;
//        idOfClickedCost = costEstimateModel.getId();
//        titleCost = costEstimateModel.getTitle();
//        dateCost = costEstimateModel.getDate();
//        amountCost = costEstimateModel.getAmount();
//        isEdit = true;
//        showAddItemDialog();
//
//    }
//    public void clear(){
//        idOfClickedCost = null;
//        titleCost = null;
//        dateCost = null;
//        amountCost = 0;
//        isEdit = false;
//    }
}