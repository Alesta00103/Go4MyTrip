package com.aleksandra.go4mytrip.lists

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aleksandra.go4mytrip.R
import com.aleksandra.go4mytrip.lists.AddItemDialog.AddItemDialogListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_shopping_list.*
import java.util.*

class ShoppingList : AppCompatActivity(), AddItemDialogListener {
    private lateinit var shoppingList: MutableList<PackingModel>
    private lateinit var uid: String
    private lateinit var idTrip: String
    private lateinit var referencePackingList: DatabaseReference
    private lateinit var categories: Array<CharSequence>
    private lateinit var id: String
    private lateinit var v: String
    private lateinit var nameOfItem: String
    private lateinit var categoryOfItem: String
    private lateinit var selectedCategory: String
    private var isPackingList = false
    private lateinit var numberOfItems: TextView
    private var numberOfShoppingItems = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        backBtn.setOnClickListener { onBackPressed() }
        categories = arrayOf(
                "Essentials",
                "Clothes",
                "Toiletries",
                "Others"
        )
        shoppingList = ArrayList()
        idTrip = intent.getStringExtra("idTripToChange").toString()
        referencePackingList = FirebaseDatabase.getInstance().getReference("packingList")
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            uid = user.uid
        }

        numberOfItems = findViewById(R.id.numberOfShoppingItems)
        numberOfItems.text = "Items: " + 0
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiverCB,
                IntentFilter("checkboxS"))

        fab.setOnClickListener { showDialog() }
    }

    private var messageReceiverCB: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intentCheckBoxS: Intent) {
            val packingModel = intentCheckBoxS.getParcelableExtra<PackingModel>(CHECKBOXS) as PackingModel
            val updates: MutableMap<String, Any> = HashMap()
            updates["checkedS"] = packingModel.checkedS ?: false
            packingModel.itemId?.let {it1 -> referencePackingList.child(uid).child("trips").child(idTrip).child(it1).updateChildren(updates)}

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showDialog() {
        val builder = MaterialAlertDialogBuilder(this@ShoppingList, R.style.AlertDialogTheme)
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
            v = null.toString()
            v = categories[which].toString()
        }
        builder.setView(input)
        builder.background = resources.getDrawable(R.drawable.background_dialog, null)
        builder.setPositiveButton(getString(R.string.add)) { dialog, which ->
            val name = input.text.toString()
            id = referencePackingList.push().key.toString()

            name.let {
                val packingModel = PackingModel(id, it, v, checkedS = false, checked = true, toBuy = false)
                referencePackingList.child(uid).child("trips").child(idTrip).child(id).setValue(packingModel)
                Snackbar.make(relativeLayout, getString(R.string.item_added), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                clearPackingItem()
                dialog.dismiss()
            } ?: run {
                Snackbar.make(relativeLayout, getString(R.string.select_category), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show()
            }
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiverCB)
    }

    public override fun onStart() {
        super.onStart()
        referencePackingList.child(uid).child("trips").child(idTrip).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                shoppingList.clear()
                numberOfShoppingItems = 0
                dataSnapshot.children.forEach{
                    val packingModel = it.getValue(PackingModel::class.java)
                    packingModel?.toBuy?.let {
                        shoppingList.add(packingModel)
                        numberOfShoppingItems += 1
                    }
                }
                numberOfItems.text = "Items: $numberOfShoppingItems"
                val myAdapter = PackingListAdapter(this@ShoppingList, shoppingList, isPackingList)
                shoppingRecyclerView.layoutManager = LinearLayoutManager(this@ShoppingList)
                shoppingRecyclerView.setHasFixedSize(true)
                shoppingRecyclerView.adapter = myAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun applyTexts(name: String, selection: String) {
        name?.let {
            nameOfItem = it
        }
        selection?.let {
            selectedCategory = it
        }
        val packingModel= PackingModel(id, nameOfItem, selectedCategory, checkedS = false, checked = true, toBuy = false)

        referencePackingList.child(uid).child("trips").child(idTrip).child(id).setValue(packingModel)
            Snackbar.make(relativeLayout, getString(R.string.item_added) + packingModel.category?.toUpperCase(Locale.ROOT), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        clearPackingItem()
    }

    private fun clearPackingItem() {
        nameOfItem = null.toString()
        categoryOfItem = null.toString()
    }

    companion object{
        const val CHECKBOXS = "CHECKBOXS"
    }
}