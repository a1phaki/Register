package com.example.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.register.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class Register : AppCompatActivity() {

    //初始化
    private lateinit var phoneNumberET : EditText
    private lateinit var mAuth : FirebaseAuth
    private lateinit var number : String
    private lateinit var otp: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    //初始化binding and firebaseAuth變數
    private  lateinit var  binding : ActivityRegisterBinding
    private  lateinit var  auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //返回起始畫面
        binding.textview1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //呼叫id
        phoneNumberET = findViewById(R.id.phoneEditTextNumber)
        mAuth = FirebaseAuth.getInstance()
        val database = Firebase.database("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val myRef=database.getReference("users")

        //OTPBtn點擊事件
        binding.sendOTPBtn.setOnClickListener {
            number = phoneNumberET.text.trim().toString() //獲取電話號碼

            if(number.isNotEmpty()){
                if (number.length == 10){
                    number = "+886$number"

                    //身份認證
                    val options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }else{
                    Toast.makeText(this , "Please Enter Correct Number" , Toast.LENGTH_SHORT).show() //提示字元
                }
            }else{
                Toast.makeText(this , "Please Enter Number" , Toast.LENGTH_SHORT).show() //提示字元
            }
        }

        binding.resendTextView.setOnClickListener(){
            resendVerificationCode()
        }

        binding.textview8.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth

        //按下註冊按鈕後取得email and password
        binding.RegisterBtn.setOnClickListener{

            val typedOTP = binding.otpEditTextNumber.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val number = phoneNumberET.text.trim().toString()
            val date=binding.Date.text.toString()
            val name=binding.username.text.toString()

            val users = HashMap<String, Any>()
            users["name"] = name
            users["phone"] = number
            users["date"] = date
            val emailAsNode =email?.replace(".", "_")//將user的email的.取代成＿（firebase的節點不能有.)

            if(typedOTP.isNotEmpty()){
                if(typedOTP.length ==6){
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(otp, typedOTP)
                    signInWithPhoneAuthCredential(credential)

                    //auth內有一個create user using email and password 的函數
                    auth.createUserWithEmailAndPassword(email,password) //放入email and password
                        .addOnCompleteListener{

                            //判斷是否成功放入
                            if(it.isSuccessful){
                                Toast.makeText(this , "Register Successfully" , Toast.LENGTH_SHORT).show()
                                if (emailAsNode != null) {
                                    myRef.child(emailAsNode).setValue(users)
                                }
                                finish()
                            }else{
                                Toast.makeText(this , "Register Failed" , Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    Toast.makeText(this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resendVerificationCode(){

        //身份認證
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this , "Authenticate Successfully" , Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    //回條函數
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        //驗證完成
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        //驗證失敗
        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }

            // Show a message and update the UI
        }

        //發送代碼
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            otp = verificationId
            resendToken = token
        }
    }
}