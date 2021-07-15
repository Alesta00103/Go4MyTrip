package com.aleksandra.go4mytrip.lists

import android.os.Parcel
import android.os.Parcelable

data class PackingModel (var itemId: String? = null, var name: String? = null, var category: String? = null, var checkedS: Boolean? = null, var checked: Boolean? = null, var toBuy: Boolean? = null): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemId)
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeValue(checkedS)
        parcel.writeValue(checked)
        parcel.writeValue(toBuy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PackingModel> {
        override fun createFromParcel(parcel: Parcel): PackingModel {
            return PackingModel(parcel)
        }

        override fun newArray(size: Int): Array<PackingModel?> {
            return arrayOfNulls(size)
        }
    }

}