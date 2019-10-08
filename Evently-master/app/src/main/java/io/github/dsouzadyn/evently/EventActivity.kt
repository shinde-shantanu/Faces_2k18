package io.github.dsouzadyn.evently

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import io.github.dsouzadyn.evently.fragments.EventFragment
import io.github.dsouzadyn.evently.fragments.RecieptFragment
import io.github.dsouzadyn.evently.models.DayContent
import io.github.dsouzadyn.evently.models.EventContent
import kotlinx.android.synthetic.main.activity_event.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast

class EventActivity : AppCompatActivity() {


    var uid1 = ""

    private val CUMPOLSORY = "1"
    private val NON_CUMPOLSORY = "2"

    //NEW ADDITION , 3 events-
    private val FUN_EVENT = "1"
    private val CULTURAL_EVENT = "2"
    private val SPORTS_EVENT = "3"
    // private val DAY_ONE = "1"
    // private val DAY_TWO = "2"
    // private val DAY_THREE = "3"
    private val MY_EVENTS = "4"
    private val SIGNIN_OK = 420

    var events : List<MainActivity.Event>? = null
    var error: FuelError? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        if (viewPager != null) {
            val adapter = PagerAdapter(supportFragmentManager)
            viewPager.adapter = adapter
            eventTabLayout.setupWithViewPager(viewPager)
        }
        val sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        val uid = sharedPref.getString(getString(R.string.uid_key), "")
        uid1 = uid
        setSupportActionBar(eventToolbar)
        eventToolbar.setTitle("FACES 2K18")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    data class Event(val id: String, val name: String, val description: String = "", val capacity: Int,
                     val start_time: String, val end_time: String,
                     val start_time2: String, val end_time2: String,
                     val start_time3: String, val end_time3: String,
                     val price: Float, val type: String, val subtype: String, val location: String, val cumpolsory: Boolean = false) {
        class Deserializer: ResponseDeserializable<List<Event>> {
            override fun deserialize(content: String): List<Event>? = Gson().fromJson(content, Array<Event>::class.java).toList()
        }
    }

    /*override fun onFragmentInteraction(item: DayContent.DayItem){
        val sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        val uid = sharedPref.getString(getString(R.string.uid_key), "")
        val uname = sharedPref.getString(getString(R.string.uname_key), "")
        when {
        /*item.id == CUMPOLSORY -> {
            // Launch the cumpolsory events
            navigateEvents(true, uid)
        }
        item.id == NON_CUMPOLSORY -> {
            // Launch the non cumpolsory events
            navigateEvents(false, uid)
        }*/

        //NEW ADDITION, removed compulsory and added 3 catg
            item.id == FUN_EVENT -> {
                navigateEvents("fun", uid)
            }
            item.id == CULTURAL_EVENT -> {
                navigateEvents("cultural", uid)
            }
            item.id == SPORTS_EVENT -> {
                navigateEvents("sports", uid)
            }
            else -> Log.e("ERROR", "Something went wrong")
        }
    }*/

    public fun navigateEvents(eventType: String, uid: String = ""):EventFragment {    //removed isCumpolsory: Boolean
        EventContent.ITEMS.clear()
        val progressDialog = indeterminateProgressDialog("Fetching events...")
        val sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPref.getString(getString(R.string.token_key), "")
        FuelManager.instance.baseHeaders = mapOf("Authorization" to token)
        progressDialog.show()
        "${getString(R.string.temp_url)}/event?type=$eventType".httpGet().responseObject(MainActivity.Event.Deserializer()) { _, _, result ->    //og=event?cumpolsory=$isCumpolsory
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
                    if (EventContent.ITEMS.size > 0) {

                    }
                }
            } else {
                progressDialog.dismiss()
                Log.e("ERROR", error!!.message)
            }
        }
        return (EventFragment.newInstance(1, uid))
    }

}
