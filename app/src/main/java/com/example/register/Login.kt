package com.example.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.register.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    //初始化binding and firebaseAuth變數
    private  lateinit var  binding : ActivityLoginBinding
    private  lateinit var  auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //返回起始畫面
        binding.textview1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth

        //按下登入按鈕後確認帳號密碼是否正確
        binding.LoginBtn.setOnClickListener{
            if(binding.email.text.toString().isEmpty()){
                Toast.makeText(this , "Please Enter Email" , Toast.LENGTH_SHORT).show()
            }else if(binding.password.text.toString().isEmpty()){
                Toast.makeText(this , "Please Enter Password" , Toast.LENGTH_SHORT).show()
            }else{
                login()
                }
            }
        }

    //檢查登入帳號密碼是否成功 成功則跳轉到Home頁面
    private fun login(){

        //取得email and password
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        //auth內有一個檢查帳號密碼的函數
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this , "Login Successfully" , Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Home::class.java))

                }else{
                    it.exception?.message?.let {  }
                    Toast.makeText(this , "Login Failed. Please check your email or password" , Toast.LENGTH_SHORT).show()
                }
            }
    }
}