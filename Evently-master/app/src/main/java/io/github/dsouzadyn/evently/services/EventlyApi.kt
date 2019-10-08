package io.github.dsouzadyn.evently.services

import com.github.kittinunf.fuel.util.FuelRouting

/**
 * Created by dylan on 2/2/18.
 */
sealed class EventlyApi: FuelRouting {
    override val basePath: String = "http://192.168.43.41/"


}