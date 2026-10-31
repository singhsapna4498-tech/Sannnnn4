package com.example.data.model

enum class DeviceTier(val label: String, val description: String) {
    LOW_END("Low-End (< 3GB RAM)", "Optimized for minimal touch latency and maximum drag response"),
    BUDGET("Budget Tier (3GB - 4GB RAM)", "Balanced for consistent 60 FPS on smooth graphics"),
    MID_RANGE("Mid-Range (6GB - 8GB RAM)", "Standard competitive profile with stabilized recoil"),
    HIGH_END("High-End (8GB - 12GB RAM)", "Sharp response tuned for 90Hz/120Hz smooth tracking"),
    FLAGSHIP("Flagship (12GB+ RAM / 120Hz+)", "Micro-aim precision for high touch sampling rates")
}

enum class PlayStyle(val label: String, val description: String) {
    RUSH("Rush (Aggressive Close Combat)", "Rapid close-range drag headshots and flick agility"),
    BALANCED("Balanced (Hybrid Combat)", "Equalized close, mid and long range precision"),
    LONG_RANGE("Long Range (AR / DMR)", "Controlled crosshair reset for tapping and bursting"),
    SNIPER("Sniper (Long Range Precision)", "Ultra-stable micro-tracking with sniper scopes")
}

enum class WeaponPreference(val label: String, val weaponName: String) {
    ALL_WEAPONS("All Weapons (Balanced)", "All-Around"),
    MP40("MP40 (High RPM SMG Spray)", "MP40"),
    M1887("M1887 (Flick Shotgun One-Tap)", "M1887"),
    DESERT_EAGLE("Desert Eagle (Single Drag Tap)", "Desert Eagle"),
    SNIPER("Sniper (AWM / M82B Precision)", "Sniper Rifle")
}

enum class FingerControl(val label: String, val fingerCount: Int) {
    TWO_FINGER("2-Finger (Default Thumbs)", 2),
    THREE_FINGER("3-Finger (Index + Thumbs)", 3),
    FOUR_FINGER("4-Finger (Claw)", 4),
    FIVE_FINGER("5-Finger (Full Claw / Gyro)", 5)
}
