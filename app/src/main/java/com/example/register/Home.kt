package com.example.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val zelda = findViewById<ImageView>(R.id.Zelda)
        zelda.setOnClickListener{
            startActivity(Intent(this, Zelda::class.java))
        }
        val logout=findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
        }
        intent?.extras?.let{
            val value=it.getString("key1")
            val account=findViewById<TextView>(R.id.account)
            account.text="帳號："+value.toString()
        }
    }
}