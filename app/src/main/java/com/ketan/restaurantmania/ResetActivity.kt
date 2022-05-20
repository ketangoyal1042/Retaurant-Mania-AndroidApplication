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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import model.UserCredentials
import org.json.JSONObject
import util.ConnectionManager

class ResetActivity : AppCompatActivity() {

    lateinit var etOTP : EditText
    lateinit var etPass : EditText
    lateinit var etPass1 : EditText
    lateinit var btnLoginButton : Button
    lateinit var Forgot_mobsharedpreferences : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        etOTP = findViewById(R.id.etOTP)
        etPass = findViewById(R.id.etPass)
        etPass1 = findViewById(R.id.etPass1)
        btnLoginButton = findViewById(R.id.btnLoginButton)
        Forgot_mobsharedpreferences =  getSharedPreferences(getString(R.string.ForgotMobPref), Context.MODE_PRIVATE)


        btnLoginButton.setOnClickListener {

            val OTP = etOTP.text.toString()
            val pass = etPass.text.toString()
            val passConfirm = etPass1.text.toString()

            if (pass == passConfirm) {
                val mob = Forgot_mobsharedpreferences.getString("Mob", null)

                val queue = Volley.newRequestQueue(this@ResetActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                if (ConnectionManager().checkConnectivity(this@ResetActivity))                 //Check for internet connection
                {
                    val jsonParams = JSONObject()

                    jsonParams.put("mobile_number", mob)
                    jsonParams.put("password", pass)
                    jsonParams.put("otp", OTP)


                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams,

                        com.android.volley.Response.Listener {
                            try {
                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")
                                if (success) {
                                    val Message = data1.getString("successMessage")

                                    Toast.makeText(
                                        this@ResetActivity,
                                        Message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    finish()
                                    val intent = Intent(this@ResetActivity, LoginActivity::class.java)
                                    startActivity(intent)

                                } else {
                                    Toast.makeText(
                                        this@ResetActivity,
                                        "Something Went Wrong | Try Again Later",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@ResetActivity,
                                    "An error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@ResetActivity,
                                "An Volley error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        override fun getHeaders(): MutableMap<String, String> {             //Send Header to API // send data to server
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "657017af6a3035"
                            return headers
                        }
                    }
                    queue.add(jsonRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@ResetActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("open Settings")
                    { text, listener ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit")
                    { text, listener ->
                        ActivityCompat.finishAffinity(this@ResetActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            else
            {
                Toast.makeText(
                    this@ResetActivity,
                    "Password Must be same | Check Again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}