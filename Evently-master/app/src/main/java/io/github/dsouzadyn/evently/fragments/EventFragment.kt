package io.github.dsouzadyn.evently.fragments


import android.content.Context
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
import io.github.dsouzadyn.evently.EventActivity
import io.github.dsouzadyn.evently.MainActivity
import io.github.dsouzadyn.evently.R
import io.github.dsouzadyn.evently.adapters.EventRecyclerViewAdapter

import io.github.dsouzadyn.evently.models.EventContent
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.indeterminateProgressDialog

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class EventFragment : Fragment() {
    // TODO: Customize parameters
    private var mColumnCount = 1
    private var mUid = ""
    val eventType = "fun"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
            mUid = arguments.getString(ARG_UID)
        }
        val sharedPref = activity.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        val uid = sharedPref.getString(getString(R.string.uid_key), "")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_event_list, container, false)

        var events : List<EventActivity.Event>? = null
        var error: FuelError? = null

        EventContent.ITEMS.clear()
        val progressDialog = indeterminateProgressDialog("Fetching events...")
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
                        EventContent.addItem(
                                EventContent.createEventItem(
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
                    if (EventContent.ITEMS.size < 0) {
                        progressDialog.dismiss()
                        Log.e("ERROR", error!!.message)
                    }
                }
            }
        }

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            if (mColumnCount <= 1) {
                view.layoutManager = LinearLayoutManager(context)
            } else {
                view.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            view.adapter = EventRecyclerViewAdapter(EventContent.ITEMS, mUid)
        }
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

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


    companion object {
        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"
        private val ARG_UID = "uid"
        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int, uid: String): EventFragment {
            val fragment = EventFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            args.putString(ARG_UID, uid)
            fragment.arguments = args
            return fragment
        }
    }
}
