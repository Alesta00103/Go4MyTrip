package com.aleksandra.go4mytrip

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_trips.*
import java.util.*

class Trips : AppCompatActivity(), PopupMenu.OnMenuItemClickListener, TripsListener {
    var image2: ImageView? = null
    var email: String? = null
    var uriPhoto: String? = null
    var userName: String? = null
    var tripList: MutableList<TripModel?>? = null
    private var costEstimatesList: List<CostEstimateModel>? = null
    private var noteList: List<NoteModel>? = null
    private var packingLists: List<PackingModel>? = null
    lateinit var referenceTrips: DatabaseReference
    private lateinit var referenceUser: DatabaseReference
    lateinit var referencePackingList: DatabaseReference
    lateinit var referenceExpenses: DatabaseReference
    lateinit var referenceNotes: DatabaseReference
    lateinit var referencePlaces: DatabaseReference
    var user: FirebaseUser? = null
    var uid: String? = null
    private var nameOfTrip: String? = null
    private var imageUriOfTrip = 0
    private var dateStart: String? = null
    private var dateEnd: String? = null
    var deletedTrip: TripModel? = null
    private var tripClickedPosition = -1
    private var images = intArrayOf(R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5, R.drawable.img6, R.drawable.img7,
            R.drawable.img8, R.drawable.img9, R.drawable.img10, R.drawable.img11, R.drawable.img12, R.drawable.img13, R.drawable.img14,
            R.drawable.img15, R.drawable.img16, R.drawable.img17, R.drawable.img18, R.drawable.img19, R.drawable.img20,
            R.drawable.img21, R.drawable.img22, R.drawable.img24, R.drawable.img25)

    private lateinit var mGoogleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        image2 = findViewById(R.id.image2)

        tripList = mutableListOf()
        costEstimatesList = ArrayList()
        noteList = ArrayList()
        packingLists = ArrayList()
        user = FirebaseAuth.getInstance().currentUser

