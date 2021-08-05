package com.aleksandra.go4mytrip.ui.main.essentials

import com.aleksandra.go4mytrip.lists.PackingModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class PackingEssentialsPresenter(val view: PackingEssentialsContract.View): PackingEssentialsContract.Presenter {

    private var packingList: MutableList<PackingModel> = ArrayList()

    override fun fetchPackingList(idTrip: String, referencePackingList: DatabaseReference, uid: String) {
        referencePackingList.child(uid).child("trips").child(idTrip).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                packingList.clear()
                dataSnapshot.children.forEach{
                    val packingModel = it.getValue(PackingModel::class.java)
                    packingModel?.let {
                        if (packingModel.category == "Essentials") {
                            packingList.add(packingModel)
                        }
                    }
                }
                view.showPackingList(packingList, true)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}