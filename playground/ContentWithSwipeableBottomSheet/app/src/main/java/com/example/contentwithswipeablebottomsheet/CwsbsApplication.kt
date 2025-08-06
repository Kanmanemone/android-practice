package com.example.contentwithswipeablebottomsheet

import android.app.Application
import com.example.contentwithswipeablebottomsheet.data.DimensDataStore
import kotlinx.coroutines.runBlocking

class CwsbsApplication : Application() {
    lateinit var dimensDataStore: DimensDataStore
        private set

    override fun onCreate() {
        super.onCreate()

        runBlocking {
            dimensDataStore = DimensDataStore.create(this@CwsbsApplication)
        }
    }
}