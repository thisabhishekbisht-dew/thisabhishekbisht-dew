package com.dew.logindemo_dew

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.*



class LoginActivity : AppCompatActivity() {
    var loginButton: LoginButton? = null
    var callbackManager: CallbackManager? = null
    private val mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var twitter: ImageView
    private lateinit var gmail: ImageView
    private lateinit var facebook: ImageView
    private val google_signIn: Button? = null
    private val showOneTapUI = true
    var TAG = "abu"
    private var mAuth: FirebaseAuth? = null
    private var mCallbackManager: CallbackManager? = null
    var signInRequest: BeginSignInRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load the layout
        setContentView(R.layout.login_activity)
        gmail = findViewById(R.id.gmail)
        mAuth = FirebaseAuth.getInstance()
        twitter = findViewById(R.id.twitter)
        facebook = findViewById(R.id.facebook)


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Initialize Facebook Login button
        mCallbackManager = create()
        callbackManager = create()
        loginButton = findViewById(R.id.login_button)
        gmail.setOnClickListener(View.OnClickListener { })
        facebook.setOnClickListener(View.OnClickListener { view: View? ->
            val loginButton = findViewById<LoginButton>(R.id.login_button)
            loginButton.setReadPermissions("email", "public_profile")
            loginButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult?> {


                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {}
                override fun onSuccess(result: LoginResult?) {
                    Log.d(TAG, "facebook:onSuccess:$result")
                    handleFacebookAccessToken(result!!.accessToken)
                }
            })
        })
        LoginManager.getInstance().registerCallback(
            callbackManager!!,
            object : FacebookCallback<LoginResult?> {


                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }

                override fun onSuccess(result: LoginResult?) {

                }
            })
        twitter.setOnClickListener(View.OnClickListener { /*login with twitter */
            val intent = Intent(this@LoginActivity, TwitterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)

        // The activity result pass back to the Facebook SDK
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()

        // Checking if the user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser

        // Check if user is signed in (non-null) and update UI accordingly.
        if (currentUser != null) {
            Log.d(TAG, "Currently Signed in: " + currentUser.email)
            Toast.makeText(
                this@LoginActivity,
                "Currently Logged in: " + currentUser.email,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, UI will update with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth!!.currentUser
                    Toast.makeText(
                        this@LoginActivity,
                        "Authentication Succeeded.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    // If sign-in fails, a message will display to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    companion object {
        //
        // ...
        private const val REQ_ONE_TAP = 2 // Can be any integer unique to the Activity.
        private const val RC_SIGN_IN = 12345
    }
}

private fun LoginManager.registerCallback(callbackManager: CallbackManager, callback: FacebookCallback<LoginResult?>) {

}
