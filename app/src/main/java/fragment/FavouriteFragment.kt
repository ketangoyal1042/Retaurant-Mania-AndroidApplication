package fragment

import adapter.FavouriteRecyclerAdapter
import adapter.HomeRecyclerAdapter
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ketan.restaurantmania.R
import database.FoodDatabase
import database.FoodEntity


class FavouriteFragment : Fragment() {

    lateinit var recyclerFavourite : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    lateinit var progressLayout : RelativeLayout
    lateinit var ProgressBar : ProgressBar
    var dbBookList = listOf<FoodEntity>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        ProgressBar = view.findViewById(R.id.ProgressBar)

        progressLayout.visibility = View.VISIBLE
        ProgressBar.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)

        dbBookList = RetrieveFav(activity as Context,).execute().get()
        if (activity!=null)
        {
            progressLayout.visibility= View.GONE
            ProgressBar.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbBookList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }

        return view
    }

    class RetrieveFav(val context: Context) : AsyncTask<Void, Void, List<FoodEntity>>()
    {
        override fun doInBackground(vararg params: Void?): List<FoodEntity> {
            val db = Room.databaseBuilder(context, FoodDatabase::class.java,"food-db").build()

            return db.foodDao().getAllFood()
        }



    }

}