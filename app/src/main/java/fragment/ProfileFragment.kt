package fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ketan.restaurantmania.R
import com.squareup.picasso.Picasso
import model.UserCredentials


class ProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtMob: TextView
    lateinit var txtEmail: TextView
    lateinit var txtLocation: TextView
    lateinit var sharedPreferences_profie: SharedPreferences

    var name : String? ="Name"
    var email : String? ="email"
    var mob : String? ="mob"
    var address : String? ="address"




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtName = view.findViewById(R.id.txtName)
        txtMob = view.findViewById(R.id.txtMob)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtLocation = view.findViewById(R.id.txtLocation)
        sharedPreferences_profie = requireContext().getSharedPreferences(getString(R.string.ProfilePref), Context.MODE_PRIVATE)

        name = sharedPreferences_profie.getString("name", "Undefined")
        mob = sharedPreferences_profie.getString("mob", "hello")
        email = sharedPreferences_profie.getString("email", "hello")
        address = sharedPreferences_profie.getString("address", "hello")
        txtName.text = name
        txtEmail.text = email
        txtMob.text = mob
        txtLocation.text = address





        return view
    }

}