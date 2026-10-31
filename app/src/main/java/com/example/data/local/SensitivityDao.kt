package com.example.data.local

import androidx.room.*
import com.example.data.model.SensitivityProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface SensitivityDao {
    @Query("SELECT * FROM sensitivity_profiles ORDER BY timestamp DESC")
    fun getAllProfiles(): Flow<List<SensitivityProfile>>

    @Query("SELECT * FROM sensitivity_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): SensitivityProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: SensitivityProfile): Long

    @Update
    suspend fun updateProfile(profile: SensitivityProfile)

    @Delete
    suspend fun deleteProfile(profile: SensitivityProfile)
}
