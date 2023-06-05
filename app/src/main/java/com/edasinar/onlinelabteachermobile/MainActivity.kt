package com.edasinar.onlinelabteachermobile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import com.edasinar.onlinelabteachermobile.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        actionBarColor()
        supportActionBar!!.title = "ANA SAYFA"

        binding.teacherMessages.setOnClickListener {
            val intent = Intent(this, TeacherMessagesActivity::class.java)
            startActivity(intent)
        }

        binding.teacherFileUpload.setOnClickListener {
            val intent = Intent(this, TeacherFileUploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun actionBarColor() {
        val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#000066"))
        actionBar?.setBackgroundDrawable(colorDrawable)
    }

    fun logout(view: View) {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}