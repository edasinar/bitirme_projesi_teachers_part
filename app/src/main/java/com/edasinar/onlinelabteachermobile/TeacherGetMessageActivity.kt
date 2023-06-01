package com.edasinar.onlinelabteachermobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.edasinar.model.MessageInfo
import com.edasinar.onlinelabteachermobile.databinding.ActivityTeacherGetMessageBinding

class TeacherGetMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherGetMessageBinding

    private lateinit var messageInfo: MessageInfo
    private lateinit var studentEmail: String
    private lateinit var questionLabel: String

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherGetMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        messageInfo = intent.getParcelableExtra<MessageInfo>("message")!!

        binding.teacherGetQuestionLabel.text = messageInfo.messageLabel
        binding.teacherGetQuestionBody.text = messageInfo.messageBody
        studentEmail = messageInfo.email
        questionLabel = messageInfo.messageLabel
    }

    fun replyMessage(view: View) {
        val intent = Intent(this, TeacherReplyMessageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("email", studentEmail)
        bundle.putString("label", questionLabel)

        intent.putExtras(bundle)

        startActivity(intent)
    }
}