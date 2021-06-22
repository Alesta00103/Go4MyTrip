package com.aleksandra.go4mytrip

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    private var photo: String? = null
    private var personEmail: String? = null
    private var personName: String? = null
    var uid: String? = null

    var databaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null) {
            val intent = Intent(this@MainActivity, Trips::class.java)
            startActivity(intent)
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        login_in_google.setOnClickListener { signIn() }
    }

    private fun signIn() {
        progress_bar!!.visibility = View.VISIBLE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acc = completedTask.getResult(ApiException::class.java)!!
            Toast.makeText(this@MainActivity, "Signed In Successfully", Toast.LENGTH_SHORT).show()
            firebaseGoogleAuth(acc)
        } catch (e: ApiException) {
            Toast.makeText(this@MainActivity, "Signed In Failed", Toast.LENGTH_SHORT).show()
            firebaseGoogleAuth(null)
        }
    }

    private fun firebaseGoogleAuth(acct: GoogleSignInAccount?) {
        val authCredential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                progress_bar!!.visibility = View.INVISIBLE
                updateUI()
            } else {
                progress_bar!!.visibility = View.INVISIBLE
                updateUI()
            }
            val intentLog = Intent(this@MainActivity, Trips::class.java)
            startActivity(intentLog)
        }
    }

    private fun updateUI() {
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (account != null) {
            personName = account.displayName
            personEmail = account.email
            photo = account.photoUrl.toString()
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            uid = firebaseUser!!.uid
            addUser()
        } else {
            login_in_google!!.visibility = View.VISIBLE
        }
    }

    private fun addUser() {
        databaseReference!!.child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    val name = personName
                    val email = personEmail
                    val imageUri = photo
                    val id = databaseReference!!.push().key
                    val userr = User(id, name, email, imageUri)
                    databaseReference!!.child(uid!!).setValue(userr)
                    Toast.makeText(this@MainActivity, "added user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    companion object{
        private const val RC_SIGN_IN = 1
    }
}