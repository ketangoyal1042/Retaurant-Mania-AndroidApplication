package fragment

import adapter.HomeRecyclerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ketan.restaurantmania.R
import model.Food
import org.json.JSONException
import util.ConnectionManager
import java.util.*
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    lateinit var recyclerHome : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout : RelativeLayout
    lateinit var ProgressBar : ProgressBar
    lateinit var radioview : View
    lateinit var etsearchbar : EditText
    var FoodinfoList = arrayListOf<Food>()
    var previous : MenuItem? = null

    var ratingComparator = Comparator<Food>{ res1, res2 ->

        if (res1.FoodRating.compareTo(res2.FoodRating, true)  == 0)
        {
            res1.FoodName.compareTo(res2.FoodName, true)
        }
        else
        {
            res1.FoodRating.compareTo(res2.FoodRating, true)
        }
    }

    var lowtohighComparator = Comparator<Food> { res1, res2 ->

        if (res1.FoodCost.compareTo(res2.FoodCost, true)  == 0)
        {
            res1.FoodName.compareTo(res2.FoodName, true)
        }
        else
        {
            res1.FoodCost.compareTo(res2.FoodCost, true)
        }
    }

    var hightolowComparator = Comparator<Food> { res1, res2 ->

        if (res1.FoodCost.compareTo(res2.FoodCost, true)  == 0)
        {
            res1.FoodName.compareTo(res2.FoodName, true)
        }
        else
        {
            res1.FoodCost.compareTo(res2.FoodCost, true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        progressLayout = view.findViewById(R.id.progressLayout)
        ProgressBar = view.findViewById(R.id.ProgressBar)
        etsearchbar = view.findViewById(R.id.etsearchbar)
        progressLayout.visibility = View.VISIBLE
        ProgressBar.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context))
        {
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,

                Response.Listener
                {
                  try
                  {
                      progressLayout.visibility = View.GONE
                      ProgressBar.visibility = View.GONE

                      val data1 = it.getJSONObject("data")
                      val success = data1.getBoolean("success")
                      if(success)
                      {
                          //val obj = it.getJSONObject("data")
                          val data = data1.getJSONArray("data")
                          for(i in 0 until data.length())
                          {
                              val foodJsonObject = data.getJSONObject(i)
                              val foodObject = Food(
                                  foodJsonObject.getString("id"),
                                  foodJsonObject.getString("name"),
                                  foodJsonObject.getString("rating"),
                                  foodJsonObject.getString("cost_for_one"),
                                  foodJsonObject.getString("image_url"),
                              )
                              FoodinfoList.add(foodObject)
                              recyclerAdapter = HomeRecyclerAdapter(activity as Context, FoodinfoList)
                              recyclerHome.adapter = recyclerAdapter
                              recyclerHome.layoutManager = layoutManager
                          }
                      }
                      else
                      {
                          Toast.makeText(activity as Context, "Some Error Accured", Toast.LENGTH_SHORT).show()
                      }
                  }
                  catch (e: JSONException)
                  {
                      Toast.makeText(activity as Context, "Some Unexpected Error Accured", Toast.LENGTH_SHORT).show()
                  }
                },

                Response.ErrorListener {
                    if(activity!=null) {
                        progressLayout.visibility = View.GONE
                        ProgressBar.visibility = View.GONE
                        println("error is"+it)
                        Toast.makeText(
                            activity as Context,
                            "Some volleys Error Accured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
            {
                override fun getHeaders(): MutableMap<String, String> {             //Send Header to API // send data to server
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "657017af6a3035"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }//end of if network
        else
        {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("open Settings")
            {
                    text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit")
            {
                    text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        fun filterFun(strTyped: String)
        {
            val filterList = arrayListOf<Food>()

            for (item in FoodinfoList)
            {
                if(item.FoodName.toLowerCase().contains(strTyped.toLowerCase()))
                {
                    filterList.add(item)
                }
            }
            recyclerAdapter.filteredList(filterList)
        }
        etsearchbar.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, p1:Int, p2:Int, p3:Int){

            }
            override fun onTextChanged(p0: CharSequence?, p1:Int, p2:Int, p3:Int){

            }

            override fun afterTextChanged(strTyped: Editable?)
            {
                filterFun(strTyped.toString())
            }
        })


        return view
    }  // end of oncreateview

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
       inflater.inflate(R.menu.sort_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        val id = item.itemId
        item.isChecked = true
        item.isCheckable = true

        when(id)
        {
            R.id.Rating ->
            {
                Collections.sort(FoodinfoList,ratingComparator)
                FoodinfoList.reverse()
                recyclerAdapter.notifyDataSetChanged()
            }

            R.id.Low_to_High ->
            {
                Collections.sort(FoodinfoList,lowtohighComparator)
                recyclerAdapter.notifyDataSetChanged()
            }
            R.id.High_to_Low ->
            {
                Collections.sort(FoodinfoList,hightolowComparator)
                FoodinfoList.reverse()
                recyclerAdapter.notifyDataSetChanged()
            }
        }


        return super.onOptionsItemSelected(item)
    }

}