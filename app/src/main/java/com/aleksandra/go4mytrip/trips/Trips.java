package com.aleksandra.go4mytrip.trips;


import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aleksandra.go4mytrip.login.MainActivity;
import com.aleksandra.go4mytrip.lists.PackingModel;
import com.aleksandra.go4mytrip.R;
import com.aleksandra.go4mytrip.login.User;
import com.aleksandra.go4mytrip.costestimate.CostEstimateModel;
import com.aleksandra.go4mytrip.notes.NoteModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Trips extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, TripsListener {
    TextView nameAndSurname;
    TextView emailData;
    ImageView logoutBtn;
    ImageView image2;
    String email;
    String uriPhoto;
    String userName;
    FloatingActionButton addTripBtn;
    List<TripModel> tripList;
    List<CostEstimateModel> costEstimatesList;
    List<NoteModel> noteList;
    List<PackingModel> packingLists;
    DatabaseReference referenceTrips;
    DatabaseReference referenceUser;
    DatabaseReference referencePackingList;
    DatabaseReference referenceExpenses;
    DatabaseReference referenceNotes;
    DatabaseReference referencePlaces;
    FirebaseUser user;
    String uid;
    RecyclerView mrv;
    String nameOfTrip;
    int imageUriOfTrip;
    String dateStart;
    String dateEnd;
    TripModel deletedTrip = null;
    EditText inputSearch;
    ImageView noTrips;
    TextView noTripsText;
    private int tripClickedPosition = -1;
    public static final int REQUEST_CODE_UPDATE_TRIP = 2;
    public static final int REQUEST_CODE_ADD_TRIP = 1;

    int[] images = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5, R.drawable.img6, R.drawable.img7,
            R.drawable.img8, R.drawable.img9, R.drawable.img10, R.drawable.img11, R.drawable.img12, R.drawable.img13, R.drawable.img14,
            R.drawable.img15, R.drawable.img16, R.drawable.img17, R.drawable.img18, R.drawable.img19, R.drawable.img20,
            R.drawable.img21, R.drawable.img22, R.drawable.img24, R.drawable.img25};

    public static final int REQUEST_CODE = 1;
    GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

       /* nameAndSurname = findViewById(R.id.personData);
        emailData = findViewById(R.id.personEmail);*/
        image2 = findViewById(R.id.image2);
        addTripBtn = findViewById(R.id.circleButton);
        mrv = findViewById(R.id.recyclerview_id);
        tripList = new ArrayList<>();
        costEstimatesList = new ArrayList<>();
        noteList = new ArrayList<>();
        packingLists = new ArrayList<>();
        logoutBtn = findViewById(R.id.logout_google);
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        uid = user.getUid();
        referenceTrips = FirebaseDatabase.getInstance().getReference("trips");
        referenceUser = FirebaseDatabase.getInstance().getReference("users");
        referencePackingList = FirebaseDatabase.getInstance().getReference("packingList");
        referenceExpenses = FirebaseDatabase.getInstance().getReference("expenses");
        referenceNotes = FirebaseDatabase.getInstance().getReference("notes");
        referencePlaces = FirebaseDatabase.getInstance().getReference("placesToVisit");
        inputSearch = findViewById(R.id.inputSearch);
        noTrips = findViewById(R.id.noTrips);
        noTripsText = findViewById(R.id.noTripsText);

       /* LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-id"));*/


       /* nameAndSurname.setText(getIntent().getStringExtra("name"));
        emailData.setText(email);
        uriPhoto = getIntent().getStringExtra("photo");
        Picasso.get().load(uriPhoto).into(image2);*/

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                referenceTrips.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tripList.clear();
                        for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                            TripModel trip = tripSnapshot.getValue(TripModel.class);
                            if (trip.getTitle().toLowerCase().contains(inputSearch.getText().toString().toLowerCase())) {
                                tripList.add(trip);
                            }

                        }

                        RecyclerTripAdapter myAdapter = new RecyclerTripAdapter(Trips.this, tripList, Trips.this);
                        mrv.setLayoutManager(new LinearLayoutManager(Trips.this));
                        mrv.setHasFixedSize(true);
                        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mrv);
                        mrv.setAdapter(myAdapter);

               /* mrecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Toast.makeText(Trips.this, "position: "+ position, Toast.LENGTH_SHORT).show();
                       // referenceTrips.child(uid).child(position)
                    }
                });*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), AddNewTrip.class), REQUEST_CODE_ADD_TRIP);

            }
        });
        referenceTrips.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripList.clear();
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    TripModel trip = tripSnapshot.getValue(TripModel.class);
                    tripList.add(trip);
                    if (tripList.size() == 0) {
                        mrv.setVisibility(View.GONE);
                        noTrips.setVisibility(View.VISIBLE);
                        noTripsText.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_TRIP) {
            if (resultCode == RESULT_OK) {

                String timeStart, timeEnd;
                nameOfTrip = data.getStringExtra("nameTrip");
                dateStart = data.getStringExtra("date");
                String namePlace = data.getStringExtra("namePlace");
                String coordinate = data.getStringExtra("coordinates");
                if (data.getStringExtra("timeStart") != null) {
                    timeStart = data.getStringExtra("timeStart");
                } else {
                    timeStart = "00:00:00";
                }
                if (data.getStringExtra("timeEnd") != null) {
                    timeEnd = data.getStringExtra("timeEnd");
                } else {
                    timeEnd = "00:00:00";
                }

                dateEnd = data.getStringExtra("endDate");
                imageUriOfTrip = randomImage();

                String id = referenceTrips.push().getKey();

                if (nameOfTrip != null && imageUriOfTrip != 0 && dateStart != null && dateEnd != null) {
                    TripModel trip = new TripModel(id, nameOfTrip, namePlace, coordinate, imageUriOfTrip, dateStart, dateEnd, timeStart, timeEnd);
                    assert id != null;
                    referenceTrips.child(uid).child(id).setValue(trip);

                    Toast.makeText(this, "Trip added", Toast.LENGTH_SHORT).show();


                    clearTripCard();
                } else {
                    Toast.makeText(Trips.this, "Some data is empty, trip was not added", Toast.LENGTH_SHORT).show();
                    clearTripCard();
                }
            }
        } else if (requestCode == REQUEST_CODE_UPDATE_TRIP) {
            if (resultCode == RESULT_OK) {

                String timeStart, timeEnd;
                String id = data.getStringExtra("id");
                nameOfTrip = data.getStringExtra("nameTrip");
                dateStart = data.getStringExtra("date");
                String namePlace = data.getStringExtra("namePlace");
                String coordinate = data.getStringExtra("coordinates");
                if (data.getStringExtra("timeStart") != null) {
                    timeStart = data.getStringExtra("timeStart");
                } else {
                    timeStart = "00:00:00";
                }
                if (data.getStringExtra("timeEnd") != null) {
                    timeEnd = data.getStringExtra("timeEnd");
                } else {
                    timeEnd = "00:00:00";
                }

                dateEnd = data.getStringExtra("endDate");


                Map<String, Object> editItem = new HashMap<>();
                editItem.put("title", nameOfTrip);
                editItem.put("tripDate", dateStart);
                editItem.put("tripDateEnd", dateEnd);
                editItem.put("coordinate", coordinate);
                editItem.put("namePlace", namePlace);
                editItem.put("timeStart", timeStart);
                editItem.put("timeEnd", timeEnd);

                assert id != null;
                referenceTrips.child(uid).child(id).updateChildren(editItem);

            }

        }

    }

    private int randomImage() {

        Random rand = new Random();
        int choice = images[rand.nextInt(images.length)];

        return choice;
    }


    private void clearTripCard() {
        nameOfTrip = null;
        imageUriOfTrip = 0;
        dateStart = null;
        dateEnd = null;
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();

        referenceUser.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user != null) {
                    email = user.getEmail();
                    userName = user.getName();
                    uriPhoto = user.getImageUser();
                    Picasso.get().load(uriPhoto).into(image2);
                }

               /* nameAndSurname.setText(userName);
                emailData.setText(email);*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referenceTrips.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                        TripModel trip = tripSnapshot.getValue(TripModel.class);
                        tripList.add(trip);
                        if (tripList.size() > 0) {
                            mrv.setVisibility(View.VISIBLE);
                            noTrips.setVisibility(View.GONE);
                            noTripsText.setVisibility(View.GONE);
                        }

                    }

                    RecyclerTripAdapter myAdapter = new RecyclerTripAdapter(Trips.this, tripList, Trips.this);
                    mrv.setLayoutManager(new LinearLayoutManager(Trips.this));
                    mrv.setHasFixedSize(true);
                    new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mrv);
                    mrv.setAdapter(myAdapter);

                } else {
                    mrv.setVisibility(View.GONE);
                    noTrips.setVisibility(View.VISIBLE);
                    noTripsText.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                FirebaseAuth.getInstance().signOut();
                                startActivity(i);
                                finish();

                            }
                        });

        }
        return true;
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            final TripModel trip = tripList.get(position);
            deletedTrip = trip;
            final String id = trip.getTripId();
            referenceTrips.child(uid).child(id).removeValue();


            Snackbar.make(mrv, "Deleted trip " + deletedTrip.getTitle(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            referenceTrips.child(uid).child(id).setValue(deletedTrip);

                        }
                    }).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    referenceTrips.child(uid).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                referenceExpenses.child(uid).child(id).removeValue();
                                referenceNotes.child(uid).child(id).removeValue();
                                referencePackingList.child(uid).child("trips").child(id).removeValue();
                                referencePlaces.child(uid).child(id).removeValue();
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }, 8000);   //8 seconds
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(Trips.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(Trips.this, R.color.white))
                    .addActionIcon(R.drawable.ic_baseline_delete_24_blue)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onLongClicked(TripModel tripModel, int position) {
        tripClickedPosition = position;
        String id = tripModel.getTripId();
        String title = tripModel.getTitle();
        String coordinate = tripModel.getCoordinate();
        String tripDate = tripModel.getTripDate();
        String timeStart = tripModel.getTimeStart();
        String timeEnd = tripModel.getTimeEnd();
        int image = tripModel.getImageTrip();
        String tripDateEnd = tripModel.getTripDateEnd();
        String namePlace = tripModel.getNamePlace();

        Intent intent = new Intent(Trips.this, AddNewTrip.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("coordinate", coordinate);
        intent.putExtra("tripDate", tripDate);
        intent.putExtra("timeStart", timeStart);
        intent.putExtra("timeEnd", timeEnd);
        intent.putExtra("image", image);
        intent.putExtra("tripDateEnd", tripDateEnd);
        intent.putExtra("namePlace", namePlace);

        startActivityForResult(intent, REQUEST_CODE_UPDATE_TRIP);
    }
}