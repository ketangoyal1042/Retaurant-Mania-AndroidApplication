package adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ketan.restaurantmania.R
import model.Cart
import model.HistoryInnerDetails
import model.HistoryOuterDetails
import org.json.JSONException
import util.ConnectionManager

class HistoryOuterRecyclerAdapter(val context: Context, val itemList : ArrayList<HistoryOuterDetails>, val UserID : String?): RecyclerView.Adapter<HistoryOuterRecyclerAdapter.HistoryOuterHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryOuterHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_outer_history_single_row,parent,false)
        return HistoryOuterHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryOuterHolder, position: Int) {
        val HistoryOuter = itemList[position]
        holder.txtHistoryRetaurantName.text = HistoryOuter.RestaurantName
        holder.txtHistoryTimeAndDate.text = HistoryOuter.TimeAndDate.replace("-","/").take(8)
        var recyclerAdapter : CartRecyclerAdapter
        var OrderPerinfoList = arrayListOf<Cart>()
        val layoutManager = LinearLayoutManager(context)
        val queue = Volley.newRequestQueue(context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/${UserID?.toInt()}"

        if (ConnectionManager().checkConnectivity(context))
        {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url, null,
                Response.Listener
                {
                    try
                    {
                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if(success)
                        {
                            //val obj = it.getJSONObject("data")
                            val data = data1.getJSONArray("data")
                                val orderJsonObject = data.getJSONObject(position)


                                 val fooddetails = orderJsonObject.getJSONArray("food_items")
                                 for(j in 0 until fooddetails.length())
                                 {
                                     val foodJsonObject = fooddetails.getJSONObject(j)
                                     val orderObject2 = Cart(
                                         foodJsonObject.getString("name"),
                                         foodJsonObject.getString("cost")
                                     )
                                     OrderPerinfoList.add(orderObject2)
                                     recyclerAdapter = CartRecyclerAdapter(context, OrderPerinfoList)
                                     holder.recyclerInRecyclerHistory.adapter = recyclerAdapter
                                     holder.recyclerInRecyclerHistory.layoutManager = layoutManager
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "Some Error Accured", Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e: JSONException)
                    {
                        Toast.makeText(context, "Some Unexpected Error Accured", Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {

                        println("error is"+it)
                        Toast.makeText(
                            context,
                            "Some volleys Error Accured",
                            Toast.LENGTH_SHORT
                        ).show()

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

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class HistoryOuterHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val txtHistoryRetaurantName : TextView = view.findViewById(R.id.txtHistoryRetaurantName)
        val txtHistoryTimeAndDate : TextView = view.findViewById(R.id.txtHistoryTimeAndDate)
        val recyclerInRecyclerHistory : RecyclerView = view.findViewById(R.id.recyclerInRecyclerHistory)
    }
}