        uid = user!!.uid
        referenceTrips = FirebaseDatabase.getInstance().getReference("trips")
        referenceUser = FirebaseDatabase.getInstance().getReference("users")
        referencePackingList = FirebaseDatabase.getInstance().getReference("packingList")
        referenceExpenses = FirebaseDatabase.getInstance().getReference("expenses")
        referenceNotes = FirebaseDatabase.getInstance().getReference("notes")
        referencePlaces = FirebaseDatabase.getInstance().getReference("placesToVisit")

        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                referenceTrips.child(uid!!).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        tripList!!.clear()
                        for (tripSnapshot in dataSnapshot.children) {
                            val trip = tripSnapshot.getValue(TripModel::class.java)
                            if (trip!!.title!!.toLowerCase(Locale.ROOT).contains(inputSearch.text.toString().toLowerCase(Locale.ROOT))) {
                                tripList!!.add(trip)
                            }
                        }
                        val myAdapter = RecyclerTripAdapter(this@Trips, tripList, this@Trips)
                        recyclerview_id.layoutManager = LinearLayoutManager(this@Trips)
                        recyclerview_id.setHasFixedSize(true)
                        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerview_id)
                        recyclerview_id.adapter = myAdapter

                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun afterTextChanged(s: Editable) {}
        })
        circleButton.setOnClickListener {
            startActivityForResult(
                    Intent(applicationContext, AddNewTrip::class.java), REQUEST_CODE_ADD_TRIP)
        }
        referenceTrips.child(uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tripList!!.clear()
                for (tripSnapshot in dataSnapshot.children) {
                    val trip = tripSnapshot.getValue(TripModel::class.java)
                    tripList!!.add(trip)
                    if (tripList!!.size == 0) {
                        recyclerview_id.visibility = View.GONE
                        noTrips.visibility = View.VISIBLE
                        noTripsText.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TRIP) {
            if (resultCode == RESULT_OK) {
                nameOfTrip = data!!.getStringExtra("nameTrip")
                dateStart = data.getStringExtra("date")
                val namePlace = data.getStringExtra("namePlace")
                val coordinate = data.getStringExtra("coordinates")
                val timeStart: String? = if (data.getStringExtra("timeStart") != null) {
                    data.getStringExtra("timeStart")
                } else {
                    "00:00:00"
                }
                val timeEnd: String? = if (data.getStringExtra("timeEnd") != null) {
                    data.getStringExtra("timeEnd")
                } else {
                    "00:00:00"
                }
                dateEnd = data.getStringExtra("endDate")
                imageUriOfTrip = randomImage()
                val id = referenceTrips.push().key
                if (nameOfTrip != null && imageUriOfTrip != 0 && dateStart != null && dateEnd != null) {
                    val trip = TripModel(id, nameOfTrip, namePlace, coordinate, imageUriOfTrip, dateStart, dateEnd, timeStart, timeEnd)
                    //assert(id != null)
                    referenceTrips.child(uid!!).child(id!!).setValue(trip)
                    Toast.makeText(this, "Trip added", Toast.LENGTH_SHORT).show()
                    clearTripCard()
                } else {
                    Toast.makeText(this@Trips, "Some data is empty, trip was not added", Toast.LENGTH_SHORT).show()
                    clearTripCard()
                }
            }
        } else if (requestCode == REQUEST_CODE_UPDATE_TRIP) {
            if (resultCode == RESULT_OK) {
                val id = data!!.getStringExtra("id")
                nameOfTrip = data.getStringExtra("nameTrip")
                dateStart = data.getStringExtra("date")
                val namePlace = data.getStringExtra("namePlace")
                val coordinate = data.getStringExtra("coordinates")
                val timeStart: String? = if (data.getStringExtra("timeStart") != null) {
                    data.getStringExtra("timeStart")
                } else {
                    "00:00:00"
                }
                val timeEnd: String? = if (data.getStringExtra("timeEnd") != null) {
                    data.getStringExtra("timeEnd")
                } else {
                    "00:00:00"
                }
                dateEnd = data.getStringExtra("endDate")
                val editItem: MutableMap<String, Any?> = HashMap()
                editItem["title"] = nameOfTrip
                editItem["tripDate"] = dateStart
                editItem["tripDateEnd"] = dateEnd
                editItem["coordinate"] = coordinate
                editItem["namePlace"] = namePlace
                editItem["timeStart"] = timeStart
                editItem["timeEnd"] = timeEnd

                referenceTrips.child(uid!!).child(id!!).updateChildren(editItem)
            }
        }
    }

    private fun randomImage(): Int {
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    private fun clearTripCard() {
        nameOfTrip = null
        imageUriOfTrip = 0
        dateStart = null
        dateEnd = null
    }

    override fun onStart() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        mGoogleApiClient.connect()
        super.onStart()
        referenceUser.child(uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    email = user.email
                    userName = user.name
                    uriPhoto = user.imageUser
                    Picasso.get().load(uriPhoto).into(image2)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        referenceTrips.child(uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tripList!!.clear()
                if (dataSnapshot.exists()) {
                    for (tripSnapshot in dataSnapshot.children) {
                        val trip = tripSnapshot.getValue(TripModel::class.java)
                        tripList!!.add(trip)
                        if (tripList!!.size > 0) {
                            recyclerview_id!!.visibility = View.VISIBLE
                            noTrips!!.visibility = View.GONE
                            noTripsText!!.visibility = View.GONE
                        }
                    }
                    val myAdapter = RecyclerTripAdapter(this@Trips, tripList, this@Trips)
                    recyclerview_id!!.layoutManager = LinearLayoutManager(this@Trips)
                    recyclerview_id!!.setHasFixedSize(true)
                    ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerview_id)
                    recyclerview_id!!.adapter = myAdapter
                } else {
                    recyclerview_id!!.visibility = View.GONE
                    noTrips!!.visibility = View.VISIBLE
                    noTripsText!!.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun showPopup(v: View?) {
        val popupMenu = PopupMenu(this, v!!)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1 -> Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { // ...
                Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_SHORT).show()
                val i = Intent(applicationContext, MainActivity::class.java)
                FirebaseAuth.getInstance().signOut()
                startActivity(i)
                finish()
            }
        }
        return true
    }

    var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val trip = tripList!![position]
            deletedTrip = trip
            val id = trip!!.tripId
            referenceTrips.child(uid!!).child(id!!).removeValue()
            Snackbar.make(recyclerview_id!!, "Deleted trip " + deletedTrip!!.title, Snackbar.LENGTH_LONG)
                    .setAction("Undo") { referenceTrips.child(uid!!).child(id).setValue(deletedTrip) }.show()
            val handler = Handler()
            handler.postDelayed({
                referenceTrips.child(uid!!).child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            referenceExpenses.child(uid!!).child(id).removeValue()
                            referenceNotes.child(uid!!).child(id).removeValue()
                            referencePackingList.child(uid!!).child("trips").child(id).removeValue()
                            referencePlaces.child(uid!!).child(id).removeValue()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }, 8000) //8 seconds
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            RecyclerViewSwipeDecorator.Builder(this@Trips, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(this@Trips, R.color.white))
                    .addActionIcon(R.drawable.ic_baseline_delete_24_blue)
                    .create()
                    .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onLongClicked(tripModel: TripModel, position: Int) {
        tripClickedPosition = position
        val id = tripModel.tripId
        val title = tripModel.title
        val coordinate = tripModel.coordinate
        val tripDate = tripModel.tripDate
        val timeStart = tripModel.timeStart
        val timeEnd = tripModel.timeEnd
        val image = tripModel.imageTrip
        val tripDateEnd = tripModel.tripDateEnd
        val namePlace = tripModel.namePlace
        val intent = Intent(this@Trips, AddNewTrip::class.java)
        intent.putExtra("isViewOrUpdate", true)
        intent.putExtra("id", id)
        intent.putExtra("title", title)
        intent.putExtra("coordinate", coordinate)
        intent.putExtra("tripDate", tripDate)
        intent.putExtra("timeStart", timeStart)
        intent.putExtra("timeEnd", timeEnd)
        intent.putExtra("image", image)
        intent.putExtra("tripDateEnd", tripDateEnd)
        intent.putExtra("namePlace", namePlace)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_TRIP)
    }

    companion object {
        const val REQUEST_CODE_UPDATE_TRIP = 2
        const val REQUEST_CODE_ADD_TRIP = 1
    }
}