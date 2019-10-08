package io.github.dsouzadyn.evently.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import io.github.dsouzadyn.evently.R
import io.github.dsouzadyn.evently.adapters.RecieptRecyclerViewAdapter
import io.github.dsouzadyn.evently.database
import io.github.dsouzadyn.evently.models.Event
import kotlinx.android.synthetic.main.fragment_reciept_list.*
import kotlinx.android.synthetic.main.fragment_reciept_list.view.*
import net.glxn.qrgen.android.QRCode
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import org.jetbrains.anko.support.v4.defaultSharedPreferences
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.textColor

class RecieptFragment : Fragment() {

    private var mColumnCount = 1
    private var mUid = ""
    private var mUname = ""


    private var mListener: OnListFragmentInteractionListener? = null
    data class User(val email: String= "", val id: String = "", val roll_number: Int, val semester: Int, val username: String = "", val confirmed: Boolean) {
        class Deserializer: ResponseDeserializable<User> {
            override fun deserialize(content: String): User? = Gson().fromJson(content, User::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
            mUid = arguments.getString(ARG_UID)
            mUname = arguments.getString(ARG_UNAME)
        }
        val sharedPref = activity.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        mUid = sharedPref.getString(getString(R.string.uid_key), "")
        mUname = sharedPref.getString("UNAME", "")


    }

    private fun getTime(start_time: String, end_time: String): String {
        return "${start_time.subSequence(11, 16)}-${end_time.subSequence(11, 16)}"
    }

    private fun getDay(start_time: String): String {
        return "${start_time.subSequence(0,10)}"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_reciept_list, container, false)
        val rv = view.recieptList
        val totalPriceView = view.totalPrice
        val qrImageView = view.qrImage
        val confirmationView = view.confirmationText
        val helloView = view.hello
        // Set the adapter
        if (rv is RecyclerView) {
            val context = rv.getContext()
            if (mColumnCount <= 1) {
                rv.layoutManager = LinearLayoutManager(context)
            } else {
                rv.layoutManager = GridLayoutManager(context, mColumnCount)
            }

            val events = context.database.use {
                select(Event.TABLE_NAME).whereArgs(Event.COLUMN_UID + " = {mUid}", "mUid" to mUid).exec { parseList(classParser<Event>()) }
            }

            var total = 0.0f
            for (i in 0 until events.size) {
                total += events[i].price
            }
            helloView.text = "Hello, $mUname"
            totalPriceView.text = "₹ $total"
            val qrBmp = QRCode.from("${getString(R.string.temp_url)}/user/$mUid/confirm").withSize(172, 172).bitmap()
            qrImageView.setImageBitmap(qrBmp)
            val sharedPref = activity.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
            val token = "Bearer " + sharedPref.getString(getString(R.string.token_key), "")
            val progressDialog = indeterminateProgressDialog("Fetching status")
            progressDialog.show()
            FuelManager.instance.baseHeaders = mapOf("Authorization" to token)
            "${getString(R.string.temp_url)}/user/$mUid/status".httpGet().responseObject(User.Deserializer()) { _, _, result ->
                val (user, error) = result
                Log.d("CONF", user?.roll_number.toString())
                if (error == null && user?.confirmed == true) {
                    confirmationView.text = "PAID"
                    confirmationView.textColor = Color.GREEN
                    val editor = sharedPref.edit()
                    editor.putString(getString(R.string.conf_key), "CONF")
                    editor.apply()
                } else if(error == null && user?.confirmed == false) {
                    confirmationView.text = "NOT PAID"
                    confirmationView.textColor = Color.BLACK
                    val editor = sharedPref.edit()
                    editor.putString(getString(R.string.conf_key), "")
                    editor.apply()
                } else {
                    val conf = sharedPref.getString(getString(R.string.conf_key), "")
                    if (conf != "") {
                        confirmationView.text = conf
                    }
                }
                progressDialog.dismiss()
            }




            rv.adapter = RecieptRecyclerViewAdapter(events, mListener)

            var d1c = false
            var d2c = false
            var d3c = false
            if(events != null){
                for (e in events) {
                    if(getDay(e.start_time)=="2018-09-10")
                        d1c = true
                    if(getDay(e.start_time2)=="2018-09-11")
                        d2c = true
                    if(getDay(e.start_time3)=="2018-09-12")
                        d3c = true
                }
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        var d1c = false
        var d2c = false
        var d3c = false
        val events = context.database.use {
            select(Event.TABLE_NAME).whereArgs(Event.COLUMN_UID + " = {mUid}", "mUid" to mUid).exec { parseList(classParser<Event>()) }
        }
        if(events != null){
            for (e in events) {
                if(getDay(e.start_time)=="2018-09-10")
                    d1c = true
                if(getDay(e.start_time2)=="2018-09-11")
                    d2c = true
                if(getDay(e.start_time3)=="2018-09-12")
                    d3c = true
            }
        }
        if(!(d1c and d2c and d3c))
            notAllReg.visibility = View.VISIBLE

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Event)
    }
    companion object {
        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"
        private val ARG_UID = "uid"
        private val ARG_UNAME = "uname"
        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int, uid: String, uname: String): RecieptFragment {
            val fragment = RecieptFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            args.putString(ARG_UID, uid)
            args.putString(ARG_UNAME, uname)
            fragment.arguments = args
            return fragment
        }
    }
}
