package com.aleksandra.go4mytrip.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aleksandra.go4mytrip.R
import com.aleksandra.go4mytrip.lists.PackingListAdapter
import com.aleksandra.go4mytrip.lists.PackingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class PackingClothesFragment : Fragment() {
    private lateinit var packingList: MutableList<PackingModel?>
    private lateinit var recyclerView: RecyclerView
    private lateinit var uid: String
    private lateinit var idTrip: String
    private lateinit var referencePackingList: DatabaseReference
    var isPacking = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        packingList = ArrayList()
        idTrip = activity?.intent?.getStringExtra("idTripToChange").toString()
        referencePackingList = FirebaseDatabase.getInstance().getReference("packingList")
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { uid = it.uid }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.tab_packing_clothes, container, false)
        recyclerView = root.findViewById(R.id.rv_packing_item)
        return root
    }

    override fun onStart() {
        super.onStart()
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
                val myAdapter = PackingListAdapter(activity, packingList, isPacking)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = myAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}