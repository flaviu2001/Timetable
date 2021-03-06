package com.flaviu.timetable.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Card::class, Subtask::class, Label::class, CardLabel::class], version = 10)
@TypeConverters(Converters::class)
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

        private val migration_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //Add the old notes into the new subtask table
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_subtask_table (subtaskId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, cardId INTEGER NOT NULL, description TEXT NOT NULL, dueDate INTEGER, reminderDate INTEGER, reminderId INTEGER, FOREIGN KEY(cardId) REFERENCES timetable_card_table(cardId) ON UPDATE NO ACTION ON DELETE CASCADE )")
                database.execSQL("INSERT INTO timetable_subtask_table (cardId, description) SELECT cardId, notes FROM timetable_card_table WHERE notes != \"\"")
                //Remove the notes column from card table
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_card_table_backup (cardId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timeBegin TEXT NOT NULL, timeEnd TEXT NOT NULL, weekday INTEGER NOT NULL, place TEXT NOT NULL, name TEXT NOT NULL, info TEXT NOT NULL, label TEXT NOT NULL, color INTEGER NOT NULL, textColor INTEGER NOT NULL)")
                database.execSQL("INSERT INTO timetable_card_table_backup(cardId, timeBegin, timeEnd, weekday, place, name, info, label, color, textColor) SELECT cardId, timeBegin, timeEnd, weekday, place, name, info, label, color, textColor FROM timetable_card_table")
                database.execSQL("DROP TABLE timetable_card_table")
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_card_table (cardId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timeBegin TEXT NOT NULL, timeEnd TEXT NOT NULL, weekday INTEGER NOT NULL, place TEXT NOT NULL, name TEXT NOT NULL, info TEXT NOT NULL, label TEXT NOT NULL, color INTEGER NOT NULL, textColor INTEGER NOT NULL)")
                database.execSQL("INSERT INTO timetable_card_table(cardId, timeBegin, timeEnd, weekday, place, name, info, label, color, textColor) SELECT cardId, timeBegin, timeEnd, weekday, place, name, info, label, color, textColor FROM timetable_card_table_backup")
                database.execSQL("DROP TABLE timetable_card_table_backup")
                //Adding and populating label and card_label
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_label_table (labelId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_card_label_table (cardId INTEGER NOT NULL, labelId INTEGER NOT NULL, PRIMARY KEY(cardId, labelId), FOREIGN KEY(cardId) REFERENCES timetable_card_table(cardId) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(labelId) REFERENCES timetable_label_table(labelId) ON UPDATE NO ACTION ON DELETE CASCADE )")
                database.execSQL("INSERT INTO timetable_label_table(name) SELECT DISTINCT label FROM timetable_card_table")
                database.execSQL("INSERT INTO timetable_card_label_table(cardId, labelId) SELECT C.cardId, L.labelId FROM timetable_card_table C INNER JOIN timetable_label_table L ON C.label = L.name")
                //Removing label from card table
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_card_table_backup(cardId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timeBegin TEXT NOT NULL, timeEnd TEXT NOT NULL, weekday INTEGER NOT NULL, place TEXT NOT NULL, name TEXT NOT NULL, info TEXT NOT NULL, color INTEGER NOT NULL, textColor INTEGER NOT NULL)")
                database.execSQL("INSERT INTO timetable_card_table_backup(cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor) SELECT cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor FROM timetable_card_table")
                database.execSQL("DROP TABLE timetable_card_table")
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_card_table (cardId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timeBegin TEXT NOT NULL, timeEnd TEXT NOT NULL, weekday INTEGER NOT NULL, place TEXT NOT NULL, name TEXT NOT NULL, info TEXT NOT NULL, color INTEGER NOT NULL, textColor INTEGER NOT NULL)")
                database.execSQL("INSERT INTO timetable_card_table(cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor) SELECT cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor FROM timetable_card_table_backup")
                database.execSQL("DROP TABLE timetable_card_table_backup")
            }
        }

        private val migration_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //Adding reminderDate and reminderId
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_card_table_backup (cardId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timeBegin TEXT NOT NULL, timeEnd TEXT NOT NULL, weekday INTEGER NOT NULL, place TEXT NOT NULL, name TEXT NOT NULL, info TEXT NOT NULL, color INTEGER NOT NULL, textColor INTEGER NOT NULL, reminderDate INTEGER, reminderId INTEGER)")
                database.execSQL("INSERT INTO timetable_card_table_backup (cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor) SELECT cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor FROM timetable_card_table")
                database.execSQL("DROP TABLE timetable_card_table")
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_card_table (cardId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timeBegin TEXT NOT NULL, timeEnd TEXT NOT NULL, weekday INTEGER NOT NULL, place TEXT NOT NULL, name TEXT NOT NULL, info TEXT NOT NULL, color INTEGER NOT NULL, textColor INTEGER NOT NULL, reminderDate INTEGER, reminderId INTEGER)")
                database.execSQL("INSERT INTO timetable_card_table (cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor) SELECT cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor FROM timetable_card_table_backup")
                database.execSQL("DROP TABLE timetable_card_table_backup")
            }
        }

        private val migration_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //Adding visibility to label
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_label_table_backup (labelId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, visible INTEGER NOT NULL, name TEXT NOT NULL)")
                database.execSQL("INSERT INTO timetable_label_table_backup (labelId, visible, name) SELECT labelId, 1,  name FROM timetable_label_table")
                database.execSQL("DROP TABLE timetable_label_table")
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_label_table (labelId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, visible INTEGER NOT NULL, name TEXT NOT NULL)")
                database.execSQL("INSERT INTO timetable_label_table (labelId, visible, name) SELECT labelId, visible, name FROM timetable_label_table_backup")
                database.execSQL("DROP TABLE timetable_label_table_backup")
            }
        }

        private val migration_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //Adding expirationDate and expirationId to card
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_card_table_backup (cardId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timeBegin TEXT NOT NULL, timeEnd TEXT NOT NULL, weekday INTEGER NOT NULL, place TEXT NOT NULL, name TEXT NOT NULL, info TEXT NOT NULL, color INTEGER NOT NULL, textColor INTEGER NOT NULL, reminderDate INTEGER, reminderId INTEGER, expirationDate INTEGER, expirationId INTEGER)")
                database.execSQL("INSERT INTO timetable_card_table_backup (cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor, reminderDate, reminderId) SELECT cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor, reminderDate, reminderId FROM timetable_card_table")
                database.execSQL("DROP TABLE timetable_card_table")
                database.execSQL("CREATE TABLE IF NOT EXISTS timetable_card_table (cardId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timeBegin TEXT NOT NULL, timeEnd TEXT NOT NULL, weekday INTEGER NOT NULL, place TEXT NOT NULL, name TEXT NOT NULL, info TEXT NOT NULL, color INTEGER NOT NULL, textColor INTEGER NOT NULL, reminderDate INTEGER, reminderId INTEGER, expirationDate INTEGER, expirationId INTEGER)")
                database.execSQL("INSERT INTO timetable_card_table (cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor, reminderDate, reminderId) SELECT cardId, timeBegin, timeEnd, weekday, place, name, info, color, textColor, reminderDate, reminderId FROM timetable_card_table_backup")
                database.execSQL("DROP TABLE timetable_card_table_backup")
            }
        }

        fun getInstance(context: Context): CardDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CardDatabase::class.java,
                        "card_database")
                        .addMigrations(migration_1_6)
                        .addMigrations(migration_6_7)
                        .addMigrations(migration_7_8)
                        .addMigrations(migration_8_9)
                        .addMigrations(migration_9_10)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}