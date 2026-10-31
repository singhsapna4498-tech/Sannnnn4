package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensitivity_profiles")
data class SensitivityProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val timestamp: Long = System.currentTimeMillis(),
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniperScope: Int,
    val freeLook: Int,
    val fireButtonSize: Int,
    val recommendedDpi: Int,
    val deviceModel: String,
    val playStyle: String,
    val weaponPreference: String,
    val fingerControl: String,
    val ratingScore: Int = 0 // 1-5 rating user feedback
)
