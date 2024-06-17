package com.example.chainingworkers

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.ExistingWorkPolicy
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
        // WorkRequest 객체 만들기
        val uploadRequest = OneTimeWorkRequest
            .Builder(UploadWorker::class.java)
            .build()

        val otherWorker = OneTimeWorkRequest
            .Builder(OtherWorker::class.java)
            .build()

        val anotherWorker = OneTimeWorkRequest
            .Builder(AnotherWorker::class.java)
            .build()

        // WorkManager 객체의 작업 큐(Job queue)에 WorkRequest 등록
        workManager
            .beginWith(otherWorker)
            .then(anotherWorker)
            .then(uploadRequest)
            .enqueue()

        // WorkInfo는 작업에 대한 정보를 담은 객체.
        // WorkManager.getWorkInfoByIdLiveData()는 LiveData<WorkInfo>를 반환.
        val workInfo = workManager.getWorkInfoByIdLiveData(uploadRequest.id)
        workInfo.observe(this, Observer {
            Log.i("interfacer_han", "(${this::class.simpleName}) UploadWorker's state: ${it.state.name}")
        })
    }

    private fun setPeriodicWorkRequest() {
        // 'Worker 체인 개시'용 Worker 만들기
        val chainInitiationWorker = PeriodicWorkRequest
            .Builder(ChainInitiationWorker::class.java, 15, TimeUnit.MINUTES) // 안드로이드에서 허용하는 PeriodicWorkRequest의 최소 주기는 15분이다.
            .build()

        // WorkManager 객체의 작업 큐(Job queue)에 WorkRequest 등록
        workManager.enqueue(chainInitiationWorker)

        // WorkInfo는 작업에 대한 정보를 담은 객체.
        // WorkManager.getWorkInfoByIdLiveData()는 LiveData<WorkInfo>를 반환.
        val workInfo = workManager.getWorkInfoByIdLiveData(chainInitiationWorker.id)
        workInfo.observe(this, Observer {
            Log.i("interfacer_han", "(${this::class.simpleName}) ChainInitiationWorker's state: ${it.state.name}")
        })
    }
}
