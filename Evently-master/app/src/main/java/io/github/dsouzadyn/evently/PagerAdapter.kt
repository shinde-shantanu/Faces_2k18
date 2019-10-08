package io.github.dsouzadyn.evently

/**
 * Created by Shantanu Shinde on 28-08-2018.
 */
import android.content.Context
import android.provider.Settings.Global.getString
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import io.github.dsouzadyn.evently.fragments.EventFragment
import io.github.dsouzadyn.evently.models.DayContent
import io.github.dsouzadyn.evently.models.EventContent
import org.jetbrains.anko.indeterminateProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import io.github.dsouzadyn.evently.fragments.DayFragment
import io.github.dsouzadyn.evently.fragments.HomeFragment
import io.github.dsouzadyn.evently.fragments.RecieptFragment
import io.github.dsouzadyn.evently.models.Event
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton


class PagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val COUNT = 3
    public val a = EventActivity()

    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                fragment = FunFragment()
            }
            1 -> {
                fragment = CulturalFragment()
            }
            2 -> {
                fragment = SportsFragment()
            }
        }

        return fragment
    }

    override fun getCount(): Int {
        return COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        when (position) {
            0 -> {
                title = "Fun"
            }
            1 -> {
                title = "Cultural"
            }
            2 -> {
                title = "Sports"
            }
        }

        return title
    }

}
