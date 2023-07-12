package com.quiz.app.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quiz.app.database.model.FreeQuizEntity
import com.quiz.app.database.model.UpcomingQuizEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FreeQuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFreeQuizzes(vararg freeQuizEntity: FreeQuizEntity)

    @Query("SELECT * FROM free_quiz_table")
    fun geFreeQuizzes(): Flow<List<FreeQuizEntity>>

    @Update
    suspend fun updateFreeQuizzes(vararg freeQuizEntity: FreeQuizEntity)

    @Query("DELETE FROM free_quiz_table")
    suspend fun deleteFreeQuizzes()
}