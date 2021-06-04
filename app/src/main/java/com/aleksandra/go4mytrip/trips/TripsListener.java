package com.aleksandra.go4mytrip.trips;

import com.aleksandra.go4mytrip.trips.TripModel;

public interface TripsListener {
    void onLongClicked(TripModel tripModel, int position);
}
