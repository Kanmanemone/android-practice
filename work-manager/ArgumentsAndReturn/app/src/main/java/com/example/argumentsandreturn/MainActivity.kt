package com.example.argumentsandreturn

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

lateinit var workManager: WorkManager

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_LUCKY_NUMBER= "key_lucky"
    }

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
        // Worker에 전달할 Data 객체 만들기
        val inputData: Data = Data
            .Builder()
            .putInt(KEY_LUCKY_NUMBER, 777)
            .build()

        // WorkRequest 객체 만들기
        val uploadRequest = OneTimeWorkRequest
            .Builder(UploadWorker::class.java)
            .setInputData(inputData)
            .build()

        // WorkManager 객체의 작업 큐(Job queue)에 WorkRequest 등록
        workManager.enqueue(uploadRequest)

        // WorkInfo는 작업에 대한 정보를 담은 객체.
        // WorkManager.getWorkInfoByIdLiveData()는 LiveData<WorkInfo>를 반환.
        val workInfo = workManager.getWorkInfoByIdLiveData(uploadRequest.id)
        workInfo.observe(this, Observer {
            Log.i("interfacer_han", "(${this::class.simpleName}) UploadWorker's state: ${it.state.name}")

            if(it.state.name == "SUCCEEDED") {
                val outputData = it.outputData
                Log.i("interfacer_han", "(${this::class.simpleName}) UploadWorker's output: ${outputData.getString(UploadWorker.KEY_UPLOAD_WORKER)}")
            }
        })
    }

    private fun setPeriodicWorkRequest() {
        // Worker에 전달할 Data 객체 만들기
        val inputData: Data = Data
            .Builder()
            .putInt(KEY_LUCKY_NUMBER, 777)
            .build()

        // WorkRequest 객체 만들기
        val downloadRequest = PeriodicWorkRequest
            .Builder(DownloadWorker::class.java, 15, TimeUnit.MINUTES) // 안드로이드에서 허용하는 PeriodicWorkRequest의 최소 주기는 15분이다.
            .setInputData(inputData)
            .build()

        // WorkManager 객체의 작업 큐(Job queue)에 WorkRequest 등록
        workManager.enqueue(downloadRequest)

        // WorkInfo는 작업에 대한 정보를 담은 객체.
        // WorkManager.getWorkInfoByIdLiveData()는 LiveData<WorkInfo>를 반환.
        val workInfo = workManager.getWorkInfoByIdLiveData(downloadRequest.id)
        workInfo.observe(this, Observer {
            Log.i("interfacer_han", "(${this::class.simpleName}) DownloadWorker's state: ${it.state.name}")

            // 이 if문 속의 코드는 영원히 실행되지 못한다.
            if(it.state.name == "SUCCEEDED") {
                val outputData = it.outputData
                Log.i("interfacer_han", "(${this::class.simpleName}) DownloadWorker's output: ${outputData.getString(DownloadWorker.KEY_DOWNLOAD_WORKER)}")
            }
        })
    }
}
