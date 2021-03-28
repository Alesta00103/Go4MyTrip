package com.aleksandra.go4mytrip;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddNewTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText tripName;
    ImageView addNewTripBtn;
    ImageView imageBack;
    Button setDateStart;
    Button setDateEnd;
    TextView endDate;
    Button addNewPlace;
    TextView dateOfTripStart;
    String dateOfTripEnd;
    String currentDateString, endDateString;
    String dateS;
    String dateE;
    TextView placeName;
    String coordinates;
    String placeNameText;
    LinearLayout layoutAddImage;
    String timeS;
    String timeE;
    String id;
    TripModel alreadyAvailableTrip;
    int image;
    Calendar calendarS, calendarE;
    Date date1, date2;

    Boolean isDataStart = false;

    int hour, minute, hour2, minute2;
    int hourFinal, minuteFinal, hourFinal2, minuteFinal2;

    static final int REQUEST_CODE_ADD_NAME_PLACE = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 2;
    private static final int REQUEST_CODE_SELECT_IMAGE = 3;
    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);

        tripName = findViewById(R.id.editTripName);
        addNewTripBtn = findViewById(R.id.saveTrip);
        setDateStart = findViewById(R.id.setDateTimeStart);
        setDateEnd = findViewById(R.id.setTimeStart);
        endDate = findViewById(R.id.timeStart);
        dateOfTripStart = findViewById(R.id.dataTimeStart);
        addNewPlace = findViewById(R.id.setPlace);
        placeName = findViewById(R.id.placeName);
        layoutAddImage = findViewById(R.id.layoutAddImage);
        imageBack = findViewById(R.id.backBtn);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            id = getIntent().getStringExtra("id");
            String title = getIntent().getStringExtra("title");
            String namePlace = getIntent().getStringExtra("namePlace");
            String coordinate = getIntent().getStringExtra("coordinate");
            String tripDate = getIntent().getStringExtra("tripDate");
            String tripDateEnd = getIntent().getStringExtra("tripDateEnd");
            String timeStart = getIntent().getStringExtra("timeStart");
            String timeEnd = getIntent().getStringExtra("timeEnd");
            image = getIntent().getIntExtra("image", 0);
            TripModel tripModel = new TripModel(id, title, namePlace, coordinate, image, tripDate, tripDateEnd, timeStart, timeEnd);
            alreadyAvailableTrip = tripModel;
            setViewOrUpdateTrip();
        }

        addNewPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewTrip.this, DetailsMap.class);
                intent.putExtra("setOnlyPlace", true);
                startActivityForResult(intent, REQUEST_CODE_ADD_NAME_PLACE);
            }
        });


        setDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isDataStart = true;
                DialogFragment dialogFragment = new DatePicker();
                dialogFragment.show(getSupportFragmentManager(), "date picker");


            }
        });


        setDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isDataStart = false;
                DialogFragment dialogFragment2 = new DatePicker();
                dialogFragment2.show(getSupportFragmentManager(), "date picker");

            }
        });

        addNewTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendTripInfoIntent = new Intent();
                String name = tripName.getText().toString().trim();

                if (alreadyAvailableTrip != null) {
                    id = alreadyAvailableTrip.getTripId();
                    sendTripInfoIntent.putExtra("id", id);

                }

                sendTripInfoIntent.putExtra("nameTrip", name);
                sendTripInfoIntent.putExtra("date", dateS); //currentDateString
                sendTripInfoIntent.putExtra("endDate", dateE);
                sendTripInfoIntent.putExtra("namePlace", placeNameText);
                sendTripInfoIntent.putExtra("coordinates", coordinates);
                sendTripInfoIntent.putExtra("timeStart", timeS);
                sendTripInfoIntent.putExtra("timeEnd", timeE);
                setResult(RESULT_OK, sendTripInfoIntent);
                finish();

            }
        });

    }

    private void setViewOrUpdateTrip() {
        tripName.setText(alreadyAvailableTrip.getTitle());
        placeName.setText(alreadyAvailableTrip.getNamePlace());
        dateOfTripStart.setText(alreadyAvailableTrip.getTripDate() + " " + alreadyAvailableTrip.getTimeStart());
        dateS = alreadyAvailableTrip.getTripDate();
        endDate.setText(alreadyAvailableTrip.getTripDateEnd() + " " + alreadyAvailableTrip.getTimeEnd() );
        dateE = alreadyAvailableTrip.getTripDateEnd();
        coordinates = alreadyAvailableTrip.getCoordinate();
        // dateOfTripEnd = alreadyAvailableTrip.getTripDateEnd();
        placeNameText = placeName.getText().toString().trim();
        timeS = alreadyAvailableTrip.getTimeStart();
        timeE = alreadyAvailableTrip.getTimeEnd();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NAME_PLACE) {
            if (resultCode == RESULT_OK) {

                assert data != null;

                placeNameText = data.getStringExtra("nameCountry");
                placeName.setText(placeNameText);
                coordinates = data.getStringExtra("coordinate");
                Toast.makeText(this, "Added location", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        if (isDataStart) {
            calendarS = Calendar.getInstance();
            calendarS.set(Calendar.YEAR, year);
            calendarS.set(Calendar.MONTH, month);
            calendarS.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            currentDateString = simpleDateFormat.format(calendarS.getTime());
            currentDateString = currentDateString.replace( '/', '.');
            dateOfTripStart.setText(currentDateString);
            hour2 = calendarS.get(Calendar.HOUR_OF_DAY);
            minute2 = calendarS.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog2 = new TimePickerDialog(AddNewTrip.this, R.style.TimePickerTheme, AddNewTrip.this, hour2, minute2, true);
            timePickerDialog2.show();

        } else {
            calendarE = Calendar.getInstance();
            calendarE.set(Calendar.YEAR, year);
            calendarE.set(Calendar.MONTH, month);
            calendarE.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            endDateString = simpleDateFormat.format(calendarE.getTime());
            endDateString = endDateString.replace( '/', '.');
            endDate.setText(endDateString);
            hour = calendarE.get(Calendar.HOUR_OF_DAY);
            minute = calendarE.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewTrip.this, R.style.TimePickerTheme, AddNewTrip.this, hour, minute, true);
            timePickerDialog.show();
        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (isDataStart) {
            hourFinal = hourOfDay;
            minuteFinal = minute;
            dateOfTripStart.setText(currentDateString + " " + hourFinal + ":" + minuteFinal);
            dateS = currentDateString;
            timeS = hourFinal + ":" + minuteFinal;

        } else {
            hourFinal2 = hourOfDay;
            minuteFinal2 = minute;
            endDate.setText(endDateString + " " + hourFinal2 + ":" + minuteFinal2);
            dateE = endDateString;
            timeE = hourFinal2 + ":" + minuteFinal2;
        }

    }

}