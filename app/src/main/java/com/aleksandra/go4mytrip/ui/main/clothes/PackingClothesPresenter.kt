package com.aleksandra.go4mytrip.ui.main.clothes

import com.aleksandra.go4mytrip.lists.PackingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class PackingClothesPresenter(val view: PackingClothesContract.View): PackingClothesContract.Presenter {

    private lateinit var packingList: MutableList<PackingModel>
    private lateinit var uid: String
    private lateinit var referencePackingList: DatabaseReference
    var isPacking = true


    fun declareData(){
            packingList = ArrayList()
            referencePackingList = FirebaseDatabase.getInstance().getReference("packingList")
            val user = FirebaseAuth.getInstance().currentUser
            user?.let { uid = it.uid }
    }


    override fun fetchPackingList(idTrip: String) {
        declareData()
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