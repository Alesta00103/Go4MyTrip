package com.aleksandra.go4mytrip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;

public class DetailsMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    String tripId;
    BottomNavigationView bottomNavigationView;
    private GoogleMap mMap;
    static final String TAG = "Info";
    ImageView backBtn;
    CardView cardViewAddPlaceToVisit;
    Button addPlaceToVisitBtn;
    TextView namePlaceToVisit, addressPlaceToVisit;
    CardView cardChooseTripPlace;
    Button selectTripPlace;
    TextView nameTripPlace, addressTripPlace;
    DatabaseReference mapReference, referenceTrips;
    FirebaseUser user;
    String uid;
    String id;
    String address;
    Boolean setOnlyPlace = false;


    private GoogleApiClient client2;
    private LocationRequest locationRequest;
    LatLng latlangNearby;
    String nameNearby;
    Location lastLocation;
    Marker currentLocationMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 50000;
    double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(DetailsMap.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        backBtn = findViewById(R.id.backBtn);

        cardViewAddPlaceToVisit = findViewById(R.id.cardViewAddPlaceToVisit);
        addPlaceToVisitBtn = findViewById(R.id.addPlaceToVisit);
        namePlaceToVisit = findViewById(R.id.namePlaceToVisit);
        addressPlaceToVisit = findViewById(R.id.addressPlaceToVisit);
        cardChooseTripPlace = findViewById(R.id.cardChooseTripPlace);
        nameTripPlace = findViewById(R.id.nameTripPlace);
        addressTripPlace = findViewById(R.id.addressTripPlace);
        selectTripPlace = findViewById(R.id.selectTripPlace);
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        uid = user.getUid();

        mapReference = FirebaseDatabase.getInstance().getReference("placesToVisit");
        referenceTrips = FirebaseDatabase.getInstance().getReference("trips");
        setOnlyPlace = getIntent().getBooleanExtra("setOnlyPlace", false);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addPlaceToVisitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = mapReference.push().getKey();
                String latLangN = latlangNearby.toString();
                PlaceModel place = new PlaceModel(id, nameNearby, "1", latLangN, address, null, null);
                assert id != null;
                mapReference.child(uid).child(tripId).child(id).setValue(place);
                cardViewAddPlaceToVisit.setVisibility(View.GONE);
                Toast.makeText(DetailsMap.this, "Place added to visit", Toast.LENGTH_SHORT).show();
            }
        });

        selectTripPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String latLangN = latlangNearby.toString();

                String nameT = nameNearby.toString();
                cardChooseTripPlace.setVisibility(View.GONE);
                Intent intent = new Intent(DetailsMap.this, AddNewTrip.class);
                intent.putExtra("nameCountry", nameT);
                intent.putExtra("coordinate", latLangN);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        String apiKey = getString(R.string.api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                mMap.clear();
                final LatLng latLng = place.getLatLng();
                latlangNearby = place.getLatLng();
                address = place.getAddress();
                String placeName = place.getName();
                nameNearby = place.getName();
                MarkerOptions options = new MarkerOptions().position(latLng).title(placeName);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.addMarker(options);


                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                if (!setOnlyPlace) {
                    namePlaceToVisit.setText(nameNearby);
                    addressPlaceToVisit.setText(address);
                    cardViewAddPlaceToVisit.setVisibility(View.VISIBLE);
                } else {
                    nameTripPlace.setText(nameNearby);
                    addressTripPlace.setText(address);
                    cardChooseTripPlace.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        tripId = getIntent().getStringExtra("tripId");
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.map);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.notes:
                        Intent in = new Intent(getApplicationContext(), DetailsNotes.class);
                        if (tripId != null) {
                            in.putExtra("tripId", tripId);
                        }
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        Intent ih = new Intent(getApplicationContext(), DetailTrip.class);
                        ih.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ih);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.map:
                        return true;
                }
                return false;
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission is granted
                    if (ContextCompat.checkSelfPermission(DetailsMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client2 == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
                }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.map);
    }

    protected synchronized void buildGoogleApiClient() {
        client2 = new GoogleApiClient.Builder(DetailsMap.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client2.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();

        referenceTrips.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                        TripModel trip = tripSnapshot.getValue(TripModel.class);
                        assert trip != null;
                        if (trip.getTripId().equals(tripId)) {


                            String latLng = trip.getCoordinate();
                            String latLng2 = latLng.replace("lat/lng: (", "");
                            String latLng3 = latLng2.replace(")", "");

                            String[] coordinates = latLng3.split(",");
                            double latitude1 = Double.parseDouble(coordinates[0]);
                            double longitude1 = Double.parseDouble(coordinates[1]);
                            LatLng latlngLocation = new LatLng(latitude1, longitude1);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngLocation, 12));

                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        if (!setOnlyPlace) {

            mapReference.child(uid).child(tripId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mMap.clear();
                    for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {

                        PlaceModel placeModel = placeSnapshot.getValue(PlaceModel.class);

                        assert placeModel != null;
                        String latLng = placeModel.getLatLang();
                        String latLng2 = latLng.replace("lat/lng: (", "");
                        String latLng3 = latLng2.replace(")", "");

                        String[] coordinates = latLng3.split(",");
                        double latitude1 = Double.parseDouble(coordinates[0]);
                        double longitude1 = Double.parseDouble(coordinates[1]);
                        LatLng latlngLocation = new LatLng(latitude1, longitude1);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latlngLocation);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        mMap.addMarker(markerOptions);

                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(DetailsMap.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(DetailsMap.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client2, locationRequest, DetailsMap.this);
        }

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(DetailsMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DetailsMap.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(DetailsMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(DetailsMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else return true;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
      /*  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        currentLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));*/

        //mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
        if (client2 != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client2, DetailsMap.this);
        }
    }

}
