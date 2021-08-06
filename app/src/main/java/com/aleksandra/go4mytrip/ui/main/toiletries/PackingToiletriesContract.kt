package com.aleksandra.go4mytrip.ui.main.toiletries

import com.aleksandra.go4mytrip.lists.PackingModel
import com.google.firebase.database.DatabaseReference

interface PackingToiletriesContract {

    interface View {
        fun showPackingList(packingList: MutableList<PackingModel>, isPacking: Boolean)
    }
    interface Presenter {
        fun fetchPackingList(idTrip : String, referencePackingList: DatabaseReference, uid: String)
    }

}