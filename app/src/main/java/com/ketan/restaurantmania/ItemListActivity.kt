package com.ketan.restaurantmania

import adapter.ItemListRecyclerAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import database.FoodDatabase
import database.FoodEntity
import model.Food
import model.Menu
import util.ConnectionManager

class ItemListActivity : AppCompatActivity() {

    lateinit var ToolBarLayout : androidx.appcompat.widget.Toolbar
    lateinit var imgFavButton : ImageView
    lateinit var progressLayout : RelativeLayout
    lateinit var ProgressBar : ProgressBar
    lateinit var recyclerItemList : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : ItemListRecyclerAdapter
    lateinit var btngotocart : Button

    var Foodid : String?= "100"
    var RestName : String? = "abcd"
    lateinit var RestaurantName : String
    val MenuinfoList = arrayListOf<Menu>()
    var FoodinfoList = arrayListOf<Food>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        ToolBarLayout = findViewById(R.id.ToolBarLayout)
        imgFavButton  = findViewById(R.id.imgFavButton)
        progressLayout = findViewById(R.id.progressLayout)
        ProgressBar = findViewById(R.id.ProgressBar)
        recyclerItemList = findViewById(R.id.recyclerItemList)
        btngotocart = findViewById(R.id.btngotocart)

        progressLayout.visibility = View.VISIBLE
        ProgressBar.visibility = View.VISIBLE
        btngotocart.visibility=View.GONE
        layoutManager = LinearLayoutManager(this)
        setuptoolbar()



        if (intent!=null)
        {
            Foodid = intent.getStringExtra("res_id")
            RestName = intent.getStringExtra("res_name")
            RestaurantName = RestName.toString()
        }
        else
        {
            finish()
            Toast.makeText(this@ItemListActivity,"An name error Accured", Toast.LENGTH_SHORT).show()
        }

        if (Foodid=="100")
        {
            finish()
            Toast.makeText(this@ItemListActivity,"An name2 error Accured", Toast.LENGTH_SHORT).show()
        }

        supportActionBar?.title = RestName
        ToolBarLayout.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
            finish()
        })

        val queue = Volley.newRequestQueue(this@ItemListActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${Foodid?.toInt()}"

        if (ConnectionManager().checkConnectivity(this@ItemListActivity))                 //Check for internet connection
        {
            val jsonRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                com.android.volley.Response.Listener {
                    try {

                        progressLayout.visibility = View.GONE
                        ProgressBar.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if(success)
                        {
                            val data1 = data.getJSONArray("data")
                            println("Error is $it")
                            for(i in 0 until data1.length())
                            {
                                val menuJsonObject = data1.getJSONObject(i)
                                val menuObject = Menu(
                                    menuJsonObject.getString("id"),
                                    menuJsonObject.getString("name"),
                                    menuJsonObject.getString("cost_for_one"),
                                    menuJsonObject.getString("restaurant_id")
                                )
                                println("error is $it")
                                MenuinfoList.add(menuObject)
                                recyclerAdapter = ItemListRecyclerAdapter(this, MenuinfoList, btngotocart, RestaurantName)

                                recyclerItemList.adapter = recyclerAdapter
                                recyclerItemList.layoutManager = layoutManager
                            }
                        }

                        else {
                            println("error is $it")
                            Toast.makeText(
                                this@ItemListActivity,
                                "An else error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ItemListActivity,
                            "An catch error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                com.android.volley.Response.ErrorListener
                {
                    Toast.makeText(
                        this@ItemListActivity,
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
            val dialog = AlertDialog.Builder(this@ItemListActivity)
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
                ActivityCompat.finishAffinity(this@ItemListActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    fun setuptoolbar()
    {
        setSupportActionBar(ToolBarLayout)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed()
    {

        super.onBackPressed()
    }

    class DBAsyncTask(val context: Context, val foodEntity: FoodEntity, val mode: Int): AsyncTask<Void, Void, Boolean>()
    {
        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode)
            {
                1 ->
                {
                    val food: FoodEntity? = db.foodDao().getFoodByID(foodEntity.foodId.toString())
                    db.close()
                    return food!=null
                }
                2->
                {
                    db.foodDao().insertFood(foodEntity)
                    db.close()
                    return true
                }

                3->
                {
                    db.foodDao().deleteFood(foodEntity)
                    db.close()
                    return true
                }

            }
            return false
        }
    }


}