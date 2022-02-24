package com.ripanjatt.easychatlib

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val chatLib = findViewById<EasyChatLib>(R.id.chatView)
        chatLib.setUp(this, "ripanjatt")
        chatLib.setDP(R.mipmap.person)
        chatLib.addIncomingMessage("Test message 1", true, Date(System.currentTimeMillis()))
        chatLib.addIncomingMessage("Test message 2", true, Date(System.currentTimeMillis()))
        chatLib.addIncomingMessage("Test message 3", true, Date(System.currentTimeMillis()))
        chatLib.addOutgoingMessage("Test message 1", Date(System.currentTimeMillis()))
        chatLib.addIncomingMessage("Test message 4", true, Date(System.currentTimeMillis()))
        chatLib.addIncomingMessage("Test message 5", true, Date(System.currentTimeMillis()))
        chatLib.addIncomingMessage("Test message 6", true, Date(System.currentTimeMillis()))
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.person)
        chatLib.addIncomingImage(bitmap, "This is an image Test", true, Date(System.currentTimeMillis()))
        chatLib.addOutgoingImage(bitmap, null, Date(System.currentTimeMillis()))
        val button = Button(this)
        button.text = ("Test View")
        chatLib.addIncomingView(button, true, Date(System.currentTimeMillis()))
        chatLib.setOnSendListener {
            Log.e("TEST", "Message is $it")
        }
    }
}