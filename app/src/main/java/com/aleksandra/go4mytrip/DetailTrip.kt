package com.aleksandra.go4mytrip

import android.annotation.SuppressLint
import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_details_trip.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DetailTrip : AppCompatActivity(), OnDateSetListener, OnTimeSetListener, PlaceListener {
    private var title: TextView? = null
    private var dateS: TextView? = null
    private var dateE: TextView? = null
    private var image: ImageButton? = null
    private var imageInt = 0
    private lateinit var dateStart: String
    private var dateStartOnly: String? = null
    private var currentDateOnly: String? = null
    private lateinit var dateEnd: String
    private lateinit var tripId: String
    private var timeStart: String? = null
    private var timeEnd: String? = null
    private lateinit var dateOfStartTrip: Date
    private var datee2end: Date? = null
    private lateinit var datenow: Date
    private lateinit var noPlaces: ImageView
    private lateinit var noPlacesText: TextView
    private var endMillis: Long = 0
    private var timeMilli: Long = 0
    private var startMillis: Long = 0
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var mapReference: DatabaseReference
    private lateinit var referenceTrips: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var uid: String
    private var placeList: MutableList<PlaceModel?>? = null
    private lateinit var recyclerView: RecyclerView
    private var placeClickedPosition = -1
    private var hour = 0
    private var minute = 0
    private var hourFinal = 0
    private var minuteFinal = 0
    private lateinit var currentDateString: String
    private lateinit var currentTimeString: String
    private lateinit var idOfClickedPlace: String
    private lateinit var dateAndTimeString: String
    private var dateAndTimeDate: Date? = null
    private var dateAndTimeMillis: Long = 0
    private lateinit var calendarS: Calendar

    @SuppressLint("SimpleDateFormat")
    var sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @SuppressLint("SimpleDateFormat")
    var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_trip)

        createNotificationChannel()
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        referenceTrips = FirebaseDatabase.getInstance().getReference("trips")

        addPlace.setOnClickListener {
            startActivity(Intent(this@DetailTrip, DetailsMap::class.java))
        }
        backBtn.setOnClickListener { onBackPressed() }
        bottom_navigation.selectedItemId = R.id.home
        bottom_navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.notes -> {
                    val `in` = Intent(applicationContext, DetailsNotes::class.java)
                    `in`.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    `in`.putExtra("tripId", tripId)
                    startActivity(`in`)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.home -> return@OnNavigationItemSelectedListener true
                R.id.map -> {
                    val im = Intent(applicationContext, DetailsMap::class.java)
                    im.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    im.putExtra("tripId", tripId)
                    startActivity(im)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        title = findViewById(R.id.nameTrip)
        dateS = findViewById(R.id.startDate)
        dateE = findViewById(R.id.endDate)
        image = findViewById(R.id.imagexTrip)

        mapReference = FirebaseDatabase.getInstance().getReference("placesToVisit")
        user = FirebaseAuth.getInstance().currentUser!!
        uid = user.uid
        placeList = ArrayList()
        recyclerView = findViewById(R.id.recyclerview_id)
        noPlaces = findViewById(R.id.noPlaces)
        noPlacesText = findViewById(R.id.noPlacesText)


        tripId = intent.getStringExtra("idTripToDetail").toString()
        nameTrip.text = intent.getStringExtra("title")
        startDate.text = intent.getStringExtra("dateS")
        dateStart = intent.getStringExtra("dateS").toString()
        endDate.text = intent.getStringExtra("dateE")
        dateEnd = intent.getStringExtra("dateE").toString()
        timeStart = intent.getStringExtra("timeStart")
        timeEnd = intent.getStringExtra("timeEnd")
        imageInt = intent.getIntExtra("image", 1)

        imagexTrip.setImageResource(imageInt)
        dateStart = dateStart.replace('.', '/')
        dateEnd = dateEnd.replace('.', '/')
        dateStartOnly = dateStart
        dateStart = "$dateStart $timeStart:00"
        dateEnd = "$dateEnd $timeEnd:00" // data of trip's end
        val current = Date()
        currentDateOnly = simpleDateFormat.format(current)
        dateOfStartTrip = Date()

        try {
            dateOfStartTrip = sdf.parse(dateStart)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        dateOfStartTrip.let {
            startMillis = dateOfStartTrip.time //start of trip in ms
            datee2end = Date()
        }

        try {
            datee2end = sdf.parse(dateEnd)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        datee2end?.let {
            endMillis = it.time // data zakonczenia wyjazdu w ms
        }

        datenow = Date()
        val newDate = sdf.format(datenow)
        try {
            datenow = sdf.parse(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        timeMilli = System.currentTimeMillis() // actual time in ms
        countDownTimer(startMillis - timeMilli) // time to departure in ms
        btn_packingList.setOnClickListener {
            val intent = Intent(this@DetailTrip, PackingList::class.java)
            intent.putExtra("idTripToChange", tripId)
            startActivity(intent)
        }
        btn_shoppingList.setOnClickListener {
            val intent = Intent(this@DetailTrip, ShoppingList::class.java)
            intent.putExtra("idTripToChange", tripId)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        tripId = intent.getStringExtra("idTripToDetail").toString()
    }

    override fun onStart() {
        super.onStart()
        mapReference.child(uid).child(tripId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                placeList?.clear()
                if (dataSnapshot.exists()) {
                    for (placeSnapshot in dataSnapshot.children) {
                        val placeModel = placeSnapshot.getValue(PlaceModel::class.java)
                        placeList?.add(placeModel)
                    }
                    recyclerView.visibility = View.VISIBLE
                    noPlaces.visibility = View.GONE
                    noPlacesText.visibility = View.GONE
                    val myAdapter = PlaceAdapter(this@DetailTrip, placeList, this@DetailTrip)
                    recyclerView.layoutManager = LinearLayoutManager(this@DetailTrip)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = myAdapter
                } else {
                    recyclerView.visibility = View.GONE
                    noPlaces.visibility = View.VISIBLE
                    noPlacesText.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun countDownTimer(total_millis: Long) {    // total_millis to time to departure in ms
                                                        // 1000- tick every 1 second
        val cdt: CountDownTimer = object : CountDownTimer(total_millis, 1000) {
                                                        //  long total__millis = total_millis;
            override fun onTick(millisUntilFinished: Long) {
                var millisUntilFinished: Long = millisUntilFinished
                val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days)
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                if (days == 1L) {
                    val s1 = getString(R.string._day)
                    val s = days.toString() + s1
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1.5f), 0, 1, 0)
                    counterDown.text = ss1
                } else if (days > 1) {
                    val s1 = getString(R.string._days)
                    val s = days.toString() + s1
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1.5f), 0, 2, 0)
                    counterDown.text = ss1
                } else if (days == 0L && hours > 1) {
                    val s1 = getString(R.string._hours)
                    val s = hours.toString() + s1
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1.5f), 0, 2, 0)
                    counterDown.text = ss1
                } else if (days == 0L && hours == 1L) {
                    val s1 = getString(R.string._hour)
                    val s = hours.toString() + s1
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1.5f), 0, 2, 0)
                    counterDown.text = ss1
                } else if (days == 0L && hours == 0L && minutes > 1) {
                    val s1 = getString(R.string._minutes)
                    val s = minutes.toString() + s1
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1.5f), 0, 2, 0)
                    counterDown.text = ss1
                } else if (days == 0L && hours == 0L && minutes == 1L) {
                    val s1 = getString(R.string._minute)
                    val s = minutes.toString() + s1
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1.5f), 0, 2, 0)
                    counterDown.text = ss1
                } else if (days == 0L && hours == 0L && minutes == 0L && seconds > 0) {
                    val s1 = getString(R.string._seconds)
                    val s2 = getString(R.string._second)
                    var s: String? = null
                    if (seconds > 1) {
                        s = seconds.toString() + s1
                    } else if (seconds == 1L) {
                        s = seconds.toString() + s2
                    }
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1.5f), 0, 2, 0)
                    counterDown.text = ss1
                }
            }

            override fun onFinish() {
                val milliseconds = endMillis - timeMilli    // time from now to end of trip in ms
                if (milliseconds > 0) {
                    counterDown.text = getString(R.string.DuringTheTrip)
                    counterDown.gravity = Gravity.CENTER
                    counterDown.textSize = 11f
                    counterDown.layout(0, 0, 0, 0)
                } else {
                    counterDown.text = getString(R.string.AfterTrip)
                    counterDown.textSize = 15f
                    counterDown.gravity = Gravity.CENTER
                    counterDown.layout(0, 0, 0, 0)
                }
            }
        }
        cdt.start()
    }

    public override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.home
    }

    override fun onDateSet(view: android.widget.DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        calendarS = Calendar.getInstance()
        calendarS.set(Calendar.YEAR, year)
        calendarS.set(Calendar.MONTH, month)
        calendarS.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        currentDateString = simpleDateFormat.format(calendarS.time)
        currentDateString = currentDateString.replace("/", ".")
        hour = calendarS.get(Calendar.HOUR_OF_DAY)
        minute = calendarS.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this@DetailTrip, R.style.TimePickerTheme, this@DetailTrip, hour, minute, true)
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        hourFinal = hourOfDay
        minuteFinal = minute
        currentTimeString = "$hourFinal:$minuteFinal"
        updateItem()
    }

    private fun updateItem() {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val setEditTime: MutableMap<String, Any?> = HashMap()
        setEditTime["date"] = currentDateString
        setEditTime["time"] = currentTimeString
        mapReference.child(uid).child(tripId).child(idOfClickedPlace).updateChildren(setEditTime)
        currentDateString = currentDateString.replace('.', '/')
        dateAndTimeString = currentDateString.trim { it <= ' ' } + " " + currentTimeString.trim { it <= ' ' } + ":00"

        try {
            dateAndTimeDate = sdf.parse(dateAndTimeString.trim { it <= ' ' })
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        dateAndTimeDate?.let {
            dateAndTimeMillis = it.time
            setReminder()
        }

    }

    override fun onLongClicked(placeModel: PlaceModel, position: Int) {
        placeClickedPosition = position
        idOfClickedPlace = placeModel.id
        val dialogFragment: DialogFragment = DatePicker()
        dialogFragment.show(supportFragmentManager, "date picker")
    }

    override fun onClickDelete(placeModel: PlaceModel, position: Int) {
        placeClickedPosition = position
        idOfClickedPlace = placeModel.id
        mapReference.child(uid).child(tripId).child(idOfClickedPlace).removeValue()
    }

    private fun setReminder() {
        val intent = Intent(this@DetailTrip, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this@DetailTrip, 0, intent, 0)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, dateAndTimeMillis - 3600000] = pendingIntent //dateAndTimeMillis - currentTimeInMillis
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "ReminderChannel"
            val description = "Channel for reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notifyPlace", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}