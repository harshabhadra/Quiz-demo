package com.quiz.app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quiz.app.database.model.DbParticipant

@Dao
interface ParticipantDao {

    @Query("SELECT * FROM participant_table")
    fun getParticipantList(): LiveData<List<DbParticipant>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addParticipants(vararg dbParticipant: DbParticipant)

    @Query("DELETE from participant_table")
    fun deleteLeaderBoard()
}