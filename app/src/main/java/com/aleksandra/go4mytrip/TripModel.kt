package com.aleksandra.go4mytrip

data class TripModel constructor(var tripId: String? = null, var title: String? = null, var namePlace: String? = null, var coordinate: String? = null, var imageTrip:Int = 0, var tripDate: String? = null,
                                 var tripDateEnd: String? = null, var timeStart: String? = null, var timeEnd: String? = null)