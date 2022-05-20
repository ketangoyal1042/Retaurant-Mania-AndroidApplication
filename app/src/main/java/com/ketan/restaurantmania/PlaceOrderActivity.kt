package com.ketan.restaurantmania

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PlaceOrderActivity : AppCompatActivity() {
    lateinit var ok : Button
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        ok = findViewById(R.id.ok)

        ok.setOnClickListener {

            val intent = Intent(this@PlaceOrderActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}