package com.flaviu.timetable.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Card::class], version = 6, exportSchema = false)
abstract class CardDatabase : RoomDatabase() {

    abstract val cardDatabaseDao: CardDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: CardDatabase? = null

        private val migration_1_6: Migration = object : Migration(1, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE timetable_card_table ADD COLUMN textColor INTEGER DEFAULT 4294967295 NOT NULL")
            }
        }

        fun getInstance(context: Context): CardDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CardDatabase::class.java,
                        "card_database"
                    )
                        .addMigrations(migration_1_6)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}