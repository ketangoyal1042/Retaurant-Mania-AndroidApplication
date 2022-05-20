package adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ketan.restaurantmania.CartActivity
import com.ketan.restaurantmania.R
import model.Menu

class ItemListRecyclerAdapter(val context: Context, val itemList : ArrayList<Menu>, val btngotocart : Button, val RestaurantName : String): RecyclerView.Adapter<ItemListRecyclerAdapter.ItemListHolder>()
{

    var count: Int=0
    var itemsSelectedId= arrayListOf<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_single_row,parent,false)
        return ItemListHolder(view)
    }

    override fun onBindViewHolder(holder: ItemListHolder, position: Int)
    {
        val menu = itemList[position]
        holder.txtMenuFoodName.text = menu.MenuName
        holder.txtMenuFoodPrice.text = "Rs. "+menu.MenuCost
        holder.couning.text = (position+1).toString()



        holder.btnAdd.setOnClickListener {

            if (holder.btnAdd.text.toString() == "Add")
            {
                    count++
                    holder.btnAdd.text = "Remove"
                    val favcolor = ContextCompat.getColor(context, R.color.yellow)
                    holder.btnAdd.setBackgroundColor(favcolor)
                itemsSelectedId.add(menu.MenuId)
//                    Toast.makeText(context, "${menu.MenuName} Added to cart", Toast.LENGTH_SHORT).show()
            }
            else
            {
                    count--
                    holder.btnAdd.text = "Add"
                    val favcolor = ContextCompat.getColor(context, R.color.redis)
                    holder.btnAdd.setBackgroundColor(favcolor)
                itemsSelectedId.remove(menu.MenuId)
//                    Toast.makeText(context, "${menu.MenuName} Removed from cart", Toast.LENGTH_SHORT).show()
            }

            if (count==0)
            {
                btngotocart.visibility=View.GONE
            }
            else
            {
                btngotocart.visibility=View.VISIBLE
            }
        }

        btngotocart.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("Restaurant_name",RestaurantName)
            intent.putExtra("Restaurant_id",menu.RestaurantID)
                intent.putExtra("AllMenu_id",itemsSelectedId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int
    {
        return itemList.size
    }

    class ItemListHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val AllContent : LinearLayout = view.findViewById(R.id.AllContent)
        val couning : TextView = view.findViewById(R.id.couning)
        val txtMenuFoodName : TextView = view.findViewById(R.id.txtMenuFoodName)
        val txtMenuFoodPrice : TextView = view.findViewById(R.id.txtMenuFoodPrice)
        val btnAdd : Button = view.findViewById(R.id.btnAdd)
    }
}