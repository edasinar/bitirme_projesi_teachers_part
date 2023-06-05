package com.edasinar.onlinelabteachermobile

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.edasinar.model.TeacherMessageInfo
import com.edasinar.onlinelabteachermobile.databinding.ActivityTeacherReplyMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class TeacherReplyMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherReplyMessageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var fileUri: Uri

    private var downloadUrl = ""
    private var email = ""
    private var label = ""

    private val PERMISSION_CODE = 1000
    private val FILE_PICK_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherReplyMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        actionBarColor()
        supportActionBar!!.title = "MESAJ CEVAPLAMA"

        val bundle = intent.extras
        if (bundle != null) {
            email = bundle.getString("email").toString()
            label = bundle.getString("label").toString()
        }
        binding.teacherReplyMessageLabel.text = label
        println("reply activity öğrenci emaili: $email" )

    }

    private fun actionBarColor() {
        val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#000066"))
        actionBar?.setBackgroundDrawable(colorDrawable)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == FILE_PICK_CODE) {

            fileUri = data?.data!!

            val fileName = fileUri.path?.substringAfterLast("/")

            val ref = storage.reference
            val fileRef = ref.child("teacherMessageFile").child("$fileName")

            fileRef.putFile(fileUri).addOnSuccessListener {
                val uploadFileRef = storage.reference.child("teacherMessageFile").child("$fileName")
                uploadFileRef.downloadUrl.addOnSuccessListener {
                    downloadUrl = it.toString()
                }
            }.addOnFailureListener {
                Toast.makeText(this,"dosya yüklenemedi hata....", Toast.LENGTH_SHORT).show()
            }

        }
        val selectedFilePath = fileUri.path
        val selectedFileName = selectedFilePath?.substringAfterLast("/")

        binding.teacherUploadFileName.text = selectedFileName
    }

    private fun selectFileFromPhone() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_PICK_CODE)

    }

    private fun uploadDataToFirebase( messageLabel: String, messageBody: String) {

        val uuid = UUID.randomUUID()
        val teacherMessageInfo = TeacherMessageInfo(email, messageLabel,messageBody,downloadUrl)
        firestore.collection("teacher_message_info").document("$uuid").set(teacherMessageInfo)

    }

    /** telefondan belge almak için */
    fun uploadFile(view: View) {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            selectFileFromPhone()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
        }
    }

    /** mesajı göndermek için*/
    fun sendMessageToStudent(view: View) {
        val questionLabel = binding.teacherReplyMessageLabel.text.toString()
        val questionText = binding.teacherReplyMessageBody.text.toString()
        var isValid = true

        if(questionText.isEmpty()) {
            Toast.makeText(this, "soru kısmı boş geçilemez", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (isValid) {
            uploadDataToFirebase(questionLabel, questionText)

            val intent = Intent(this, TeacherMessagesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}