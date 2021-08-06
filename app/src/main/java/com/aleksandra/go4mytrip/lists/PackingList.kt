package com.aleksandra.go4mytrip.lists

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.aleksandra.go4mytrip.R
import com.aleksandra.go4mytrip.lists.AddItemDialog.AddItemDialogListener
import com.aleksandra.go4mytrip.ui.main.SectionsPagerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_packing_list.*
import java.util.*

class PackingList : AppCompatActivity(), AddItemDialogListener {
    private lateinit var packingList: List<PackingModel>
    private lateinit var uid: String
    private lateinit var idTrip: String
    private lateinit var referencePackingList: DatabaseReference
    private lateinit var id: String
    private lateinit var nameOfItem: String
    private lateinit var categoryOfItem: String
    private lateinit var selectedCategory: String
    private lateinit var mainLayout: CoordinatorLayout
    private lateinit var categories: Array<CharSequence>
    private var v: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packing_list)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        categories = arrayOf(
                "Essentials",
                "Clothes",
                "Toiletries",
                "Others"
        )
        backBtn.setOnClickListener { onBackPressed() }
        fab.setOnClickListener { showDialog() }
        packingList = ArrayList()
        idTrip = intent.getStringExtra("idTripToChange").toString()
        referencePackingList = FirebaseDatabase.getInstance().getReference("packingList")
        val user = FirebaseAuth.getInstance().currentUser
        user?.let{
            uid = user.uid
        }

        mainLayout = findViewById(R.id.mainLayout)
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                IntentFilter("delete-item"))
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiverCB,
                IntentFilter("checkbox"))
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiverBP,
                IntentFilter("toBuy"))
    }

    private var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intentPackingItemDelete: Intent) {
            val packingModel = intentPackingItemDelete.getParcelableExtra<PackingModel>(DELETE) as PackingModel
                Toast.makeText(this@PackingList, getString(R.string.deleted_item), Toast.LENGTH_SHORT).show()
                packingModel.itemId?.let { it1 -> referencePackingList.child(uid).child("trips").child(idTrip).child(it1).removeValue() }
        }
    }
    private var messageReceiverCB: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intentCheckBox: Intent) {
            val packingModel = intentCheckBox.getParcelableExtra<PackingModel>(CHECKBOX) as PackingModel
            val updates: MutableMap<String, Any> = HashMap()
            updates["checked"] = packingModel.checked ?: false
            packingModel.itemId?.let { it1-> referencePackingList.child(uid).child("trips").child(idTrip).child(it1).updateChildren(updates)}
        }
    }
    private var messageReceiverBP: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intentToBuy: Intent) {
            val packingModel = intentToBuy.getParcelableExtra<PackingModel>(TOBUY) as PackingModel
            val updates: MutableMap<String, Any> = HashMap()
            updates["toBuy"] = packingModel.toBuy ?: false
            packingModel.itemId?.let { it1-> referencePackingList.child(uid).child("trips").child(idTrip).child(it1).updateChildren(updates)}

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showDialog() {
        val builder = MaterialAlertDialogBuilder(this@PackingList, R.style.AlertDialogTheme)
        val input = EditText(this)
        input.hint = getString(R.string.enter_name)
        input.setHintTextColor(resources.getColor(R.color.textHint))
        input.setTextColor(resources.getColor(R.color.white))
        input.background.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        input.setLinkTextColor(resources.getColor(R.color.white))
        input.highlightColor = resources.getColor(R.color.white)
        builder.setTitle(getString(R.string.add_item))
        builder.setIcon(R.drawable.ic_baseline_list_add_24)
        builder.setSingleChoiceItems(categories, -1) { dialog, which ->
            v = null
            v = categories[which].toString()
        }
        builder.setView(input)
        builder.background = resources.getDrawable(R.drawable.background_dialog, null)
        builder.setPositiveButton(getString(R.string.add)) { dialog, which ->
            val name = input.text.toString()
            id = referencePackingList.push().key.toString()
            if (name != "" && v != null) {
                val packingModel = PackingModel(id, name, v, checkedS = false, checked = false, toBuy = false)
                referencePackingList.child(uid).child("trips").child(idTrip).child(id).setValue(packingModel)
                packingModel.let {
                    Snackbar.make(mainLayout, getString(R.string.item_added_to_category) + (packingModel.category?.toUpperCase(Locale.ROOT)), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }

                clearPackingItem()
                dialog.dismiss()
            } else {
                Snackbar.make(mainLayout,  getString(R.string.select_category), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show()
            }
        }
        builder.setNegativeButton("CANCEL") { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    public override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(messageReceiverCB, filter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiverCB)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiverBP)
    }

    override fun applyTexts(name: String, selection: String) {
        nameOfItem = name
        selectedCategory = selection
        val packingModel= PackingModel(id, nameOfItem, selectedCategory, checkedS = false, checked = false, toBuy = false)
        referencePackingList.child(uid).child("trips").child(idTrip).child(id).setValue(packingModel)
        Snackbar.make(mainLayout, "Item added to category " + packingModel.category?.toUpperCase(Locale.ROOT), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        clearPackingItem()
    }

    private fun clearPackingItem() {
        nameOfItem = null.toString()
        categoryOfItem = null.toString()
    }
    companion object{
        const val DELETE = "DELETE"
        const val CHECKBOX = "CHECKBOX"
        const val TOBUY = "TOBUY"
    }
}