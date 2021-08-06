package com.aleksandra.go4mytrip.trips

interface GetActiveTripListener {
    fun onClicked(tripModel: TripModel, position: Int)
}