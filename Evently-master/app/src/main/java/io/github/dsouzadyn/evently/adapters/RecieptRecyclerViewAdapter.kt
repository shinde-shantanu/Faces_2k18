package io.github.dsouzadyn.evently.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.dsouzadyn.evently.R
import io.github.dsouzadyn.evently.R.id.notAllReg

import io.github.dsouzadyn.evently.fragments.RecieptFragment.OnListFragmentInteractionListener
import io.github.dsouzadyn.evently.models.Event
import io.github.dsouzadyn.evently.models.EventContent
import kotlinx.android.synthetic.main.fragment_reciept_list.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class RecieptRecyclerViewAdapter(var mEvents: List<Event>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<RecieptRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_reciept, parent, false)
        return ViewHolder(view)
    }

    private fun getTime(start_time: String, end_time: String): String {
        return "${start_time.subSequence(11, 16)}-${end_time.subSequence(11, 16)}"
    }

    private fun getDay(start_time: String): String {
        return "${start_time.subSequence(0,10)}"
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(mEvents != null) {
            holder.mItem = mEvents[position]
            holder.mIdView.text = mEvents[position].name
            holder.mContentView.text = mEvents[position].price.toString()
            holder.mLocationView.text = "Location: ${mEvents[position].location}"

            val d = getDay(mEvents[position].start_time)

            if (d == "2018-09-10") {
                holder.day.text = "Day 1"
                holder.time.text = getTime(mEvents[position].start_time, mEvents[position].end_time)
            }
            else {
                holder.day.visibility = View.GONE
                holder.time.visibility = View.GONE
            }
            val d2 = getDay(mEvents[position].start_time2)
            if (d2 == "2018-09-11") {
                holder.day2.text = "Day 2"
                holder.time2.text = getTime(mEvents[position].start_time2, mEvents[position].end_time2)
            }
            else {
                holder.day2.visibility = View.GONE
                holder.time2.visibility = View.GONE
            }
            val d3 = getDay(mEvents[position].start_time3)
            if (d3 == "2018-09-12") {
                holder.day3.text = "Day 3"
                holder.time3.text = getTime(mEvents[position].start_time3, mEvents[position].end_time3)
            }
            else {
                holder.day3.visibility = View.GONE
                holder.time3.visibility = View.GONE
            }
        }
        holder.mView.setOnClickListener {
            //mListener?.onListFragmentInteraction(holder.mItem)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)


    }

    override fun getItemCount(): Int {
        if (mEvents != null) {
            return mEvents!!.size
        }
        return 0
    }

    fun setEvents(events: List<Event>) {
        this.mEvents = events
        notifyDataSetChanged()
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView
        val mContentView: TextView
        val mLocationView: TextView
        val day: TextView
        val day2: TextView
        val day3: TextView
        val time: TextView
        val time2: TextView
        val time3: TextView
        var mItem: Event? = null

        init {
            mIdView = mView.findViewById<View>(R.id.id) as TextView
            mContentView = mView.findViewById<View>(R.id.content) as TextView
            mLocationView = mView.findViewById<View>(R.id.location) as TextView
            day = mView.findViewById<View>(R.id.itemDay) as TextView
            day2 = mView.findViewById<View>(R.id.itemDay2) as TextView
            day3 = mView.findViewById<View>(R.id.itemDay3) as TextView
            time = mView.findViewById<View>(R.id.itemTime) as TextView
            time2 = mView.findViewById<View>(R.id.itemTime2) as TextView
            time3 = mView.findViewById<View>(R.id.itemTime3) as TextView



        }

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
