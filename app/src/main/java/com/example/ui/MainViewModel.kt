package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DeviceHardware
import com.example.data.model.SensitivityProfile
import com.example.engine.CalibrationOffsets
import com.example.engine.HardwareScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.sensitivityDao()

    val savedProfiles: StateFlow<List<SensitivityProfile>> = dao.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _hardwareSpecs = MutableStateFlow(HardwareScanner.scan(application))
    val hardwareSpecs: StateFlow<DeviceHardware> = _hardwareSpecs

    private val _calibrationOffsets = MutableStateFlow(CalibrationOffsets())
    val calibrationOffsets: StateFlow<CalibrationOffsets> = _calibrationOffsets

    fun scanHardware() {
        _hardwareSpecs.value = HardwareScanner.scan(getApplication())
    }

    fun updateManualHardware(hardware: DeviceHardware) {
        _hardwareSpecs.value = hardware
    }

    fun applyCalibrationOffsets(offsets: CalibrationOffsets) {
        _calibrationOffsets.value = offsets
    }

    fun saveProfile(profile: SensitivityProfile) {
        viewModelScope.launch {
            dao.insertProfile(profile)
        }
    }

    fun renameProfile(profile: SensitivityProfile, newName: String) {
        viewModelScope.launch {
            dao.updateProfile(profile.copy(name = newName))
        }
    }

    fun deleteProfile(profile: SensitivityProfile) {
        viewModelScope.launch {
            dao.deleteProfile(profile)
        }
    }
}
