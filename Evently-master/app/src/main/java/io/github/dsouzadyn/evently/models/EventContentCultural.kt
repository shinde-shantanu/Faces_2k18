package io.github.dsouzadyn.evently.models

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 *
 */
object EventContentCultural {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<EventItem> = ArrayList()


    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, EventItem> = HashMap()



    fun addItem(item: EventItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    fun clearitemslist() {
        ITEMS.clear()
    }

    fun createEventItem(position: Int, id: String, name: String, description: String, capacity: Int
                        , start_time: String, end_time: String,
                        start_time2: String, end_time2:String, start_time3: String, end_time3: String,
                        price: Float, type: String, subtype: String, location: String, cumpolsory: Boolean): EventItem {
        return EventItem(id,
                name,
                description,
                capacity,
                start_time,
                end_time,
                start_time2,
                end_time2,
                start_time3,
                end_time3,
                price,
                type,
                subtype,
                location,
                cumpolsory,
                makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0 until position) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A dummy item representing a piece of content.
     */
    class EventItem(val id: String, val name: String, val description: String, val capacity: Int,
                    val start_time: String, val end_time: String,
                    val start_time2: String, val end_time2: String,
                    val start_time3: String, val end_time3: String,
                    val price: Float,
                    val type: String, val subtype: String, val location: String, val cumpolsory: Boolean,
                    val details: String) {
        override fun toString(): String {
            return name
        }
    }
}
