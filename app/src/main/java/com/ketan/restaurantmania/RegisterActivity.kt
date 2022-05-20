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

class RegisterActivity : AppCompatActivity() {

    lateinit var etEmail : EditText
    lateinit var etName : EditText
    lateinit var etPhone : EditText
    lateinit var etAddress : EditText
    lateinit var etPass : EditText
    lateinit var etPassConfirm : EditText
    lateinit var btnLoginButton : Button
    lateinit var txtLogin1: TextView
    lateinit var sharedpreferences: SharedPreferences
    lateinit var sharedPreferences_profie: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        title = "Register Yourself"


        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etPass = findViewById(R.id.etPass)
        etPassConfirm = findViewById(R.id.etPassConfirm)
        btnLoginButton = findViewById(R.id.btnLoginButton)
        txtLogin1 = findViewById(R.id.txtLogin1)
        sharedpreferences =  getSharedPreferences(getString(R.string.shared_file), Context.MODE_PRIVATE)
        sharedPreferences_profie = getSharedPreferences(getString(R.string.ProfilePref), Context.MODE_PRIVATE)

        btnLoginButton.setOnClickListener {

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val address = etAddress.text.toString()
            val password1 = etPass.text.toString()
            val password2 = etPassConfirm.text.toString()

            if(name.length<3 || name.isEmpty())
            {
                Toast.makeText(this@RegisterActivity, "Name must contain Min 3 Char", Toast.LENGTH_SHORT).show()
            }
            else if (email.isEmpty())
            {
                Toast.makeText(this@RegisterActivity, "Email can not be Empty", Toast.LENGTH_SHORT).show()
            }
            else if (phone.length<10)
            {
                Toast.makeText(this@RegisterActivity, "Phone Should not NULL and ( Contain min 10 Digit)", Toast.LENGTH_SHORT).show()
            }
            else if (address.isEmpty())
            {
                Toast.makeText(this@RegisterActivity, "Address cannot NULL", Toast.LENGTH_SHORT).show()
            }
            else if (password1!=password2 || password1.isEmpty() || password2.isEmpty() || password1.length<5 || password2.length<5)
            {
                Toast.makeText(this@RegisterActivity, "Password Should be Same and can not NULL", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val queue = Volley.newRequestQueue(this@RegisterActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"




                if (ConnectionManager().checkConnectivity(this@RegisterActivity))                 //Check for internet connection
                {
                    val jsonParams = JSONObject()
                    jsonParams.put("name", name)
                    jsonParams.put("mobile_number", phone)
                    jsonParams.put("password",password1)
                    jsonParams.put("address",address)
                    jsonParams.put("email",email)

                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams,

                        com.android.volley.Response.Listener {
                            try {
                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")
                                if(success)
                                {
                                    val credentialJsonObject = data1.getJSONObject("data")
                                    val userObject = UserCredentials(
                                        credentialJsonObject.getString("user_id"),
                                        credentialJsonObject.getString("name"),
                                        credentialJsonObject.getString("email"),
                                        credentialJsonObject.getString("mobile_number"),
                                        credentialJsonObject.getString("address")
                                    )

                                    val userid1 = credentialJsonObject.getString("user_id")
                                    val name1 = credentialJsonObject.getString("name")
                                    val email1 = credentialJsonObject.getString("email")
                                    val mob1 = credentialJsonObject.getString("mobile_number")
                                    val address1 = credentialJsonObject.getString("address")

                                    sharedPreferences_profie.edit().putString("userId1", userid1).apply()
                                    sharedPreferences_profie.edit().putString("name", name1).apply()
                                    sharedPreferences_profie.edit().putString("email", email1).apply()
                                    sharedPreferences_profie.edit().putString("mob", mob1).apply()
                                    sharedPreferences_profie.edit().putString("address", address1).apply()

                                    finish()
                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    sharedpreferences()
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Register Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(intent)
                                }
                                else
                                {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "User Already Registered or Something Went Wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            catch (e: Exception)
                            {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "An error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@RegisterActivity,
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
                    val dialog = AlertDialog.Builder(this@RegisterActivity)
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
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    dialog.create()
                    dialog.show()
                }


            }
        }


        txtLogin1.setOnClickListener{
            val intent = Intent(this@RegisterActivity,LoginActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    fun sharedpreferences()
    {
        sharedpreferences.edit().putBoolean("isLoggedIn", true).apply()
    }

}