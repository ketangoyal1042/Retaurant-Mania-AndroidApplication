package fragment

import adapter.CartRecyclerAdapter
import adapter.HistoryOuterRecyclerAdapter
import adapter.HomeRecyclerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ketan.restaurantmania.R
import model.Food
import model.HistoryInnerDetails
import model.HistoryOuterDetails
import org.json.JSONException
import util.ConnectionManager


class HistoryFragment : Fragment() {

    lateinit var recyclerHistory : RecyclerView
    lateinit var progressLayout : RelativeLayout
    lateinit var ProgressBar : ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter : HistoryOuterRecyclerAdapter
    lateinit var sharedPreferences_profie: SharedPreferences
    var UserID : String? = "100"
    var OrderOuterinfoList = arrayListOf<HistoryOuterDetails>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerHistory = view.findViewById(R.id.recyclerHistory)
        progressLayout = view.findViewById(R.id.progressLayout)
        ProgressBar = view.findViewById(R.id.ProgressBar)

        progressLayout.visibility = View.VISIBLE
        ProgressBar.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)
        sharedPreferences_profie = requireContext().getSharedPreferences(getString(R.string.ProfilePref), Context.MODE_PRIVATE)
        UserID = sharedPreferences_profie.getString("userId1", "undefined")


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/$UserID"

        if (ConnectionManager().checkConnectivity(activity as Context))
        {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url, null,

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
                                val orderJsonObject = data.getJSONObject(i)
                                val orderObject = HistoryOuterDetails(
                                    orderJsonObject.getString("order_id"),
                                    orderJsonObject.getString("restaurant_name"),
                                    orderJsonObject.getString("total_cost"),
                                    orderJsonObject.getString("order_placed_at")
                                )

                               /* val fooddetails = orderJsonObject.getJSONArray("food_items")
                                for(j in 0 until fooddetails.length())
                                {
                                    val foodJsonObject = fooddetails.getJSONObject(j)
                                    val orderObject2 = HistoryInnerDetails(
                                        foodJsonObject.getString("food_item_id"),
                                        foodJsonObject.getString("name"),
                                        foodJsonObject.getString("cost")
                                    )
                                    OrderInnerinfoList.add(orderObject2)
                                    recyclerAdapter = HistoryOuterRecyclerAdapter(activity as Context, OrderInnerinfoList)
                                    recyclerHistory.adapter = recyclerAdapter
                                    recyclerHistory.layoutManager = layoutManager
                                }*/

                                OrderOuterinfoList.add(orderObject)
                                recyclerAdapter = HistoryOuterRecyclerAdapter(activity as Context, OrderOuterinfoList, UserID)
                                recyclerHistory.adapter = recyclerAdapter
                                recyclerHistory.layoutManager = layoutManager
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
        return view
    }

}