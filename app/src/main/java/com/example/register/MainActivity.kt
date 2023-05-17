package com.example.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerBtn = findViewById<Button>(R.id.RegisterBtn)
        val loginTextView = findViewById<TextView>(R.id.Login)

        registerBtn.setOnClickListener{
            startActivity(Intent(this, Register::class.java))
        }

        loginTextView.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
        }

    }
}