package com.quiz.app.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quiz.app.database.model.VotingCategoryItemEntity

class Converter {
    @TypeConverter
    fun convertVotingListItemsToString(votingCategoryItem: List<VotingCategoryItemEntity>?): String? {
        if (votingCategoryItem == null) return null
        val gson = Gson()
        val type = object : TypeToken<List<VotingCategoryItemEntity>>() {}.type
        return gson.toJson(votingCategoryItem, type)
    }

    @TypeConverter
    fun convertStringToVotingListItems(input: String?): List<VotingCategoryItemEntity>? {
        if (input == null) return null
        val gson = Gson()
        val type = object : TypeToken<List<VotingCategoryItemEntity>?>() {}.type
        return gson.fromJson(input, type)
    }

}