package com.aleksandra.go4mytrip

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class PackingListRepository {

    private lateinit var uid: String

    fun getDatabaseReferencePackingList() : DatabaseReference{
        return FirebaseDatabase.getInstance().getReference("packingList")
    }

    fun getUserId() : String{
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { uid = it.uid }
        return uid
    }

}
