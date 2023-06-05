package com.edasinar.onlinelabteachermobile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import com.edasinar.model.MessageInfo
import com.edasinar.model.TeacherMessageInfo
import com.edasinar.onlinelabteachermobile.databinding.ActivityTeacherMessagesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TeacherMessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherMessagesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherMessagesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        actionBarColor()
        supportActionBar!!.title = "MESAJLARIM"

        auth = Firebase.auth
        firestore = Firebase.firestore

        getCurrentUserMessagesFromFirestore { messageInfoList ->
            println("on create message info list size: ${messageInfoList.size}")
            val adapter = TeacherMessagesAdapter(messageInfoList)
            val layoutManager = GridLayoutManager(this, 2)
            binding.teacherRecyclerView.layoutManager = layoutManager
            binding.teacherRecyclerView.adapter = adapter
        }
    }

    private fun actionBarColor() {
        val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#000066"))
        actionBar?.setBackgroundDrawable(colorDrawable)
    }

    private fun getCurrentUserMessagesFromFirestore(callback: (ArrayList<MessageInfo>) -> Unit) {
        val currentUserEmail = auth.currentUser?.email.toString()
        val messageInfoList = ArrayList<MessageInfo>()
        val messageRef = firestore.collection("message_info")
        val user = firestore.collection("teachers")
        val userQuery = user.whereEqualTo("email", currentUserEmail)
        var teacherName = ""
        userQuery.get()
            .addOnSuccessListener { querySnapshot ->
                for(document in querySnapshot.documents) {
                    val fullName = document.getString("full_name")
                    teacherName = fullName!!
                    println("teacher full name: $fullName")
                }
                println("teacher current user email: $currentUserEmail")

                println("teacher name:::: $teacherName")

                val query = messageRef.whereEqualTo("teacher", teacherName)
                query.get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val messageLabel = document.getString("messageLabel")
                            val email = document.getString("email")
                            val messageBody = document.getString("messageBody")
                            val fileDownloadUrl = document.getString("fileDownloadUrl")
                            val teacher = document.getString("teacher")
                            println("message label: $messageLabel")
                            messageInfoList.add(
                                MessageInfo(email!!, teacher!!, messageLabel!!,messageBody!!, fileDownloadUrl!!)
                            )
                        }

                        callback(messageInfoList)
                    }
                    .addOnFailureListener { exception ->
                        println("Exception: $exception")
                        callback(ArrayList())
                    }

            }


    }
}