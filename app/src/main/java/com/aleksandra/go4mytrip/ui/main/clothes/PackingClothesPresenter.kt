package com.aleksandra.go4mytrip.ui.main.clothes

import com.aleksandra.go4mytrip.lists.PackingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class PackingClothesPresenter(val view: PackingClothesContract.View): PackingClothesContract.Presenter {

    private var packingList: MutableList<PackingModel> = ArrayList()


    override fun fetchPackingList(idTrip: String, referencePackingList: DatabaseReference, uid: String) {
        referencePackingList.child(uid).child("trips").child(idTrip).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                packingList.clear()
                dataSnapshot.children.forEach {
                    val packingModel = it.getValue(PackingModel::class.java)
                    packingModel?.let {
                        if (packingModel.category == "Clothes") {
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