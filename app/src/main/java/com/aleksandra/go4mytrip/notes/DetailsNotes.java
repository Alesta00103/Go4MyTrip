package com.aleksandra.go4mytrip.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.aleksandra.go4mytrip.trips.DetailTrip;
import com.aleksandra.go4mytrip.googlemap.DetailsMap;
import com.aleksandra.go4mytrip.R;
import com.aleksandra.go4mytrip.costestimate.CreateCostEstimate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class DetailsNotes extends AppCompatActivity implements NotesListener {

    Animation rotateOpen, rotateClose, fromBottom, toBottom;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fabMain, fab2, fab3;
    Boolean clicked = false;
    String titleNote, dateNote, textNote;
    List<NoteModel> noteList;
    FirebaseUser user;
    String uid;
    RecyclerView notesRecyclerView;
    String tripId;
    EditText inputSearch;
    String selectedNoteColor;
    String textLink;
    String selectedImagePath;
    ImageView backBtn;
    TextView numberNotes;
    LinearLayout noNotesView;
    DatabaseReference referenceNotes;

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_ADD_COST_ESTIMATE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;

    private int noteClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_notes);

        user = FirebaseAuth.getInstance().getCurrentUser();
        tripId = getIntent().getStringExtra("tripId");
        uid = user.getUid();

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        noteList = new ArrayList<>();
        inputSearch = findViewById(R.id.inputSearch);
        backBtn = findViewById(R.id.backBtn);
        numberNotes = findViewById(R.id.numberNotes);
        referenceNotes = FirebaseDatabase.getInstance().getReference("notes");
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_animation);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_animation);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_animation);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_animation);

        fabMain = findViewById(R.id.floatingButtonMain);
        fab2 = findViewById(R.id.floatingButtonAddLink);
        fab3 = findViewById(R.id.floatingButtonAddNote);
        noNotesView = findViewById(R.id.noNotesView);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabMainButtonClicked();

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateCostEstimate.class);
                intent.putExtra("tripId", tripId);
                startActivityForResult(intent, REQUEST_CODE_ADD_COST_ESTIMATE);

            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNote.class), REQUEST_CODE_ADD_NOTE);
            }
        });

        Log.d("DetailsNotes", "DetailsNotes are on create method");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.notes);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.notes:
                        return true;
                    case R.id.home:
                        Intent ih = new Intent(getApplicationContext(), DetailTrip.class);
                        ih.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ih);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.map:
                        Intent im = new Intent(getApplicationContext(), DetailsMap.class);
                        im.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (tripId != null) {
                            im.putExtra("tripId", tripId);
                        }
                        startActivity(im);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                referenceNotes.child(uid).child(tripId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        noteList.clear();
                        for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                            NoteModel note = noteSnapshot.getValue(NoteModel.class);
                            assert note != null;
                            if ((note.getNoteTitle().toLowerCase().contains(inputSearch.getText().toString().toLowerCase())) || (note.getTextNote().toLowerCase().contains(inputSearch.getText().toString().toLowerCase()))) {
                                noteList.add(note);
                            }

                        }


                        NotesAdapter myAdapter = new NotesAdapter(DetailsNotes.this, noteList, DetailsNotes.this);
                        notesRecyclerView.setLayoutManager(new GridLayoutManager(DetailsNotes.this, 2));
                        notesRecyclerView.setHasFixedSize(true);
                        notesRecyclerView.setAdapter(myAdapter);
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

    }

    private void onFabMainButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
    }


    @Override
    protected void onStart() {
        super.onStart();
        referenceNotes.child(uid).child(tripId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                        NoteModel note = noteSnapshot.getValue(NoteModel.class);
                        noteList.add(note);
                    }
                    notesRecyclerView.setVisibility(View.VISIBLE);
                    noNotesView.setVisibility(View.GONE);
                    String textAmountNotes = noteList.size() + " notes";
                    numberNotes.setText(textAmountNotes);

                    NotesAdapter myAdapter = new NotesAdapter(DetailsNotes.this, noteList, DetailsNotes.this);
                    notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    notesRecyclerView.setHasFixedSize(true);
                    notesRecyclerView.setAdapter(myAdapter);
                } else {
                    notesRecyclerView.setVisibility(View.GONE);
                    noNotesView.setVisibility(View.VISIBLE);
                    String textAmountNotes = noteList.size() + " notes";
                    numberNotes.setText(textAmountNotes);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void setVisibility(Boolean clicked) {
        if (!clicked) {
            fab2.setVisibility(View.VISIBLE);
            fab3.setVisibility(View.VISIBLE);
        } else {
            fab2.setVisibility(View.INVISIBLE);
            fab3.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(Boolean clicked) {
        if (!clicked) {
            fab2.startAnimation(fromBottom);
            fab3.startAnimation(fromBottom);
            fabMain.startAnimation(rotateOpen);
        } else {
            fab2.startAnimation(toBottom);
            fab3.startAnimation(toBottom);
            fabMain.startAnimation(rotateClose);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DetailsNotes", "DetailsNotes are on resume method");
        bottomNavigationView.setSelectedItemId(R.id.notes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                titleNote = data.getStringExtra("title");
                assert titleNote != null;
                if (titleNote.trim().isEmpty()) {
                    titleNote = "No title";
                }
                String id;
                dateNote = data.getStringExtra("date");
                textNote = data.getStringExtra("noteText");
                selectedNoteColor = data.getStringExtra("color");
                textLink = data.getStringExtra("link");
                selectedImagePath = data.getStringExtra("image");

                id = referenceNotes.push().getKey();
                if (textNote != null) {
                    NoteModel note = new NoteModel(id, titleNote, textNote, dateNote, selectedNoteColor, textLink, selectedImagePath);
                    assert id != null;
                    referenceNotes.child(uid).child(tripId).child(id).setValue(note);

                    Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Note not added", Toast.LENGTH_SHORT).show();
                }


                clearNoteCard();

            }
        }
        else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                String id = data.getStringExtra("id");

                if (data.getBooleanExtra("isNoteDeleted", false)) {
                    assert id != null;
                    referenceNotes.child(uid).child(tripId).child(id).removeValue();

                    Toast.makeText(this, "Deleted Note", Toast.LENGTH_LONG).show();
                } else {
                    titleNote = data.getStringExtra("title");
                    if (titleNote.trim().isEmpty()) {
                        titleNote = "No title";
                    }

                    dateNote = data.getStringExtra("date");
                    textNote = data.getStringExtra("noteText");
                    selectedNoteColor = data.getStringExtra("color");
                    textLink = data.getStringExtra("link");
                    selectedImagePath = data.getStringExtra("image");
                    if (textNote != null) {
                        Map<String, Object> editItem = new HashMap<>();
                        editItem.put("textNote", textNote);
                        editItem.put("noteTitle", titleNote);
                        editItem.put("noteColor", selectedNoteColor);
                        editItem.put("image", selectedImagePath);
                        editItem.put("link", textLink);


                        assert id != null;
                        referenceNotes.child(uid).child(tripId).child(id).updateChildren(editItem);

                        Toast.makeText(this, "Note was edited", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Note was not edited", Toast.LENGTH_SHORT).show();
                    }

                }
                clearNoteCard();

            }

        }

    }

    private void clearNoteCard() {
        titleNote = null;
        dateNote = null;
        textNote = null;
        selectedNoteColor = null;

    }


    @Override
    public void onNoteClicked(NoteModel note, int position) {
        noteClickedPosition = position;
        String id = note.getIdNote();
        String title = note.getNoteTitle();
        String text = note.getTextNote();
        String date = note.getDateTime();
        String color = note.getNoteColor();
        String link = note.getLink();
        String image = note.getImage();

        Intent intent = new Intent(DetailsNotes.this, CreateNote.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        intent.putExtra("date", date);
        intent.putExtra("color", color);
        intent.putExtra("link", link);
        intent.putExtra("image", image);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }
}
