package io.github.dsouzadyn.evently

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.github.dsouzadyn.evently.models.Event
import org.jetbrains.anko.db.*

/**
 * Created by laptop64 on 1/16/18.
 */
class DatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "EventsDatabase", null, 1) {

    companion object {
        private var instance: DatabaseHelper? = null

        @Synchronized
        fun Instance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(database: SQLiteDatabase) {
        database.createTable(Event.TABLE_NAME, true, Event.COLUMN_ID to TEXT + PRIMARY_KEY,
                Event.COLUMN_UID to TEXT, Event.COLUMN_NAME to TEXT, Event.COLUMN_PRICE to INTEGER, Event.COLUMN_LOCATION to TEXT,
                Event.COLUMN_START_TIME to TEXT, Event.COLUMN_START_TIME2 to TEXT, Event.COLUMN_START_TIME3 to TEXT,
                Event.COLUMN_END_TIME to TEXT, Event.COLUMN_END_TIME2 to TEXT, Event.COLUMN_END_TIME3 to TEXT)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.dropTable(Event.TABLE_NAME, true)
    }

}

val Context.database: DatabaseHelper
    get() = DatabaseHelper.Instance(applicationContext)