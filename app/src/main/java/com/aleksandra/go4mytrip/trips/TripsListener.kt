package com.aleksandra.go4mytrip.trips

import com.aleksandra.go4mytrip.trips.TripModel

interface TripsListener {
    fun onLongClicked(tripModel: TripModel, position: Int)
}