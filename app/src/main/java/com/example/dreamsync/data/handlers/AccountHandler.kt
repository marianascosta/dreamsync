package com.example.dreamsync.data.handlers

import android.util.Log
import com.example.dreamsync.data.models.Account
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.AccountService
import com.example.dreamsync.data.services.ProfileService
import com.google.firebase.auth.FirebaseAuth

class AccountHandler {

    private val accountService = AccountService()
    private val profileService = ProfileService()

    /**
     * Login user and return the user's profile object
     */
     fun loginUser(email: String, password: String, onLoginResult: (Profile?) -> Unit) {
//        accountService.getAccountByEmail(email, onAccountFetched = { account ->
//            if (account != null && account.password == password) {
//                profileService.getProfileById(account.profileId, onProfileFetched = { profile ->
//                    onLoginResult(profile)
//                    Log.d("AccountHandler", "User logged in: $profile")
//                })
//            } else {
//                onLoginResult(null)
//            }
//        })
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Check if profile already exists by the same UID
                        profileService.getProfileById(user.uid) { profile ->
                            if (profile == null) {
                                // Create a new profile if none exists
                                val newProfile = Profile(
                                    id = user.uid,  // Ensure it matches the auth UID
                                    userName = user.displayName ?: "Default Name",
                                    userEmail = user.email ?: "No Email"
                                )
                                profileService.saveProfile(newProfile) { profileId ->
                                    if (profileId != null) {
                                        newProfile.id = profileId
                                        onLoginResult(newProfile)
                                    } else {
                                        onLoginResult(null)
                                    }
                                }
                            } else {
                                // Return the existing profile
                                onLoginResult(profile)
                            }
                        }
                    } else {
                        onLoginResult(null)
                    }
                } else {
                    onLoginResult(null)
                }
            }
    }

    fun registerUser(email: String, password: String, userName: String, onRegisterResult: (Boolean, Profile?) -> Unit) {
//        val newProfile = Profile(userName = userName)
//        profileService.saveProfile(
//            newProfile, onProfileSaved = { profileId ->
//            if (profileId != null) {
//                val newAccount = Account(email, password, profileId)
//                accountService.saveAccount(newAccount, onAccountSaved = { success ->
//                    if (success) {
//                        onRegisterResult(true, newProfile)
//                    } else {
//                        profileService.deleteProfile(profileId)
//                        onRegisterResult(false, null)
//                    }
//                })
//            } else {
//                onRegisterResult(false, null)
//            }
//        })
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val newProfile = Profile(
                            id = user.uid,  // Match profile ID with Auth UID
                            userName = userName,
                            userEmail = email
                        )
                        profileService.saveProfile(newProfile) { profileId ->
                            if (profileId != null) {
                                onRegisterResult(true, newProfile)
                            } else {
                                onRegisterResult(false, null)
                            }
                        }
                    } else {
                        onRegisterResult(false, null)
                    }
                } else {
                    onRegisterResult(false, null)
                }
            }
    }
}