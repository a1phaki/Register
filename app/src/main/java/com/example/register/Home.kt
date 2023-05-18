package com.example.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Home : AppCompatActivity() {
    private  lateinit var  auth : FirebaseAuth
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
        auth = FirebaseAuth.getInstance()
        val user=auth.currentUser
        val email=user?.email
        val emailAsNode =email?.replace(".", "_")//將user的email的.取代成＿（firebase的節點不能有.)
        val database=Firebase.database("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val myRef=database.getReference("users")
        if (emailAsNode != null) {
            myRef.child(emailAsNode).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataSnapshot = task.result
                    if (dataSnapshot != null) {
                        // 取得節點下的值
                        val name = dataSnapshot.child("name").getValue(String::class.java)

                        // 使用取得的值進行後續處理
                        if (name != null) {
                            // 對取得的值進行相應操作
                            Log.d("TAG", "Value: $name")
                        }
                    }
                } else {
                    // 讀取資料失敗，處理錯誤
                    Log.w("TAG", "Failed to read value.", task.exception)
                }
            }
        }
        }

    }