package com.dew.logindemo_dew

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.share.Sharer
import com.facebook.share.model.*
import com.facebook.share.widget.ShareDialog
import java.util.*


class ShareDataToSocial : AppCompatActivity() {
    lateinit var fbShareButton: Button
    lateinit var main_name_txt: TextView
    lateinit var main_email_txt: TextView
    private val URL = "https://github.com/rajivmanivannan/Android-Social-Login"
    var REQUEST_TAKE_GALLERY_VIDEO: Int = 1
    var filemanagerstring: String = ""
    private var shareDialog: ShareDialog? = null
    private var callbackManager: CallbackManager? = null

    //Google plus sign-in button
    private var googleSignInHelper: GoogleSignInHelper? = null

    lateinit var builder:AlertDialog
    lateinit var LogoutB: Button
    lateinit var main_linked_in_sign_in_button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_data_to_social)

        fbShareButton = findViewById(R.id.main_fb_share_button)
        main_name_txt = findViewById(R.id.main_name_txt)
        main_email_txt = findViewById(R.id.main_email_txt)
        LogoutB = findViewById(R.id.LogoutB)
        main_linked_in_sign_in_button = findViewById(R.id.main_linked_in_sign_in_button)

        val userName: String = intent.getStringExtra("user_name").toString()
        val email: String = intent.getStringExtra("user_email").toString()

        main_name_txt.text = userName
        main_email_txt.text = email

        googleSignInHelper = GoogleSignInHelper(this)
        callbackManager = CallbackManager.Factory.create();
        shareDialog = ShareDialog(this)
         shareDialog!!.registerCallback(callbackManager!!,callback);

        fbShareButton.setOnClickListener {
            showDialog("FaceBook")

        }
        main_linked_in_sign_in_button.setOnClickListener {
         shareOnTwiter()

        }

        LogoutB.setOnClickListener {
            LoginManager.getInstance().logOut();
            googleSignInHelper!!.signOut()
            val intent = Intent(this@ShareDataToSocial, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
    private val callback: FacebookCallback<Sharer.Result> =
        object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(result: Sharer.Result) {
                Toast.makeText(applicationContext,"Share data sucess",Toast.LENGTH_SHORT).show()
                builder.dismiss()
            }

            override fun onCancel() {
                Log.v("cancle", "Sharing cancelled")

                Toast.makeText(applicationContext,"Sharing cancelled",Toast.LENGTH_SHORT).show()
                // Write some code to do some operations when you cancel sharing content.
            }
            override fun onError(error: FacebookException) {
                Log.v("errror", error.message!!)
                Toast.makeText(applicationContext,"Sharing Error",Toast.LENGTH_SHORT).show()
                // Write some code to do some operations when some error occurs while sharing content.
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                var selectedImageUri: Uri = data?.data!!;
                val cr: ContentResolver = this.getContentResolver()
                val mime = cr.getType(selectedImageUri)
                Log.e("mime::-------", mime+"")

                val accessToken = AccessToken.getCurrentAccessToken()
                if (accessToken == null) {
                    Log.d("TAG", accessToken+">>>" + "Signed Out")
                } else {
                    shareOnFacebook(selectedImageUri,mime)
                }


               /* if (mime.toString().contains("image")) {
                    val  photo: SharePhoto =  SharePhoto.Builder()
                        .setImageUrl(selectedImageUri)
                        .build();
                    val photoContent: SharePhotoContent =  SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build()
                    shareDialog!!.show(photoContent, ShareDialog.Mode.AUTOMATIC)

                }
                else  if (mime.toString().contains("video")) {
                    val video: ShareVideo = ShareVideo.Builder()
                        .setLocalUrl(selectedImageUri)
                        .build()
                    val content: ShareVideoContent = ShareVideoContent.Builder()
                        .setVideo(video)
                        .build()
                    shareDialog!!.show(content, ShareDialog.Mode.AUTOMATIC)
                }
                filemanagerstring = selectedImageUri.getPath().toString();
                val selectedImagePath = parsePath(selectedImageUri);
                Log.e("data::-------", selectedImagePath + "" + selectedImageUri)
*/
            }
        }
    }

    fun parsePath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } else null
    }


    fun showDialog( button:String) {
         builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        builder.setTitle("Share")
        val view = layoutInflater.inflate(R.layout.custom_layout, null)
        val linkLayout = view.findViewById<LinearLayout>(R.id.linkLayout)
        val videoLayout = view.findViewById<LinearLayout>(R.id.videoLayout)
        builder.setView(view)
        linkLayout.setOnClickListener {
                if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                    val linkContent: ShareLinkContent = ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(URL))
                        .setQuote("sharing")
                        .build()
                    shareDialog!!.show(linkContent)
                }
        }
        videoLayout.setOnClickListener {
             val intent = Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
              intent.type = "image/* video/*";
         //   intent.setAction(Intent.ACTION_SEND);
            startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO)

        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()

    }

    fun shareOnFacebook(fileUri: Uri?, mime: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,fileUri)
        if (fileUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/*"
        }
        var facebookAppFound = false
        val matches = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        for (info in matches) {
            if (info.activityInfo.packageName.lowercase(Locale.getDefault())
                    .startsWith("com.facebook.katana") ||
                info.activityInfo.packageName.lowercase(Locale.getDefault())
                    .startsWith("com.facebook.lite")
            ) {
                intent.setPackage(info.activityInfo.packageName)
                facebookAppFound = true
                break
            }
        }
        if (facebookAppFound) {
           startActivity(intent)
        } else {
            if (mime.toString().contains("image")) {
                val  photo: SharePhoto =  SharePhoto.Builder()
                    .setImageUrl(fileUri)
                    .build();
                val photoContent: SharePhotoContent =  SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build()
                shareDialog!!.show(photoContent, ShareDialog.Mode.AUTOMATIC)

            }
            else  if (mime.toString().contains("video")) {
                val video: ShareVideo = ShareVideo.Builder()
                    .setLocalUrl(fileUri)
                    .build()
                val content: ShareVideoContent = ShareVideoContent.Builder()
                    .setVideo(video)
                    .build()
                shareDialog!!.show(content, ShareDialog.Mode.AUTOMATIC)
            }
        }
    }

    fun shareOnTwiter(){
        var intent: Intent? = null
        try {
            // get the Twitter app if possible
            this.packageManager.getPackageInfo("com.twitter.android", 0)
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=1546364474533507072"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) {
            // no Twitter app, revert to browser
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/AtulDew"))
        }
        this.startActivity(intent)



        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.setPackage("com.twitter.android")
        shareIntent.putExtra(Intent.EXTRA_STREAM, "imgUriForShare")
        shareIntent.type = "image/*"
        Intent.createChooser(shareIntent, "Share Image")
     //   deleteTempImageLauncher.launch(shareIntent)

     /*   try {
            Log.i("Yes twitter", "no twitter native")
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, "this is a tweet")
            intent.type = "text/plain"
            val pm = packageManager
            val activityList: List<*> = pm.queryIntentActivities(intent, 0)
            val len = activityList.size
            for (i in 0 until len) {
                val app = activityList[i] as ResolveInfo
                if ("com.twitter.android.PostActivity" == app.activityInfo.name) {
                    val activity = app.activityInfo
                    val name = ComponentName(activity.applicationInfo.packageName, activity.name)
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    intent.component = name
                    startActivity(intent)
                    break
                }
            }
        } catch (e: ActivityNotFoundException) {
            Log.i("twitter", "no twitter native", e)
        }*/
    }
}