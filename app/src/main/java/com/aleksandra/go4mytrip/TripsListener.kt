package com.aleksandra.go4mytrip

interface TripsListener {
    fun onLongClicked(tripModel: TripModel?, position: Int)
}