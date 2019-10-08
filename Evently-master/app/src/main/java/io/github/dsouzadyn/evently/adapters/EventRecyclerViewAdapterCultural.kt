package io.github.dsouzadyn.evently.adapters

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpPatch
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import io.github.dsouzadyn.evently.MainActivity
import io.github.dsouzadyn.evently.R
import io.github.dsouzadyn.evently.database

import io.github.dsouzadyn.evently.models.Event


import io.github.dsouzadyn.evently.models.EventContent
import io.github.dsouzadyn.evently.models.EventContentCultural
import org.jetbrains.anko.*
import org.jetbrains.anko.db.insert

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class EventRecyclerViewAdapterCultural(private val mValues: List<EventContentCultural.EventItem>, private val mUid: String) : RecyclerView.Adapter<EventRecyclerViewAdapterCultural.ViewHolder>() {



    data class Acknowledgement(val n: Int, val nModified: Int, val ok: Int) {
        class Deserializer: ResponseDeserializable<Acknowledgement> {
            override fun deserialize(content: String) = Gson().fromJson(content, Acknowledgement::class.java)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.mView.context

        holder.capacity.text = "REMAINING SLOTS:" + mValues[position].capacity.toString()
        if((mValues[position].capacity.toString().equals("0"))){
            holder.button.backgroundColor = Color.BLACK
            holder.button.isEnabled = false
            holder.button.text = "FULL"
        }
        holder.mItem = mValues[position]
        holder.time.text = getTime(mValues[position].start_time, mValues[position].end_time)
        holder.title.text = mValues[position].name
        holder.description.text = mValues[position].description
        holder.price.text = mValues[position].price.toString()
        holder.etype.text = mValues[position].type
        //holder.subtype.text = mValues[position].subtype
        if(mValues[position].cumpolsory)
            holder.subtype.visibility = View.VISIBLE
        holder.location.text = mValues[position].location

        val d = getDay(mValues[position].start_time)
        Log.wtf("time is", mValues[position].start_time)
        Log.wtf("time 2 is", d)
        if (d == "2018-09-10") {
            holder.day.text = "Day 1"
            holder.time.text = getTime(mValues[position].start_time, mValues[position].end_time)
        }
        else {
            holder.day.visibility = View.GONE
            holder.time.visibility = View.GONE
        }
        val d2 = getDay(mValues[position].start_time2)
        if (d2 == "2018-09-11") {
            holder.day2.text = "Day 2"
            holder.time2.text = getTime(mValues[position].start_time2, mValues[position].end_time2)
        }
        else {
            holder.day2.visibility = View.GONE
            holder.time2.visibility = View.GONE
        }
        val d3 = getDay(mValues[position].start_time3)
        if (d3 == "2018-09-12") {
            holder.day3.text = "Day 3"
            holder.time3.text = getTime(mValues[position].start_time3, mValues[position].end_time3)
        }
        else {
            holder.day3.visibility = View.GONE
            holder.time3.visibility = View.GONE
        }

        /*if(mValues[position].type == "TECHNICAL") {
            holder.mView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed))
        } else {
            holder.mView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
        }*/

        holder.register.setOnClickListener {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.settings_file), Context.MODE_PRIVATE)
            val conf = sharedPref.getString(context.getString(R.string.conf_key), "")
            if(conf == "") {
                context.alert("Do you wan't to confirm your registration for " + mValues[position].name + " ?", "Confirmation") {
                    yesButton {
                        val progress = context.indeterminateProgressDialog("Confirming...")
                        progress.show()
                        "${context.getString(R.string.temp_url)}/event/${mValues[position].id}/register".httpPatch()           //URL CHANGED HERE
                                .body("users=$mUid")
                                .responseObject(Acknowledgement.Deserializer()) { _, _, result ->
                                    val (acknowledgement, error) = result
                                    if (error == null) {
                                        progress.dismiss()
                                        if (acknowledgement?.nModified == 1) {
                                            context.alert("Successfully registered").show()
                                            context.database.use {
                                                insert(
                                                        Event.TABLE_NAME,
                                                        Event.COLUMN_ID to mValues[position].id,
                                                        Event.COLUMN_NAME to mValues[position].name,
                                                        Event.COLUMN_PRICE to mValues[position].price,
                                                        Event.COLUMN_UID to mUid,
                                                        Event.COLUMN_LOCATION to mValues[position].location,
                                                        Event.COLUMN_START_TIME to mValues[position].start_time,
                                                        Event.COLUMN_START_TIME2 to mValues[position].start_time2,
                                                        Event.COLUMN_START_TIME3 to mValues[position].start_time3,
                                                        Event.COLUMN_END_TIME to mValues[position].end_time,
                                                        Event.COLUMN_END_TIME2 to mValues[position].end_time2,
                                                        Event.COLUMN_END_TIME3 to mValues[position].end_time3
                                                )
                                            }
                                        } else {
                                            progress.dismiss()
                                            context.alert("Registrations full!").show()
                                        }
                                    } else {
                                        context.alert(error.localizedMessage).show()
                                    }
                                }
                    }
                    noButton { }
                }.show()
            } else {
                context.alert("Unfortunately you're booking is confirmed and no more registrations are possible!").show()
            }
        }
        holder.mView.setOnClickListener {
            //mListener?.onListFragmentInteraction(holder.mItem!!)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    private fun getTime(start_time: String, end_time: String): String {
        return "${start_time.subSequence(11, 16)}-${end_time.subSequence(11, 16)}"
    }

    private fun getDay(start_time: String): String {
        return "${start_time.subSequence(0,10)}"
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val time: TextView
        val title: TextView
        val location: TextView
        val description: TextView
        val etype: TextView
        val subtype: TextView
        val price: TextView
        val register: Button
        val day: TextView
        val capacity:TextView
        val day2: TextView
        val time2: TextView
        val day3: TextView
        val time3: TextView
        val button: Button

        var mItem: EventContentCultural.EventItem? = null

        init {
            time = mView.findViewById<View>(R.id.itemTime) as TextView
            title = mView.findViewById<View>(R.id.itemTitle) as TextView
            description = mView.findViewById<View>(R.id.itemDescription) as TextView
            price = mView.findViewById<View>(R.id.itemPrice) as TextView
            etype = mView.findViewById<View>(R.id.itemType) as TextView
            subtype = mView.findViewById<View>(R.id.itemSubType) as TextView
            register = mView.findViewById<View>(R.id.itemRegisterBtn) as Button
            location = mView.findViewById<View>(R.id.itemLocation) as TextView
            day = mView.findViewById<View>(R.id.itemDay) as TextView
            capacity = mView.findViewById<View>(R.id.itemCapacity) as TextView
            day2 = mView.findViewById<View>(R.id.itemDay2) as TextView
            day3 = mView.findViewById<View>(R.id.itemDay3) as TextView
            time2 = mView.findViewById<View>(R.id.itemTime2) as TextView
            time3 = mView.findViewById<View>(R.id.itemTime3) as TextView
            button = mView.findViewById<Button>(R.id.itemRegisterBtn) as Button
        }

        override fun toString(): String {
            return super.toString() + " '" + title.text + "'"
        }
    }
}
