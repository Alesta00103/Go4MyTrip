package com.aleksandra.go4mytrip.ui.main.essentials

import com.aleksandra.go4mytrip.lists.PackingModel
import com.google.firebase.database.DatabaseReference

interface PackingEssentialsContract {
    interface View {
        fun showPackingList(packingList: MutableList<PackingModel>, isPacking: Boolean)
    }
    interface Presenter {
        fun fetchPackingList(idTrip : String, referencePackingList: DatabaseReference, uid: String)
    }
}