package com.aleksandra.go4mytrip

import android.os.Parcel
import android.os.Parcelable

data class TripModel (var tripId: String? = null, var title: String? = null, var namePlace: String? = null, var coordinate: String? = null, var imageTrip:Int = 0, var tripDate: String? = null,
                                 var tripDateEnd: String? = null, var timeStart: String? = null, var timeEnd: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tripId)
        parcel.writeString(title)
        parcel.writeString(namePlace)
        parcel.writeString(coordinate)
        parcel.writeInt(imageTrip)
        parcel.writeString(tripDate)
        parcel.writeString(tripDateEnd)
        parcel.writeString(timeStart)
        parcel.writeString(timeEnd)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TripModel> {
        override fun createFromParcel(parcel: Parcel): TripModel {
            return TripModel(parcel)
        }

        override fun newArray(size: Int): Array<TripModel?> {
            return arrayOfNulls(size)
        }
    }
}
