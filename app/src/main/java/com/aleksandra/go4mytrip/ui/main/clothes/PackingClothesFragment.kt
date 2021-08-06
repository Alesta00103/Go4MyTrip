package com.aleksandra.go4mytrip.ui.main.clothes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aleksandra.go4mytrip.PackingListRepository
import com.aleksandra.go4mytrip.R
import com.aleksandra.go4mytrip.lists.PackingListAdapter
import com.aleksandra.go4mytrip.lists.PackingModel
import com.aleksandra.go4mytrip.trips.TripModel
import com.google.firebase.database.DatabaseReference

class PackingClothesFragment : Fragment(), PackingClothesContract.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var referencePackingList: DatabaseReference
    private lateinit var packingListRepository : PackingListRepository
    private lateinit var uid: String

    val presenter by lazy { PackingClothesPresenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        packingListRepository = PackingListRepository()
        referencePackingList = packingListRepository.getDatabaseReferencePackingList()
        uid = packingListRepository.getUid()
    }

    companion object {
        private lateinit var idTrip: String
        fun createInstance(trip: TripModel) {
            idTrip = trip.tripId.toString()
        }
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
        presenter.fetchPackingList(idTrip, referencePackingList, uid)
    }

    override fun showPackingList(packingList: MutableList<PackingModel>, isPacking: Boolean) {
        val myAdapter = activity?.let { PackingListAdapter(it, packingList, isPacking) }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter
    }
}