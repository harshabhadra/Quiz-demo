package com.quiz.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.quiz.app.database.dao.FreeQuizDao
import com.quiz.app.database.dao.ParticipantDao
import com.quiz.app.database.dao.UpcomingQuizDao
import com.quiz.app.database.model.DbParticipant
import com.quiz.app.database.model.FreeQuizEntity
import com.quiz.app.database.model.UpcomingQuizEntity

@Database(
    entities = [DbParticipant::class, UpcomingQuizEntity::class, FreeQuizEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class MoxieDatabase : RoomDatabase() {

    //Initializing Dao
    abstract val participantDao: ParticipantDao
    abstract val upcomingQuizDao: UpcomingQuizDao
    abstract val freeQuizDao:FreeQuizDao

    companion object {
        @Volatile
        private var INSTANCE: MoxieDatabase? = null

        fun getDatabase(context: Context): MoxieDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MoxieDatabase::class.java,
                        "Moxie_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return INSTANCE!!
            }
        }
    }
}