package com.aleksandra.go4mytrip;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DetailTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, PlaceListener {
    private TextView title;
    private TextView dateS;
    private TextView dateE;
    private ImageButton image;
    private int imageInt;
    private TextView counter;
    Button packingListBtn;
    Button shoppingListBtn;
    String dateStart;
    String dateStartOnly;
    String currentDateOnly;
    String dateEnd;
    String tripId;
    String timeStart, timeEnd;
    Date dateOfStartTrip;
    Date datee2end;
    Date datenow;
    ImageView addPlace;
    ImageView noPlaces;
    TextView noPlacesText;
    long end_millis;
    long timeMilli;
    long start_millis;
    ImageView backBtn;
    private static final String TAG = "DetailTrip";
    public static final int REQUEST_CODE_ADD_PLACE = 1;
    BottomNavigationView bottomNavigationView;
    DatabaseReference mapReference;
    DatabaseReference referenceTrips;
    FirebaseUser user;
    String uid;
    List<PlaceModel> placeList;
    RecyclerView recyclerView;
    private int placeClickedPosition = -1;
    int hour, minute;
    int hourFinal, minuteFinal;
    String currentDateString;
    String currentTimeString;
    String idOfClickedPlace;
    String dateAndTimeString;
    Date dateAndTimeDate;
    long dateAndTimeMillis;
    Calendar calendarS;

    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_trip);
        createNotificationChannel();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        backBtn = findViewById(R.id.backBtn);
        addPlace = findViewById(R.id.addPlace);
        referenceTrips = FirebaseDatabase.getInstance().getReference("trips");

        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailTrip.this, DetailsMap.class);
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.notes:

                        Intent in = new Intent(getApplicationContext(), DetailsNotes.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        in.putExtra("tripId", tripId);
                        startActivity(in);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.home:
                        return true;

                    case R.id.map:
                        Intent im = new Intent(getApplicationContext(), DetailsMap.class);
                        im.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        im.putExtra("tripId", tripId);
                        startActivity(im);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        title = findViewById(R.id.nameTrip);
        dateS = findViewById(R.id.startDate);
        dateE = findViewById(R.id.endDate);
        image = findViewById(R.id.imagexTrip);
        counter = findViewById(R.id.counterDown);
        packingListBtn = findViewById(R.id.btn_packingList);
        shoppingListBtn = findViewById(R.id.btn_shoppingList);
        mapReference = FirebaseDatabase.getInstance().getReference("placesToVisit");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        placeList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview_id);
        noPlaces = findViewById(R.id.noPlaces);
        noPlacesText = findViewById(R.id.noPlacesText);


        tripId = getIntent().getStringExtra("idTripToDetail");
        // idOfThisTrip.setText(tripId);
        title.setText(getIntent().getStringExtra("title"));
        dateS.setText(getIntent().getStringExtra("dateS"));
        dateStart = getIntent().getStringExtra("dateS");
        dateE.setText(getIntent().getStringExtra("dateE"));
        dateEnd = getIntent().getStringExtra("dateE");
        timeStart = getIntent().getStringExtra("timeStart");
        timeEnd = getIntent().getStringExtra("timeEnd");
        imageInt = getIntent().getIntExtra("image", 1);
        image.setImageResource(imageInt);
        dateStart = dateStart.replace('.', '/');
        dateEnd = dateEnd.replace('.', '/');



        dateStartOnly = dateStart;
        dateStart = dateStart + " " + timeStart + ":00";
        dateEnd = dateEnd + " " + timeEnd + ":00"; // data zakończania wyjazdu

        Date current = new Date();
        currentDateOnly = simpleDateFormat.format(current);

        dateOfStartTrip = new Date();
        try {
            dateOfStartTrip = sdf.parse(dateStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert dateOfStartTrip != null;
        start_millis = dateOfStartTrip.getTime(); //start wyjazdu w ms

        datee2end = new Date();
        try {
            datee2end = sdf.parse(dateEnd);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert datee2end != null;
        end_millis = datee2end.getTime(); // data zakonczenia wyjazdu w ms

        datenow = new Date();
        String newdate = sdf.format(datenow);
        try {
            datenow = sdf.parse(newdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        timeMilli = System.currentTimeMillis(); // aktualny czas w ms
        countDownTimer(start_millis - timeMilli); // czas do wyjazdu w ms

        packingListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailTrip.this, PackingList.class);
                intent.putExtra("idTripToChange", tripId);
                startActivity(intent);
            }
        });

        shoppingListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailTrip.this, ShoppingList.class);
                intent.putExtra("idTripToChange", tripId);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tripId = getIntent().getStringExtra("idTripToDetail");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mapReference.child(uid).child(tripId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placeList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                        PlaceModel placeModel = placeSnapshot.getValue(PlaceModel.class);
                        placeList.add(placeModel);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    noPlaces.setVisibility(View.GONE);
                    noPlacesText.setVisibility(View.GONE);


                    PlaceAdapter myAdapter = new PlaceAdapter(DetailTrip.this, placeList, DetailTrip.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(DetailTrip.this));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(myAdapter);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noPlaces.setVisibility(View.VISIBLE);
                    noPlacesText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void countDownTimer(final long total_millis) { // total_millis to czas do wyjazdu w ms
        // 1000- tick every 1 second
        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
//            long totall__millis = total_millis;

            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                if (days == 1) {
                    String s1 = "\nday";
                    String s = days + s1;

                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1.5f), 0, 1, 0);
                    counter.setText(ss1);


                } else if (days > 1) {
                    String s1 = "\ndays";
                    String s = days + s1;
                    // String s = String.valueOf(days + s1);

                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1.5f), 0, 2, 0);
                    counter.setText(ss1);

                } else if (days == 0 && hours > 1) {
                    String s1 = "\nhours";
                    String s = hours + s1;

                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1.5f), 0, 2, 0);
                    counter.setText(ss1);

                } else if (days == 0 && hours == 1) {
                    String s1 = "\nhour";
                    String s = hours + s1;

                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1.5f), 0, 2, 0);
                    counter.setText(ss1);

                } else if (days == 0 && hours == 0 && minutes > 1) {
                    String s1 = "\nminutes";
                    String s = minutes + s1;

                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1.5f), 0, 2, 0);
                    counter.setText(ss1);

                } else if (days == 0 && hours == 0 && minutes == 1) {
                    String s1 = "\nminute";
                    String s = minutes + s1;

                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1.5f), 0, 2, 0);
                    counter.setText(ss1);

                } else if (days == 0 && hours == 0 && minutes == 0 && seconds > 0) {
                    String s1 = "\nseconds";
                    String s2 = "\nsecond";
                    String s = null;
                    if (seconds > 1) {
                        s = seconds + s1;
                    } else if (seconds == 1) {
                        s = seconds + s2;
                    }

                    SpannableString ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(1.5f), 0, 2, 0);
                    counter.setText(ss1);

                }

            }

            @Override
            public void onFinish() {

                long milliseconds = end_millis - timeMilli;// czas od teraz do zakończenia wyjazdu w ms

                if (milliseconds > 0) {
                    counter.setText("During\nThe\nTrip");
                    counter.setGravity(Gravity.CENTER);
                    counter.setTextSize(11);
                    counter.layout(0, 0, 0, 0);
                } else {
                    counter.setText("After\nTrip");
                    counter.setTextSize(15);
                    counter.setGravity(Gravity.CENTER);
                    counter.layout(0, 0, 0, 0);
                }

            }
        };
        cdt.start();
    }

    public void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendarS = Calendar.getInstance();
        calendarS.set(Calendar.YEAR, year);
        calendarS.set(Calendar.MONTH, month);
        calendarS.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        currentDateString = simpleDateFormat.format(calendarS.getTime());
        currentDateString = currentDateString.replace("/", ".");
        //dateOfTripStart.setText(currentDateString);
        hour = calendarS.get(Calendar.HOUR_OF_DAY);
        minute = calendarS.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(DetailTrip.this, R.style.TimePickerTheme, DetailTrip.this, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;


        //dateOfTripStart.setText(currentDateString + " " + hourFinal +":" + minuteFinal);
        currentTimeString = hourFinal + ":" + minuteFinal;
        updateItem();
    }

    private void updateItem() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Map<String, Object> setEditTime = new HashMap<>();
        setEditTime.put("date", currentDateString);
        setEditTime.put("time", currentTimeString);


        mapReference.child(uid).child(tripId).child(idOfClickedPlace).updateChildren(setEditTime);
        currentDateString = currentDateString.replace('.', '/');
        dateAndTimeString = currentDateString.trim() + " " + currentTimeString.trim() + ":00";

        //Toast.makeText(this, "Date: " + dateAndTimeString, Toast.LENGTH_SHORT).show();


        try {
            dateAndTimeDate = sdf.parse(dateAndTimeString.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "Date: " + dateAndTimeDate, Toast.LENGTH_SHORT).show();

        assert dateAndTimeDate != null;
        dateAndTimeMillis = dateAndTimeDate.getTime();


        setReminder();
    }

    @Override
    public void onLongClicked(PlaceModel placeModel, int position) {
        placeClickedPosition = position;
        idOfClickedPlace = placeModel.getId();

        DialogFragment dialogFragment = new com.aleksandra.go4mytrip.DatePicker();
        dialogFragment.show(getSupportFragmentManager(), "date picker");

    }

    @Override
    public void onClickDelete(PlaceModel placeModel, int position) {
        placeClickedPosition = position;
        idOfClickedPlace = placeModel.getId();

        mapReference.child(uid).child(tripId).child(idOfClickedPlace).removeValue();

    }

    public void setReminder() {
        Intent intent = new Intent(DetailTrip.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailTrip.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long currentTimeInMillis = System.currentTimeMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP, dateAndTimeMillis-3600000, pendingIntent); //dateAndTimeMillis - currentTimeInMillis

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReminderChannel";
            String description = "Channel for reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyPlace", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}