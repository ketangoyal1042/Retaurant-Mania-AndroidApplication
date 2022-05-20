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
import model.UserCredentials
import org.json.JSONObject
import util.ConnectionManager

class ForgotActivity : AppCompatActivity() {
    lateinit var txtlogin2 : TextView
    lateinit var etPhone : EditText
    lateinit var etEmail : EditText
    lateinit var btnNext : Button
    lateinit var txtAlready : TextView
    lateinit var Forgot_mobsharedpreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        title = "Forgot Password"

        txtlogin2 = findViewById(R.id.txtLogin2)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)
        txtAlready = findViewById(R.id.txtAlready)
        Forgot_mobsharedpreferences =  getSharedPreferences(getString(R.string.ForgotMobPref), Context.MODE_PRIVATE)

        txtlogin2.setOnClickListener {
            val intent = Intent(this@ForgotActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        txtAlready.setOnClickListener {
            val intent = Intent(this@ForgotActivity, ResetActivity::class.java)
            startActivity(intent)
        }



        btnNext.setOnClickListener {

            val mobile1 = etPhone.text.toString()
            val email1 = etEmail.text.toString()
            Forgot_mobsharedpreferences.edit().putString("Mob", mobile1).apply()
           /* if (mobile.length < 10)
            {
                Toast.makeText(
                    this@ForgotActivity,
                    "Phone Should not NULL and ( Contain min 10 Digit)",
                    Toast.LENGTH_SHORT
                ).show()

            }*/

           /* if (email.isEmpty()) {
                Toast.makeText(this@ForgotActivity, "Email can not be Empty", Toast.LENGTH_SHORT)
                    .show()
            }*/

           /* else
            {*/
                val queue = Volley.newRequestQueue(this@ForgotActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                if (ConnectionManager().checkConnectivity(this@ForgotActivity))                 //Check for internet connection
                {
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", mobile1)
                    jsonParams.put("email",email1)

                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams,

                        com.android.volley.Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")

                                if(success)
                                {
                                    val firsttry = data.getBoolean("first_try")
                                    if (firsttry) {
                                        val intent = Intent(this@ForgotActivity, ResetActivity::class.java)
                                        Toast.makeText(
                                            this@ForgotActivity,
                                            "OTP Sent Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                        startActivity(intent)
                                    }
                                    else
                                    {
                                        Toast.makeText(
                                            this@ForgotActivity,
                                            "OTP sent already | Was Valid for 24 Hours",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                else
                                {
                                    Toast.makeText(
                                        this@ForgotActivity,
                                        "Some error Occurred or User Not Registered",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            catch (e: Exception)
                            {
                                Toast.makeText(
                                    this@ForgotActivity,
                                    "An error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@ForgotActivity,
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
                    val dialog = AlertDialog.Builder(this@ForgotActivity)
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
                        ActivityCompat.finishAffinity(this@ForgotActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
           // }
        }
    }
}