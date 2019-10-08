package io.github.dsouzadyn.evently.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import io.github.dsouzadyn.evently.*
import io.github.dsouzadyn.evently.models.Event
import org.jetbrains.anko.alert
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.dropTable
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.yesButton


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_home, container, false)

        //val sponsorBtn = view.findViewById<CardView>(R.id.btnSponsors)
        val scannerBtn = view.findViewById<ImageButton>(R.id.scannerBtn)
        val collegeBtn = view.findViewById<CardView>(R.id.btnCollegeInfo)
        val etamaxBtn = view.findViewById<CardView>(R.id.btnAboutEtamax)
        val eventBtn = view.findViewById<CardView>(R.id.btnEvents)
        val committeeBtn = view.findViewById<CardView>(R.id.btnCommittee)
        //val logoutBtn = view.findViewById<MenuItem>(R.id.btnLogout)

        val sharedPref = context.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        val role = sharedPref.getInt(getString(R.string.urole_key), -1)

        if (role == 3) {  //role == 2
            scannerBtn.visibility = View.VISIBLE
            scannerBtn.setOnClickListener(View.OnClickListener {
                val scannerIntent = Intent(activity, ScannerActivity::class.java)
                startActivity(scannerIntent)
            })
        }

        /*logoutBtn.setOnClickListener(View.OnClickListener {
            onLogoutBtnClick()
        })*/

        //sponsorBtn.setOnClickListener(View.OnClickListener {
        //    onSponsorBtnClick()
        //})

        collegeBtn.setOnClickListener(View.OnClickListener {
            onInfoBtnClick("college")
        })

        etamaxBtn.setOnClickListener(View.OnClickListener {
            onInfoBtnClick("etamax")
        })

        eventBtn.setOnClickListener(View.OnClickListener {
            onEventBtnClick()
        })

        committeeBtn.setOnClickListener(View.OnClickListener {
            onComitteBtnClick()
        })

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event

    private fun onEventBtnClick() {
       /* val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
        transaction.replace(R.id.fragmentContainer, DayFragment.newInstance(1)).addToBackStack("events")
        transaction.commit()*/
        var intent = Intent(activity.applicationContext, EventActivity::class.java)
        activity.startActivity(intent)
    }

    private fun onSponsorBtnClick() {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
        transaction.replace(R.id.fragmentContainer, SponsorFragment.newInstance("","")).addToBackStack("sponsors")
        transaction.commit()
    }

    /*public fun onLogoutBtnClick() {
        context.alert("Do you really want to logout?") {
            yesButton {
                val sharedPref = context.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
                context.database.use {
                    val d = delete(Event.TABLE_NAME, "uid = '${activity.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE).getString(getString(R.string.uid_key), "")}'")
                }
                sharedPref.edit().clear().apply()
                activity.finish()
            }
            noButton {  }
        }.show()
    }*/

    private fun onInfoBtnClick(info: String) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
        transaction.replace(R.id.fragmentContainer, InformationFragment.newInstance(info)).addToBackStack("infos")
        transaction.commit()
    }

    private fun onComitteBtnClick() {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
        transaction.replace(R.id.fragmentContainer, BlankFragment.newInstance("", "")).addToBackStack("comittees")
        transaction.commit()
    }



    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
