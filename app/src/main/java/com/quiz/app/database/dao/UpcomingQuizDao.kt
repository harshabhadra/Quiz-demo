package com.quiz.app.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quiz.app.database.model.UpcomingQuizEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UpcomingQuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingQuiz(vararg upcomingQuizEntity: UpcomingQuizEntity)

    @Query("SELECT * FROM upcoming_quiz_table")
    fun getUpcomingQuiz(): Flow<List<UpcomingQuizEntity>>

    @Update
    suspend fun updateUpcomingQuiz(vararg upcomingQuizEntity: UpcomingQuizEntity)

    @Query("DELETE FROM upcoming_quiz_table")
    suspend fun deleteUpcomingQuiz()
}