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
import model.Food

class HomeRecyclerAdapter(val context: Context, var itemList: ArrayList<Food>) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>()
{



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val food = itemList[position]
        holder.txtFoodName.text = food.FoodName
        holder.txtFoodPrice.text = "₹ "+food.FoodCost+"/person"
        holder.txtRating.text = food.FoodRating
        Picasso.get().load(food.Foodimg).error(R.drawable.profile).into(holder.imgFoodImage)


        val foodEntity = FoodEntity(
            food.FoodId.toInt() as Int,
            food.FoodName.toString(),
            food.FoodCost.toString(),
            food.FoodRating.toString(),
            food.Foodimg.toString()
        )



        val checkFav = DBAsyncTask(context, foodEntity , 1).execute()
        val isFav = checkFav.get()

        if (isFav)
        {
            holder.imgFavButton.setImageResource(R.drawable.ic_fav)
        }
        else
        {
            holder.imgFavButton.setImageResource(R.drawable.ic_favnot)
        }
        holder.imgFavButton.setOnClickListener {

            if (!DBAsyncTask(context, foodEntity, 1).execute().get())
            {
                val async = DBAsyncTask(context, foodEntity, 2).execute()
                val result = async.get()
                if (result)
                {
                    Toast.makeText(context,"Restaurant added to Favourite" , Toast.LENGTH_SHORT).show()
                    holder.imgFavButton.setImageResource(R.drawable.ic_fav)
                }
            }
            else
            {
                val async = DBAsyncTask(context, foodEntity, 3).execute()
                val result = async.get()
                if (result)
                {
                    Toast.makeText(context,"Restaurant removed from Favourite" , Toast.LENGTH_SHORT).show()
                    holder.imgFavButton.setImageResource(R.drawable.ic_favnot)
                }
            }
        }

        holder.content.setOnClickListener{


            val intent = Intent(context, ItemListActivity::class.java)
            intent.putExtra("res_id",food.FoodId)
            intent.putExtra("res_name",food.FoodName)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val content : LinearLayout = view.findViewById(R.id.content)
        val imgFoodImage : ImageView = view.findViewById(R.id.imgFoodImage)
        val txtFoodName : TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice : TextView = view.findViewById(R.id.txtFoodPrice)
        val imgFavButton : ImageView = view.findViewById(R.id.imgFavButton)
        val txtRating : TextView = view.findViewById(R.id.txtRating)
    }

    fun filteredList(filterList: ArrayList<Food>)
    {
        itemList = filterList
        notifyDataSetChanged()
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