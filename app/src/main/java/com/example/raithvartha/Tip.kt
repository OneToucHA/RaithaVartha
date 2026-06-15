package com.example.raithvartha

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tips")
data class Tip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titleKannada: String,
    val titleEnglish: String,
    val instructionKannada: String,
    val instructionEnglish: String,
    val cropCategory: String,
    val imageUrl: String,
    val successStory: String,
    val isDailyTip: Boolean = false
)