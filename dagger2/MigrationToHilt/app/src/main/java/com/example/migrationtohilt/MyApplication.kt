package com.example.migrationtohilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// AndroidManifest.xml에서 android:name=".MyApplication" 추가하는 것 잊지 말기!
@HiltAndroidApp
class MyApplication : Application()