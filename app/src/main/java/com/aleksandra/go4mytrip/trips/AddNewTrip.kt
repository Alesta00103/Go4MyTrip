package com.aleksandra.go4mytrip.trips

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
import com.aleksandra.go4mytrip.DatePicker
import com.aleksandra.go4mytrip.R
import com.aleksandra.go4mytrip.trips.TripModel
import com.aleksandra.go4mytrip.trips.Trips
import com.aleksandra.go4mytrip.googlemap.DetailsMap
import kotlinx.android.synthetic.main.activity_add_new_trip.*
import java.text.SimpleDateFormat
import java.util.*

class AddNewTrip : AppCompatActivity(), OnDateSetListener, OnTimeSetListener {
    private lateinit var imageBack: ImageView
    private lateinit var endDate: TextView
    private lateinit var currentDateString: String
    private lateinit var endDateString: String
    private var dateS: String? = null
    private var dateE: String? = null
    private var coordinates: String? = null
    private var placeNameText: String? = null
    private var layoutAddImage: LinearLayout? = null
    private var timeS: String? = null
    private var timeE: String? = null
    private var id: String? = null
    private var alreadyAvailableTrip: TripModel? = null
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
    private lateinit var tripFromTrips: TripModel

    @SuppressLint("SimpleDateFormat")
    var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_trip)
        endDate = findViewById(R.id.timeStart)
        layoutAddImage = findViewById(R.id.layoutAddImage)
        imageBack = findViewById(R.id.backBtn)

        backBtn.setOnClickListener { onBackPressed() }

        if (intent.getBooleanExtra("isViewOrUpdate", false)) {
            val tripModel = intent.getParcelableExtra<TripModel>(TRIP) as TripModel
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
            var newTripModel : TripModel
            alreadyAvailableTrip?.let {
                newTripModel = TripModel(it.tripId, name, placeNameText, coordinates, it.imageTrip, dateS,dateE, timeS, timeE)
                sendTripInfoIntent.putExtra(Trips.DATA, newTripModel)
            } ?: run {
                sendTripInfoIntent.putExtra("nameTrip", name)
                sendTripInfoIntent.putExtra("date", dateS) //currentDateString
                sendTripInfoIntent.putExtra("endDate", dateE)
                sendTripInfoIntent.putExtra("namePlace", placeNameText)
                sendTripInfoIntent.putExtra("coordinates", coordinates)
                sendTripInfoIntent.putExtra("timeStart", timeS)
                sendTripInfoIntent.putExtra("timeEnd", timeE)
            }

            setResult(RESULT_OK, sendTripInfoIntent)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViewOrUpdateTrip() {
        alreadyAvailableTrip?.let {
            editTripName.setText(it.title)
            placeName.text = it.namePlace
            dataTimeStart.text = "${it.tripDate} ${it.timeStart}"
            dateS = it.tripDate
            endDate.text = "${it.tripDateEnd} ${it.timeEnd}"
            dateE = it.tripDateEnd
            coordinates = it.coordinate
            placeNameText = placeName.text.toString().trim { it1 -> it1 <= ' ' }
            timeS = it.timeStart
            timeE = it.timeEnd
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_NAME_PLACE) {
            if (resultCode == RESULT_OK) {
                data?.let{placeNameText = it.getStringExtra("nameCountry")
                    placeName.text = placeNameText
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
            isDataStartTrue(year, month, dayOfMonth)
        } else {
            isDataStartFalse(year,month,dayOfMonth)
        }
    }

    private fun isDataStartTrue(year: Int, month: Int, dayOfMonth: Int) {
        calendarS = Calendar.getInstance()
        calendarS.set(Calendar.YEAR, year)
        calendarS.set(Calendar.MONTH, month)
        calendarS.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        currentDateString = simpleDateFormat.format(calendarS.time)
        currentDateString = currentDateString.replace('/', '.')
        dataTimeStart.text = currentDateString
        hour2 = calendarS.get(Calendar.HOUR_OF_DAY)
        minute2 = calendarS.get(Calendar.MINUTE)
        val timePickerDialog2 = TimePickerDialog(this@AddNewTrip, R.style.TimePickerTheme, this@AddNewTrip, hour2, minute2, true)
        timePickerDialog2.show()
    }

    private fun isDataStartFalse(year: Int, month: Int, dayOfMonth: Int){
        calendarE = Calendar.getInstance()
        calendarE.set(Calendar.YEAR, year)
        calendarE.set(Calendar.MONTH, month)
        calendarE.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        endDateString = simpleDateFormat.format(calendarE.time)
        endDateString = endDateString.replace('/', '.')
        endDate.text = endDateString
        hour = calendarE.get(Calendar.HOUR_OF_DAY)
        minute = calendarE.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this@AddNewTrip, R.style.TimePickerTheme, this@AddNewTrip, hour, minute, true)
        timePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (isDataStart) {
            hourFinal = hourOfDay
            minuteFinal = minute
            dataTimeStart.text = "$currentDateString $hourFinal:$minuteFinal"
            dateS = currentDateString
            timeS = "$hourFinal:$minuteFinal"
        } else {
            hourFinal2 = hourOfDay
            minuteFinal2 = minute
            endDate.text = "$endDateString $hourFinal2:$minuteFinal2"
            dateE = endDateString
            timeE = "$hourFinal2:$minuteFinal2"
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_NAME_PLACE = 1
        private const val REQUEST_CODE_STORAGE_PERMISSION = 2
        private const val REQUEST_CODE_SELECT_IMAGE = 3
        const val TRIP  = "TRIP"

    }
}