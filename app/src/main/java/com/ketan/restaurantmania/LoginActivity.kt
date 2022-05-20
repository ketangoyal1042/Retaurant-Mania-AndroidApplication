package com.ketan.restaurantmania

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import model.Food
import model.UserCredentials
import org.json.JSONObject
import util.ConnectionManager

class LoginActivity : AppCompatActivity() {
    lateinit var txtRegister: TextView
    lateinit var txtForgot1: TextView
    lateinit var btnLoginButton: Button
    lateinit var etPhone: EditText
    lateinit var etPass: EditText
    lateinit var sharedpreferences: SharedPreferences
    lateinit var sharedPreferences_profie: SharedPreferences

    /*val usermob : String? = "1234567890"
    val userpass : String?= "123456"*/

/*    var Uname = mutableListOf<String?>("Ketan","sahil","kk")
    var Umobile = mutableListOf<String?>("8949485188","11","22")
    var Upassword = mutableListOf<String?>("123","11","22")*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences_profie = getSharedPreferences(getString(R.string.ProfilePref), Context.MODE_PRIVATE)
        sharedpreferences =  getSharedPreferences(getString(R.string.shared_file), Context.MODE_PRIVATE)

        val isLoggedIn = sharedpreferences.getBoolean("isLoggedIn",false)
        setContentView(R.layout.activity_login)

        if (isLoggedIn)
        {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        title = "Login"

      /*  if (intent!=null)
        {
            val phone:String? = intent.getStringExtra("mobile")
            val password:String? = intent.getStringExtra("password")
            val name:String? = intent.getStringExtra("name")
            Umobile.add(phone)
            Upassword.add(password)
            Uname.add(name)
        }*/

        etPhone = findViewById(R.id.etPhone)
        etPass = findViewById(R.id.etPass)
        txtForgot1 = findViewById(R.id.txtForgot1)
        btnLoginButton = findViewById(R.id.btnLoginButton)
        txtRegister = findViewById(R.id.txtRegister)


        btnLoginButton.setOnClickListener {
            val susername = etPhone.text.toString()
            val spassword = etPass.text.toString()

            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result/"



            if (ConnectionManager().checkConnectivity(this@LoginActivity))                 //Check for internet connection
            {
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", susername)
                jsonParams.put("password",spassword)

                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,

                    com.android.volley.Response.Listener {
                        try {
                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            if(success)
                            {
                                val credentialJsonObject = data1.getJSONObject("data")
                               /* val userObject = UserCredentials(
                                    credentialJsonObject.getString("user_id"),
                                    credentialJsonObject.getString("name"),
                                    credentialJsonObject.getString("email"),
                                    credentialJsonObject.getString("mobile_number"),
                                    credentialJsonObject.getString("address")
                                    )*/
                                val userid = credentialJsonObject.getString("user_id")
                                val name = credentialJsonObject.getString("name")
                                val email = credentialJsonObject.getString("email")
                                val mob = credentialJsonObject.getString("mobile_number")
                                val address = credentialJsonObject.getString("address")

                                sharedPreferences_profie.edit().putString("userId1", userid).apply()
                                sharedPreferences_profie.edit().putString("name", name).apply()
                                sharedPreferences_profie.edit().putString("email", email).apply()
                                sharedPreferences_profie.edit().putString("mob", mob).apply()
                                sharedPreferences_profie.edit().putString("address", address).apply()


                                finish()
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                sharedpreferences()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(intent)
                            }
                            else
                            {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Invalid UserName Or Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        catch (e: Exception)
                        {
                            Toast.makeText(
                                this@LoginActivity,
                                "An error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@LoginActivity,
                            "An Volley error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ){
                    override fun getHeaders(): MutableMap<String, String> {             //Send Header to API // send data to server
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "657017af6a3035"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            }

            else
            {
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("open Settings")
                {
                        text, listener ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit")
                {
                        text, listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }

        }



       txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        txtForgot1.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotActivity::class.java)
            startActivity(intent)
            finish()
        }




      /*  btnLoginButton.setOnClickListener {
            val susername = etPhone.text.toString()
            val spassword = etPass.text.toString()
            var i = 0
            var l =0
            var hold = 0
            while (i <= Umobile.size-1) { hold++
                if (susername == Umobile[i] && spassword == Upassword[i] ) {
                    val us = Uname[i]
                    finish()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    sharedpreferences(us)
                    startActivity(intent)

                    l=1
                }
                i++
            }
            if (l==0)
            {
                Toast.makeText(
                    this@LoginActivity,
                    "Invalid UserName Or Password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }*/
    }
    fun sharedpreferences()
    {
        sharedpreferences.edit().putBoolean("isLoggedIn", true).apply()

    }
}