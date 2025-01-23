package com.example.dreamsync.data.handlers

import android.util.Log
import com.example.dreamsync.data.models.Account
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.AccountService
import com.example.dreamsync.data.services.ProfileService

class AccountHandler {

    private val accountService = AccountService()
    private val profileService = ProfileService()

     fun loginUser(email: String, password: String, onLoginResult: (Profile?) -> Unit) {
        accountService.getAccountByEmail(email, onAccountFetched = { account ->
            if (account != null && account.password == password) {
                profileService.getProfileById(account.profileId, onProfileFetched = { profile ->
                    onLoginResult(profile)
                    Log.d("AccountHandler", "User logged in: $profile")
                })
            } else {
                onLoginResult(null)
            }
        })
    }

    fun registerUser(email: String, password: String, userName: String, onRegisterResult: (Boolean, Profile?) -> Unit) {
        val newProfile = Profile(userName = userName)
        profileService.saveProfile(
            newProfile, onProfileSaved = { profileId ->
            if (profileId != null) {
                val newAccount = Account(email, password, profileId)
                accountService.saveAccount(newAccount, onAccountSaved = { success ->
                    if (success) {
                        onRegisterResult(true, newProfile)
                    } else {
                        profileService.deleteProfile(profileId)
                        onRegisterResult(false, null)
                    }
                })
            } else {
                onRegisterResult(false, null)
            }
        })
    }
}