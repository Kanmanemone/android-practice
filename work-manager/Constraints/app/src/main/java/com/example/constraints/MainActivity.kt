package com.example.constraints

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

lateinit var workManager: WorkManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workManager = WorkManager.getInstance(applicationContext)

        val oneTimeButton = findViewById<Button>(R.id.oneTimeButton)
        val periodicButton = findViewById<Button>(R.id.periodicButton)

        oneTimeButton.setOnClickListener {
            setOneTimeWorkRequest()
        }

        periodicButton.setOnClickListener {
            setPeriodicWorkRequest()
        }
    }

    private fun setOneTimeWorkRequest() {
        // 제약 조건 객체 만들기
        val constraints = Constraints
            .Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // WorkRequest 객체 만들기
        val uploadRequest = OneTimeWorkRequest
            .Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .build()

        // WorkManager 객체의 작업 큐(Job queue)에 WorkRequest 등록
        workManager.enqueue(uploadRequest)

        // WorkInfo는 작업에 대한 정보를 담은 객체.
        // WorkManager.getWorkInfoByIdLiveData()는 LiveData<WorkInfo>를 반환.
        val workInfo = workManager.getWorkInfoByIdLiveData(uploadRequest.id)
        workInfo.observe(this, Observer {
            Log.i("interfacer_han", "(${this::class.simpleName}) UploadWorker's state: ${it.state.name}")
        })
    }

    private fun setPeriodicWorkRequest() {
        // 제약 조건 객체 만들기
        val constraints = Constraints
            .Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // WorkRequest 객체 만들기
        val downloadRequest = PeriodicWorkRequest
            .Builder(DownloadWorker::class.java, 15, TimeUnit.MINUTES) // 안드로이드에서 허용하는 PeriodicWorkRequest의 최소 주기는 15분이다.
            .setConstraints(constraints)
            .build()

        // WorkManager 객체의 작업 큐(Job queue)에 WorkRequest 등록
        workManager.enqueue(downloadRequest)

        // WorkInfo는 작업에 대한 정보를 담은 객체.
        // WorkManager.getWorkInfoByIdLiveData()는 LiveData<WorkInfo>를 반환.
        val workInfo = workManager.getWorkInfoByIdLiveData(downloadRequest.id)
        workInfo.observe(this, Observer {
            Log.i("interfacer_han", "(${this::class.simpleName}) DownloadWorker's state: ${it.state.name}")
        })
    }
}
