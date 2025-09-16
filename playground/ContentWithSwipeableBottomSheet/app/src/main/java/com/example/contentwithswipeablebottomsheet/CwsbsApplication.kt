package com.example.contentwithswipeablebottomsheet

import android.app.Application
import com.example.datastore.SystemPreferences

class CwsbsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SystemPreferences.init(this@CwsbsApplication)
    }
}