package com.ketan.restaurantmania

import adapter.CartRecyclerAdapter
import adapter.ItemListRecyclerAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import model.Cart
import model.Menu
import org.json.JSONArray
import org.json.JSONObject
import util.ConnectionManager

class CartActivity : AppCompatActivity() {

    lateinit var recyclerCart : RecyclerView
    lateinit var ToolBarLayout1 : androidx.appcompat.widget.Toolbar
    lateinit var btnPlaceOrder : Button
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : CartRecyclerAdapter
    lateinit var txtRestaurantName : TextView
    lateinit var sharedPreferences_profie: SharedPreferences
    val cartinfolist = ArrayList<Cart>()
    var Restaurant_name :String? = "def"
    var Restaurant_id : String? = "def123"
    var UserID : String? = "100"
    lateinit var AllMenu_id : ArrayList<String>// used as intent




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        ToolBarLayout1 = findViewById(R.id.ToolBarLayout1)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        txtRestaurantName = findViewById(R.id.txtRestaurantName)
        layoutManager = LinearLayoutManager(this)
        recyclerCart = findViewById(R.id.recyclerCart)
        sharedPreferences_profie = getSharedPreferences(getString(R.string.ProfilePref), Context.MODE_PRIVATE)
        if (intent!=null)
        {
            Restaurant_name = intent.getStringExtra("Restaurant_name")
            Restaurant_id = intent.getStringExtra("Restaurant_id")
            AllMenu_id = intent.getStringArrayListExtra("AllMenu_id") as ArrayList<String>
        }
        else
        {
            finish()
            Toast.makeText(this@CartActivity,"An unknown error Accured", Toast.LENGTH_SHORT).show()
        }
        txtRestaurantName.text = Restaurant_name.toString()
        setSupportActionBar(ToolBarLayout1)
        println(" the value $AllMenu_id")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ToolBarLayout1.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
            finish()
        })

        var total_price :Int = 0
        supportActionBar?.title = "My Cart"
        UserID = sharedPreferences_profie.getString("userId1", "undefined")

        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${Restaurant_id?.toInt()}"


        if (ConnectionManager().checkConnectivity(this@CartActivity))                 //Check for internet connection
        {
            val jsonRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                com.android.volley.Response.Listener {
                    try {

                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if(success)
                        {
                            val data2 = data1.getJSONArray("data")
//
                            for(i in 0 until data2.length())
                            {
                                val cartJsonObject = data2.getJSONObject(i)
                                if(AllMenu_id.contains(cartJsonObject.getString("id")))
                                {

                                    val cartObject = Cart(
                                        cartJsonObject.getString("name"),
                                        cartJsonObject.getString("cost_for_one")
                                    )
                                    total_price +=  cartJsonObject.getString("cost_for_one").toInt()
                                    println("error is $it")
                                    cartinfolist.add(cartObject)
                                }
                                    recyclerAdapter = CartRecyclerAdapter(this, cartinfolist)

                                recyclerCart.adapter = recyclerAdapter

                                recyclerCart.layoutManager = layoutManager
                            }
                            btnPlaceOrder.text = "PLACE ORDER (Total Rs."+total_price.toString()+")"
                        }

                        else {
                            println("error is $it")
                            Toast.makeText(
                                this@CartActivity,
                                "An else error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@CartActivity,
                            "An catch error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                com.android.volley.Response.ErrorListener
                {
                    Toast.makeText(
                        this@CartActivity,
                        "An volley error Occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {             //Send Header to API // send data to server
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "657017af6a3035"
                    return headers
                }
            }
            queue.add(jsonRequest)
        }//end of if
        else
        {
            val dialog = AlertDialog.Builder(this@CartActivity)
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
                ActivityCompat.finishAffinity(this@CartActivity)
            }
            dialog.create()
            dialog.show()
        }// end of else

//---------------------------------------------
        btnPlaceOrder.setOnClickListener {

            val foodJsonArray = JSONArray()
            for(i in AllMenu_id)
            {
                val foodObject = JSONObject()
                foodObject.put("food_item_id",i)
                foodJsonArray.put(foodObject)
            }
            val queue1 = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"

            val jsonParams = JSONObject()
            jsonParams.put("user_id", UserID)
            jsonParams.put("restaurant_id",Restaurant_id)
            jsonParams.put("total_cost",total_price)
            jsonParams.put("food",foodJsonArray)

            if (ConnectionManager().checkConnectivity(this@CartActivity))                 //Check for internet connection
            {
                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                    com.android.volley.Response.Listener {
                        try {

                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            if(success)
                            {

                                val intent = Intent(this@CartActivity, PlaceOrderActivity::class.java)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@CartActivity, "Order Placed Successfully", Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                println("error is $it")
                                Toast.makeText(
                                    this@CartActivity,
                                    "something went wrong, Try again later",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@CartActivity,
                                "An catch error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    com.android.volley.Response.ErrorListener
                    {
                        Toast.makeText(
                            this@CartActivity,
                            "An volley error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {             //Send Header to API // send data to server
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "657017af6a3035"
                        return headers
                    }
                }
                queue1.add(jsonRequest)
            }//end of if
            else
            {
                val dialog = AlertDialog.Builder(this@CartActivity)
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
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }// end of else
        }

    }// end of on create

    override fun onBackPressed()
    {

        super.onBackPressed()
    }


}