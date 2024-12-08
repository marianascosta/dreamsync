package com.example.dreamsync.data.services

class StorageHandler {

    val dreamService = DreamService()
    val profileService = ProfileService()

    /**
     * Initialize the Realtime Database with the Dreams and Profiles
     */
    fun initRealTimeDatabase() {
        dreamService.initDreams()
        profileService.initProfiles()
    }
}