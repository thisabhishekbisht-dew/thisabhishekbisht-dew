package com.dew.logindemo_dew

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GoogleSignInHelper {
    var RC_SIGN_IN = 1008
    // GoogleSignInClient
    private var googleSignInClient: GoogleSignInClient? = null
    private var activity: Activity? = null
    private var onGoogleSignInListener: OnGoogleSignInListener? = null


    constructor(activity: Activity, onGoogleSignInListener: OnGoogleSignInListener) {
        this.activity=activity
        this.onGoogleSignInListener=onGoogleSignInListener
    }
    constructor( activity: Activity) {
        this.activity = activity
    }


    /**
     * Connect to google
     */
    open fun connect() {
        //Mention the GoogleSignInOptions to get the user profile and email.
        // Instantiate Google SignIn Client.
        googleSignInClient = GoogleSignIn.getClient(
            activity!!,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )

    }

    fun onStart() {
        val account = GoogleSignIn.getLastSignedInAccount(activity!!)
        if (account != null && onGoogleSignInListener != null) {
            onGoogleSignInListener!!.OnGSignInSuccess(account)
        }
    }

    fun signIn() {
        val signInIntent:Intent = googleSignInClient!!.signInIntent
        activity!!.startActivityForResult(signInIntent,RC_SIGN_IN)
    }
    fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        googleSignInClient.signOut()
    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult<ApiException>(ApiException::class.java)
        // Signed in successfully
        if (onGoogleSignInListener != null) {
            onGoogleSignInListener!!.OnGSignInSuccess(account)
        }
        }catch (e:ApiException){
            if (onGoogleSignInListener != null) {
                onGoogleSignInListener!!.OnGSignInError(
                    GoogleSignInStatusCodes.getStatusCodeString(e.statusCode)
                )
            }
        }

    }

    /**
     * Interface to listen the Google sign in
     */
    interface OnGoogleSignInListener {
        fun OnGSignInSuccess(googleSignInAccount: GoogleSignInAccount?)
        fun OnGSignInError(error: String?)
    }

}
