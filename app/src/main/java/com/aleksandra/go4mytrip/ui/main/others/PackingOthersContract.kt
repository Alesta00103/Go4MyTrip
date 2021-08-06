package com.aleksandra.go4mytrip.ui.main.others

import com.aleksandra.go4mytrip.lists.PackingModel
import com.google.firebase.database.DatabaseReference

interface PackingOthersContract {
    interface View {
        fun showPackingList(packingList: MutableList<PackingModel>, isPacking: Boolean)
    }
    interface Presenter {
        fun fetchPackingList(idTrip : String, referencePackingList: DatabaseReference, uid: String)
    }
}