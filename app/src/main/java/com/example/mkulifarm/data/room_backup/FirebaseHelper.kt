package com.example.mkulifarm.data.room_backup

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseHelper {

    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    fun registerFarmer(farmer: Farmer) {
        val farmerId = auth.currentUser?.uid
        val farmerRef = database.child("farmers").child(farmerId ?: "")
        farmerRef.setValue(farmer)
    }

    fun getFarmerData(): DatabaseReference {
        val farmerId = auth.currentUser?.uid
        return database.child("farmers").child(farmerId ?: "")
    }
}