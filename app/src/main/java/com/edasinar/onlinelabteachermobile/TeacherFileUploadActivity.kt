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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.edasinar.model.TeacherMessageInfo
import com.edasinar.model.UploadedFile
import com.edasinar.onlinelabteachermobile.databinding.ActivityTeacherFileUploadBinding
import com.google.common.io.Files.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*

class TeacherFileUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherFileUploadBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var fileUri: Uri

    private var downloadUrl = ""

    private val PERMISSION_CODE = 1000
    private val FILE_PICK_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherFileUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        actionBarColor()
        supportActionBar!!.title = "KAYNAK YÜKLEME"

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        binding.saveVideosToLessonButton.setOnClickListener {
            uploadVideoFromFirestore(downloadUrl)
        }
        binding.saveDocsToNotesButton.setOnClickListener {
            uploadNotesFromFirestore(downloadUrl)
        }
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
            uploadFileInStorage(data)
        }
        val selectedFilePath = fileUri.path
        val selectedFileName = selectedFilePath?.substringAfterLast("/")

        binding.textView3.text = selectedFileName
    }

    private fun uploadFileInStorage(data: Intent?) {
        fileUri = data?.data!!
        val filePath = fileUri.path
        val fileName = filePath?.substringAfterLast("/")

        if(isVideoFile(filePath.toString())) {

            val ref = storage.reference
            val fileRef = ref.child("lessons").child("$fileName")

            fileRef.putFile(fileUri).addOnSuccessListener {
                val uploadFileRef = storage.reference.child("lessons").child("$fileName")
                uploadFileRef.downloadUrl.addOnSuccessListener {
                    downloadUrl = it.toString()
                }
            }.addOnFailureListener {
                Toast.makeText(this,"dosya storage yüklenemedi hata....", Toast.LENGTH_SHORT).show()
            }
        }
        else if(isConvertibleToPdf(filePath.toString())) {

            val ref = storage.reference
            val fileRef = ref.child("notes").child("$fileName")

            fileRef.putFile(fileUri).addOnSuccessListener {
                val uploadFileRef = storage.reference.child("notes").child("$fileName")
                uploadFileRef.downloadUrl.addOnSuccessListener {
                    downloadUrl = it.toString()
                }
            }.addOnFailureListener {
                Toast.makeText(this,"dosya storage yüklenemedi hata....", Toast.LENGTH_SHORT).show()
            }

        }
        else {
            Toast.makeText(this, "ŞU ANDA İŞLEMİNİZ GERÇEKLEŞTİRİLEMEMEKTEDİR!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectFileFromPhone() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_PICK_CODE)

    }

    private fun isVideoFile(filePath: String): Boolean {
        val videoExtensions = arrayOf(".mp4", ".mkv", ".avi", ".mov", ".wmv")
        val fileExtension = getFileExtension(filePath)
        return videoExtensions.contains(fileExtension)
    }

    private fun getFileExtension(filePath: String): String {
        val file = File(filePath)
        val extension = file.extension
        return if (extension.isNotEmpty()) {
            ".$extension"
        } else {
            ""
        }
    }


    private fun isConvertibleToPdf(filePath: String): Boolean {
        val convertibleExtensions = arrayOf(".doc", ".docx", ".ppt", ".pptx", ".txt", ".pdf")
        val fileExtension = getFileExtension(filePath)
        return convertibleExtensions.contains(fileExtension)
    }

    fun uploadFileFromPhone(view: View) {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            selectFileFromPhone()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
        }
    }

    private fun uploadVideoFromFirestore(downloadUrl: String) {
        val selectedFilePath = fileUri.path
        val fileName = selectedFilePath?.substringAfterLast("/")

        val uuid = UUID.randomUUID()
        val uploadedFile = UploadedFile(fileName.toString(),downloadUrl)
        firestore.collection("lessons").document("$uuid").set(uploadedFile)
    }

    private fun uploadNotesFromFirestore(downloadUrl: String) {
        val selectedFilePath = fileUri.path
        val fileName = selectedFilePath?.substringAfterLast("/")

        val uuid = UUID.randomUUID()
        val uploadedFile = UploadedFile(fileName.toString(),downloadUrl)
        firestore.collection("notes").document("$uuid").set(uploadedFile)
    }

    private fun alertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Uyarı")
        alertDialogBuilder.setMessage("Bu bir örnek AlertDialog'dur.")
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setNegativeButton("Tamam") { dialog, which ->
            println("hiç gerek kalmaz dileği ile...")
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}