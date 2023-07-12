package com.quiz.app.data.leaderBoard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.quiz.app.data.model.Result
import com.quiz.app.database.model.DbParticipant
import com.quiz.app.database.MoxieDatabase
import com.quiz.app.network.ApiService
import com.quiz.app.network.model.asEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class LeaderBoardRepository(
    private val database: MoxieDatabase,
    private val leaderBoardRemoteDataSource: LeaderBoardRemoteDataSource
) {

    private val lock = Mutex()

    companion object {
        private const val TAG = "LeaderBoardRepository"
    }

    val participantsList: LiveData<List<DbParticipant>> =
        database.participantDao.getParticipantList()

    private var _pageInfo = MutableLiveData<Pair<Int, Int>>()
    val pageInfo: LiveData<Pair<Int, Int>> = _pageInfo


    suspend fun getLeaderBoard(quizId: String, queryDetails: Map<String, Any>) {
        val leaderBoardRemoteResponse =
            leaderBoardRemoteDataSource.getLeaderBoardData(quizId, queryDetails)

        if (leaderBoardRemoteResponse is Result.Success) {
            val data = leaderBoardRemoteResponse.data.first.asEntity()
            val pageData = leaderBoardRemoteResponse.data.second
            addParticipantsToDb(data)
            lock.withLock {
                _pageInfo.value = Pair(pageData.first, pageData.second)
            }
        }
    }

    private suspend fun addParticipantsToDb(participants: Array<DbParticipant>) {
        withContext(Dispatchers.IO) {
            database.participantDao.addParticipants(*participants)
        }
    }

    suspend fun deleteAllParticipants() {
        withContext(Dispatchers.IO) {
            database.participantDao.deleteLeaderBoard()
        }
    }
}