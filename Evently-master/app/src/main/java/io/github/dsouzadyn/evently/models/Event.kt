package io.github.dsouzadyn.evently.models

/**
 * Created by laptop64 on 1/16/18.
 */
data class Event(val id: String, val uid: String, val name: String, val price: Long, val location: String, val start_time: String, val start_time2: String, val start_time3: String,
                 val end_time: String, val end_time2: String, val end_time3: String) {
    companion object {
        val COLUMN_ID = "id"
        val TABLE_NAME = "events"
        val COLUMN_UID = "uid"
        val COLUMN_NAME = "name"
        val COLUMN_PRICE = "price"
        val COLUMN_LOCATION = "location"
        val COLUMN_START_TIME = "start_time"
        val COLUMN_START_TIME2 = "start_time2"
        val COLUMN_START_TIME3 = "start_time3"
        val COLUMN_END_TIME = "end_time"
        val COLUMN_END_TIME2 = "end_time2"
        val COLUMN_END_TIME3 = "end_time3"
    }
}