package adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ketan.restaurantmania.ItemListActivity
import com.ketan.restaurantmania.R
import com.squareup.picasso.Picasso
import database.FoodDatabase
import database.FoodEntity

class FavouriteRecyclerAdapter(val context: Context, val itemList : List<FoodEntity>) :RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>()
{



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite_single_row,parent,false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {

        val food = itemList[position]
        holder.txtFoodName.text = food.foodName
        holder.txtFoodPrice.text = food.foodPrice
        holder.txtRating.text = food.foodRating
        Picasso.get().load(food.foodImage).error(R.drawable.profile).into(holder.imgFoodImage)

        val foodEntity = FoodEntity(
            food.foodId?.toInt() as Int,
            food.foodName.toString(),
            food.foodPrice.toString(),
            food.foodRating.toString(),
            food.foodImage.toString()
        )

        holder.imgFavButton.setOnClickListener()
        {
            val async = FavouriteRecyclerAdapter.DBAsyncTask(context, foodEntity).execute()
            val result = async.get()
            if (result)
            {
                Toast.makeText(context,"Restaurant removed from Favourite" , Toast.LENGTH_SHORT).show()
                holder.imgFavButton.setImageResource(R.drawable.ic_favnot)
                notifyDataSetChanged()
            }
        }

        holder.content.setOnClickListener{
            val intent = Intent(context, ItemListActivity::class.java)
            intent.putExtra("res_id",food.foodId.toString())
            intent.putExtra("res_name",food.foodName.toString())
            Toast.makeText(context,"${food.foodId}" , Toast.LENGTH_SHORT).show()
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val content : LinearLayout = view.findViewById(R.id.content)
        val imgFoodImage : ImageView = view.findViewById(R.id.imgFoodImage)
        val txtFoodName : TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice : TextView = view.findViewById(R.id.txtFoodPrice)
        val imgFavButton : ImageView = view.findViewById(R.id.imgFavButton)
        val txtRating : TextView = view.findViewById(R.id.txtRating)

    }


    class DBAsyncTask(val context: Context, val foodEntity: FoodEntity): AsyncTask<Void, Void, Boolean>()
    {
        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

                    db.foodDao().deleteFood(foodEntity)
                    db.close()
                    return true

        }
    }
}