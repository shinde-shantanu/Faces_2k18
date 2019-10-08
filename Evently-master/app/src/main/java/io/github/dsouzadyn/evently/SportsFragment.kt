package io.github.dsouzadyn.evently

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import io.github.dsouzadyn.evently.adapters.EventRecyclerViewAdapter
import io.github.dsouzadyn.evently.adapters.EventRecyclerViewAdapterSports
import io.github.dsouzadyn.evently.models.EventContent
import io.github.dsouzadyn.evently.models.EventContentSports
import org.jetbrains.anko.support.v4.indeterminateProgressDialog


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SportsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SportsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SportsFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var uid = ""
    val eventType = "sports"
    private var mColumnCount = 1
    private var mUid = ""

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
        val sharedPref = activity.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        uid = sharedPref.getString(getString(R.string.uid_key), "")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_event_list, container, false)

        var events : List<EventActivity.Event>? = null
        var error: FuelError? = null

        EventContentSports.ITEMS.clear()
        EventContentSports.clearitemslist()
        val progressDialog = indeterminateProgressDialog("Fetching evennts...")
        val sharedPref = activity.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPref.getString(getString(R.string.token_key), "")
        FuelManager.instance.baseHeaders = mapOf("Authorization" to token)
        progressDialog.show()
        "${getString(R.string.temp_url)}/event?type=$eventType".httpGet().responseObject(EventActivity.Event.Deserializer()) { _, _, result ->    //og=event?cumpolsory=$isCumpolsory
            events = result.component1()
            error = result.component2()
            if(error == null) {
                progressDialog.dismiss()
                if(events != null) {
//                    for (event in events!!) {
//                        Log.d("EVENT", event.name)
//                     }
                    var i = 0
                    events = events?.sortedWith(compareBy({ it.name }))
                    events?.filterIndexed { index, value ->

                        value.type.equals(eventType)                 //og=value.cumpolsory == isCumpolsory
                    }?.forEach { e ->
                        EventContentSports.addItem(
                                EventContentSports.createEventItem(
                                        i++,
                                        e.id,
                                        e.name,
                                        e.description,
                                        e.capacity,
                                        e.start_time,
                                        e.end_time,
                                        e.start_time2,
                                        e.end_time2,
                                        e.start_time3,
                                        e.end_time3,
                                        e.price,
                                        e.type,
                                        e.subtype,
                                        e.location,
                                        e.cumpolsory
                                ))
                    }
                    if (EventContentSports.ITEMS.size > 0) {
                        progressDialog.dismiss()
                        //toast("suc")
                        if (view is RecyclerView) {
                            val context = view.getContext()
                            if (mColumnCount <= 1) {
                                view.layoutManager = LinearLayoutManager(context)
                            } else {
                                view.layoutManager = GridLayoutManager(context, mColumnCount)
                            }
                            view.adapter = EventRecyclerViewAdapterSports(EventContentSports.ITEMS, uid)
                        }
                    }
                }
                else{
                    progressDialog.dismiss()
                    Log.e("ERROR", error!!.message)
                }
            }
        }
        // Set the adapter
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    /*override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }*/

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
         * @return A new instance of fragment CulturalFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): CulturalFragment {
            val fragment = CulturalFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
