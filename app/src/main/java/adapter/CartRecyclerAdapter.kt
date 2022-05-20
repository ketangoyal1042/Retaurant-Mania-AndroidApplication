package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ketan.restaurantmania.R
import model.Cart

class CartRecyclerAdapter(val context: Context, val itemList : ArrayList<Cart>): RecyclerView.Adapter<CartRecyclerAdapter.CartHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent, false)
        return  CartHolder(view)
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val cartitem = itemList[position]
        holder.txtCartFoodName.text = cartitem.CartMenuName
        holder.txtCartFoodCost.text = "Rs. "+cartitem.CartMenuPrice+"/-"
        holder.couning.text = (position+1).toString()
    }

    override fun getItemCount(): Int {
       return  itemList.size
    }

    class CartHolder(view: View): RecyclerView.ViewHolder(view)
    {
             val AllContent : LinearLayout = view.findViewById(R.id.AllContent)
            val couning : TextView = view.findViewById(R.id.couning)
            val txtCartFoodName : TextView = view.findViewById(R.id.txtCartFoodName)
            val txtCartFoodCost : TextView = view.findViewById(R.id.txtCartFoodCost)
    }






}