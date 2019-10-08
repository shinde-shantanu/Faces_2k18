package io.github.dsouzadyn.evently

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import io.github.dsouzadyn.evently.fragments.DayFragment
import io.github.dsouzadyn.evently.fragments.EventFragment
import io.github.dsouzadyn.evently.fragments.HomeFragment
import io.github.dsouzadyn.evently.fragments.RecieptFragment
import io.github.dsouzadyn.evently.models.DayContent
import io.github.dsouzadyn.evently.models.Event
import io.github.dsouzadyn.evently.models.EventContent
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton


class MainActivity : AppCompatActivity(), DayFragment.OnListFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {

    }

    var uid1 = ""
    var uname1 = ""
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

    var events : List<Event>? = null
    var error: FuelError? = null


    data class Event(val id: String, val name: String, val description: String = "", val capacity: Int,
                     val start_time: String, val end_time: String,
                     val start_time2: String, val end_time2:String,
                     val start_time3: String, val end_time3: String,
                     val price: Float, val type: String, val subtype: String, val location: String, val cumpolsory: Boolean = false) {
        class Deserializer: ResponseDeserializable<List<Event>> {
            override fun deserialize(content: String): List<Event>? = Gson().fromJson(content, Array<Event>::class.java).toList()
        }
    }

    override fun onListFragmentInteraction(item: DayContent.DayItem) {
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
            item.id == MY_EVENTS -> {
                // Launch my events fragment
                navigateToFragment(RecieptFragment.newInstance(1, uid, uname))
            }
            else -> Log.e("ERROR", "Something went wrong")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPref.getString(getString(R.string.token_key), "")
        val uid = sharedPref.getString(getString(R.string.uid_key), "")
        uid1 = uid
        uname1 = sharedPref.getString(getString(R.string.uname_key), "")

        if(intent.getBooleanExtra("dontCheck", false)){
            ;
        }
        else if (token == "Bearer ") {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivityForResult(loginIntent, SIGNIN_OK)
        } else {
//            val progressDialog = indeterminateProgressDialog("Fetching events...")
//            FuelManager.instance.baseHeaders = mapOf("Authorization" to token)
//            progressDialog.show()
//            "${getString(R.string.base_api_url)}/event".httpGet().responseObject(Event.Deserializer()) { _, _, result ->
//                events = result.component1()
//                error = result.component2()
//                if(error == null) {
//                    progressDialog.dismiss()
//                    if(events != null) {
//                        for (event in events!!) {
//                            Log.d("EVENT", event.name)
//                        }
//                    }
//                } else {
//                    progressDialog.dismiss()
//                    Log.e("ERROR", error!!.message)
//                }
//            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
            transaction.replace(R.id.fragmentContainer, HomeFragment.newInstance(uid,""))
            transaction.commit()
        }

        //new additions-
        setSupportActionBar(toolbar)
        supportActionBar?.title="FACES 2K18"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.btnLogout){
            onLogoutBtnClick()
        }
        if(item?.itemId == R.id.btnMyEvents) {
            navigateToFragment(RecieptFragment.newInstance(1, uid1, uname1))
        }
        return true
    }

    private fun onLogoutBtnClick() {
        this.alert("Do you really want to logout?") {
            yesButton {
                val sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
                database.use {
                    val d = delete(io.github.dsouzadyn.evently.models.Event.TABLE_NAME, "uid = '${getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE).getString(getString(R.string.uid_key), "")}'")
                }
                sharedPref.edit().clear().apply()
                finish()
            }
            noButton {  }
        }.show()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == SIGNIN_OK) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
            transaction.replace(R.id.fragmentContainer, HomeFragment.newInstance("",""))
            transaction.commit()
        }
    }

    private fun navigateEvents(eventType: String, uid: String = "") {    //removed isCumpolsory: Boolean
        EventContent.ITEMS.clear()
        val progressDialog = indeterminateProgressDialog("Fetching events...")
        val sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPref.getString(getString(R.string.token_key), "")
        FuelManager.instance.baseHeaders = mapOf("Authorization" to token)
        progressDialog.show()
        "${getString(R.string.temp_url)}/event?type=$eventType".httpGet().responseObject(Event.Deserializer()) { _, _, result ->    //og=event?cumpolsory=$isCumpolsory
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
                    if (EventContent.ITEMS.size > 0)
                        navigateToFragment(EventFragment.newInstance(1, uid))
                }
            } else {
                progressDialog.dismiss()
                Log.e("ERROR", error!!.message)
            }
        }
//        var i = 0
//        if (events != null) {
//            events?.filterIndexed { index, value ->
//
//                value.cumpolsory == isCumpolsory
//            }?.forEach { e ->
//                EventContent.addItem(
//                        EventContent.createEventItem(
//                                i++,
//                                e.id,
//                                e.name,
//                                e.description,
//                                e.capacity,
//                                e.start_time,
//                                e.end_time,
//                                e.price,
//                                e.type,
//                                e.subtype,
//                                e.location,
//                                e.cumpolsory
//                        ))
//            }
//            if (EventContent.ITEMS.size > 0)
//                navigateToFragment(EventFragment.newInstance(1, uid))
//        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
        transaction.replace(R.id.fragmentContainer, fragment).addToBackStack("events")
        transaction.commit()
    }



    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }

    }
}
