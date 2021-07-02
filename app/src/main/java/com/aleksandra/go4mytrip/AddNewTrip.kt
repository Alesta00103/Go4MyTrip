package com.aleksandra.go4mytrip

import android.annotation.SuppressLint
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_add_new_trip.*
import java.text.SimpleDateFormat
import java.util.*

class AddNewTrip : AppCompatActivity(), OnDateSetListener, OnTimeSetListener {
    private var tripName: EditText? = null
    private var addNewTripBtn: ImageView? = null
    private var imageBack: ImageView? = null
    private var setDateStart: Button? = null
    private var setDateEnd: Button? = null
    private var endDate: TextView? = null
    private var addNewPlace: Button? = null
    private var dateOfTripStart: TextView? = null
    private lateinit var currentDateString: String
    private lateinit var endDateString: String
    private var dateS: String? = null
    private var dateE: String? = null
    private var placeName: TextView? = null
    private var coordinates: String? = null
    private var placeNameText: String? = null
    private var layoutAddImage: LinearLayout? = null
    private var timeS: String? = null
    private var timeE: String? = null
    private var id: String? = null
    private lateinit var alreadyAvailableTrip: TripModel
    private var image = 0
    private lateinit var calendarS: Calendar
    private lateinit var calendarE: Calendar
    private var isDataStart = false
    private var hour = 0
    private var minute = 0
    private var hour2 = 0
    private var minute2 = 0
    private var hourFinal = 0
    private var minuteFinal = 0
    private var hourFinal2 = 0
    private var minuteFinal2 = 0

    @SuppressLint("SimpleDateFormat")
    var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_trip)
        tripName = findViewById(R.id.editTripName)
        addNewTripBtn = findViewById(R.id.saveTrip)
        setDateStart = findViewById(R.id.setDateTimeStart)
        setDateEnd = findViewById(R.id.setTimeStart)
        endDate = findViewById(R.id.timeStart)
        dateOfTripStart = findViewById(R.id.dataTimeStart)
        addNewPlace = findViewById(R.id.setPlace)
        placeName = findViewById(R.id.placeName)
        layoutAddImage = findViewById(R.id.layoutAddImage)
        imageBack = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { onBackPressed() }
        if (intent.getBooleanExtra("isViewOrUpdate", false)) {
            id = intent.getStringExtra("id")
            val title = intent.getStringExtra("title")
            val namePlace = intent.getStringExtra("namePlace")
            val coordinate = intent.getStringExtra("coordinate")
            val tripDate = intent.getStringExtra("tripDate")
            val tripDateEnd = intent.getStringExtra("tripDateEnd")
            val timeStart = intent.getStringExtra("timeStart")
            val timeEnd = intent.getStringExtra("timeEnd")
            image = intent.getIntExtra("image", 0)
            val tripModel = TripModel(id, title, namePlace, coordinate, image, tripDate, tripDateEnd, timeStart, timeEnd)
            alreadyAvailableTrip = tripModel
            setViewOrUpdateTrip()
        }
        setPlace.setOnClickListener {
            val intent = Intent(this@AddNewTrip, DetailsMap::class.java)
            intent.putExtra("setOnlyPlace", true)
            startActivityForResult(intent, REQUEST_CODE_ADD_NAME_PLACE)
        }
        setDateTimeStart.setOnClickListener{
            isDataStart = true
            val dialogFragment: DialogFragment = DatePicker()
            dialogFragment.show(supportFragmentManager, "date picker")
        }
        setTimeStart.setOnClickListener {
            isDataStart = false
            val dialogFragment2: DialogFragment = DatePicker()
            dialogFragment2.show(supportFragmentManager, "date picker")
        }
        saveTrip.setOnClickListener {
            val sendTripInfoIntent = Intent()
            val name = editTripName.text.toString().trim { it <= ' ' }
            alreadyAvailableTrip.let {
                id = it.tripId
                sendTripInfoIntent.putExtra("id", id)
            }
            sendTripInfoIntent.putExtra("nameTrip", name)
            sendTripInfoIntent.putExtra("date", dateS) //currentDateString
            sendTripInfoIntent.putExtra("endDate", dateE)
            sendTripInfoIntent.putExtra("namePlace", placeNameText)
            sendTripInfoIntent.putExtra("coordinates", coordinates)
            sendTripInfoIntent.putExtra("timeStart", timeS)
            sendTripInfoIntent.putExtra("timeEnd", timeE)
            setResult(RESULT_OK, sendTripInfoIntent)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViewOrUpdateTrip() {
        tripName!!.setText(alreadyAvailableTrip.title)
        placeName!!.text = alreadyAvailableTrip.namePlace
        dateOfTripStart!!.text = alreadyAvailableTrip.tripDate + " " + alreadyAvailableTrip.timeStart
        dateS = alreadyAvailableTrip.tripDate
        endDate!!.text = alreadyAvailableTrip.tripDateEnd + " " + alreadyAvailableTrip.timeEnd
        dateE = alreadyAvailableTrip.tripDateEnd
        coordinates = alreadyAvailableTrip.coordinate
        placeNameText = placeName!!.text.toString().trim { it <= ' ' }
        timeS = alreadyAvailableTrip.timeStart
        timeE = alreadyAvailableTrip.timeEnd
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_NAME_PLACE) {
            if (resultCode == RESULT_OK) {
                data?.let{placeNameText = it.getStringExtra("nameCountry")
                    placeName!!.text = placeNameText
                    coordinates = it.getStringExtra("coordinate")
                    Toast.makeText(this, getString(R.string.addedLocation), Toast.LENGTH_SHORT).show()}

            }
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.resolveActivity(packageManager)?.let { startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(this, getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDateSet(view: android.widget.DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        if (isDataStart) {
            calendarS = Calendar.getInstance()
            calendarS.set(Calendar.YEAR, year)
            calendarS.set(Calendar.MONTH, month)
            calendarS.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            currentDateString = simpleDateFormat.format(calendarS.time)
            currentDateString = currentDateString.replace('/', '.')
            dateOfTripStart!!.text = currentDateString
            hour2 = calendarS.get(Calendar.HOUR_OF_DAY)
            minute2 = calendarS.get(Calendar.MINUTE)
            val timePickerDialog2 = TimePickerDialog(this@AddNewTrip, R.style.TimePickerTheme, this@AddNewTrip, hour2, minute2, true)
            timePickerDialog2.show()
        } else {
            calendarE = Calendar.getInstance()
            calendarE.set(Calendar.YEAR, year)
            calendarE.set(Calendar.MONTH, month)
            calendarE.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            endDateString = simpleDateFormat.format(calendarE.time)
            endDateString = endDateString.replace('/', '.')
            endDate!!.text = endDateString
            hour = calendarE.get(Calendar.HOUR_OF_DAY)
            minute = calendarE.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(this@AddNewTrip, R.style.TimePickerTheme, this@AddNewTrip, hour, minute, true)
            timePickerDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (isDataStart) {
            hourFinal = hourOfDay
            minuteFinal = minute
            dateOfTripStart!!.text = "$currentDateString $hourFinal:$minuteFinal"
            dateS = currentDateString
            timeS = "$hourFinal:$minuteFinal"
        } else {
            hourFinal2 = hourOfDay
            minuteFinal2 = minute
            endDate!!.text = "$endDateString $hourFinal2:$minuteFinal2"
            dateE = endDateString
            timeE = "$hourFinal2:$minuteFinal2"
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_NAME_PLACE = 1
        private const val REQUEST_CODE_STORAGE_PERMISSION = 2
        private const val REQUEST_CODE_SELECT_IMAGE = 3
    }
}