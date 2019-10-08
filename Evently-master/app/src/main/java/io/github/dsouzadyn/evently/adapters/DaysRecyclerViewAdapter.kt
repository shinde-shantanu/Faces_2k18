package io.github.dsouzadyn.evently.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import io.github.dsouzadyn.evently.fragments.DayFragment.OnListFragmentInteractionListener
import io.github.dsouzadyn.evently.R
import io.github.dsouzadyn.evently.models.DayContent


class DaysRecyclerViewAdapter(private val mValues: List<DayContent.DayItem>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<DaysRecyclerViewAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return mValues.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        Log.d("MITEM", holder.mItem.toString())
        holder.mContentView.text = mValues[position].content

        holder.mView.setOnClickListener {
            mListener?.onListFragmentInteraction(holder.mItem!!)
        }
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView
        var mItem: DayContent.DayItem? = null

        init {

            mContentView = mView.findViewById<View>(R.id.content) as TextView
        }

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
