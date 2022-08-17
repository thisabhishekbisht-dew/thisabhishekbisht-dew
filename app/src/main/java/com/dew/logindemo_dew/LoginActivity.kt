package com.dew.logindemo_dew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.CallbackManager.Factory.create
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class LoginActivity : AppCompatActivity(), GoogleSignInHelper.OnGoogleSignInListener {
    var loginButton: LoginButton? = null
    var callbackManager: CallbackManager? = null
    private lateinit var twitter: Button
    private lateinit var gmail: Button
    var TAG = "abu"
    private lateinit var mAuth :FirebaseAuth
    private var mCallbackManager: CallbackManager? = null
    private lateinit var fbk_custom: Button
    private var googleSignInHelper: GoogleSignInHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // load the layout
        setContentView(R.layout.login_activity)
        gmail = findViewById(R.id.gmail)
        twitter = findViewById(R.id.twitter)
        fbk_custom = findViewById(R.id.fbk_custom)
        // Initialize Facebook Login button
        mAuth = Firebase.auth
        // Initialize Firebase Auth
        mCallbackManager = create()
        callbackManager = create()

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onCancel() {
                TODO("Not yet implemented")
            }
            override fun onError(error: FacebookException) {
                TODO("Not yet implemented")
            }
            override fun onSuccess(result: LoginResult) {
                TODO("Not yet implemented")
            }
        })
        //----------------------------------Google +Sign in-----------------------------------//
        googleSignInHelper = GoogleSignInHelper(this, this)
        googleSignInHelper!!.connect()



        gmail.setOnClickListener {
            googleSignInHelper!!.signIn()

        }

        fbk_custom.setOnClickListener({ view: View? ->
           LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("user_photos", "email", "user_birthday", "public_profile")
            )




        })

        twitter.setOnClickListener(View.OnClickListener { /*login with twitter */
            val provider = OAuthProvider.newBuilder("twitter.com")
            val pendingResultTask: Task<AuthResult> = mAuth.getPendingAuthResult()!!
            if (pendingResultTask != null) {
                // There's something already here! Finish the sign-in for your user.
                pendingResultTask
                    .addOnSuccessListener {
                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
                        // The OAuth secret can be retrieved by calling:
                        // authResult.getCredential().getSecret().
                    }
                    .addOnFailureListener {
                        // Handle failure.
                    }
            } else {
                // There's no pending result so you need to start the sign-in flow.
                // See below.
                mAuth
                    .startActivityForSignInWithProvider( /* activity= */this, provider.build())
                    .addOnSuccessListener(
                        OnSuccessListener<AuthResult?> {
                            // User is signed in.
                            // IdP data available in
                            // authResult.getAdditionalUserInfo().getProfile().
                            // The OAuth access token can also be retrieved:
                            // authResult.getCredential().getAccessToken().
                            // The OAuth secret can be retrieved by calling:
                            // authResult.getCredential().getSecret().
                        })
                    .addOnFailureListener(
                        OnFailureListener {
                            // Handle failure.
                        })
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // The activity result pass back to the Facebook SDK
        /*this one is for facebook login*/
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        /*this one is for google sign in*/
        googleSignInHelper?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        googleSignInHelper!!.onStart()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {


        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                // Sign in success, UI will update with the signed-in user's information
                val user = mAuth!!.currentUser
                Log.d(TAG, user?.displayName.toString())
                val intent = Intent(this@LoginActivity, ShareDataToSocial::class.java)
                intent.putExtra("user_name", user?.displayName.toString())
                intent.putExtra("user_email", token.userId)
                startActivity(intent)
            } else {
                // If sign-in fails, a message will display to the user.
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }



    override fun OnGSignInSuccess(googleSignInAccount: GoogleSignInAccount?) {
        if (googleSignInAccount != null) {
            Log.e("name:>>>>>", googleSignInAccount.givenName + googleSignInAccount.photoUrl)
            val intent = Intent(this@LoginActivity, ShareDataToSocial::class.java)
            intent.putExtra("user_name", googleSignInAccount.givenName)
            intent.putExtra("user_email", googleSignInAccount.email)
            startActivity(intent)

        }
    }
    override fun OnGSignInError(error: String?) {
        Log.e("name:>>>>>", error.toString())
    }
}
