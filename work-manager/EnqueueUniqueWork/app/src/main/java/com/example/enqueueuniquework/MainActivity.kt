package com.example.enqueueuniquework

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
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

        // WorkManager 객체의 작업 큐(Job queue)에 WorkRequest 등록
        /* 과거에 enqueue()했었던 동일한 '고유 이름(여기서는 "myWork1")'인 작업을 '1st 작업',
         * 지금 enqueue()하려는 작업을 '2nd 작업'이라고 할 때,
         *
         * ExistingWorkPolicy.APPEND: 1st 작업을 취소하지 않고 그 뒷 순서로 2nd 작업을 이어 붙임 (Chaining Worker에서와 같은 동작. 참조: https://kenel.tistory.com/153)
         * ExistingWorkPolicy.APPEND_OR_REPLACE:
         *     2nd 작업이 enqueue()되는 시점에, 1st 작업의 상태가 FAILED면 2nd 작업 시작 (REPLACE와 같은 동작)
         *     2nd 작업이 enqueue()되는 시점에, 1st 작업이 아직 완료(= SUCCEEDED도 FAILED도 아님)되지 않았다면 그 뒷 순서로 2nd 작업을 이어 붙임 (APPEND와 같은 동작)
         * ExistingWorkPolicy.KEEP: 아무것도 하지 않음 (2nd 작업 실행 안됨)
         * ExistingWorkPolicy.REPLACE: 1st 작업을 Cancel하고 2nd 작업 시작
         */
        workManager.enqueueUniqueWork("myWork1", ExistingWorkPolicy.APPEND_OR_REPLACE, uploadRequest)

        // WorkInfo는 작업에 대한 정보를 담은 객체.
        // WorkManager.getWorkInfoByIdLiveData()는 LiveData<WorkInfo>를 반환.
        val workInfo = workManager.getWorkInfoByIdLiveData(uploadRequest.id)
        workInfo.observe(this, Observer {
            Log.i("interfacer_han", "(${this::class.simpleName}) UploadWorker's state: ${it.state.name}")
        })
    }

    private fun setPeriodicWorkRequest() {
        // WorkRequest 객체 만들기
        val downloadRequest = PeriodicWorkRequest
            .Builder(DownloadWorker::class.java, 15, TimeUnit.MINUTES) // 안드로이드에서 허용하는 PeriodicWorkRequest의 최소 주기는 15분이다.
            .build()

        // WorkManager 객체의 작업 큐(Job queue)에 WorkRequest 등록
        /* 과거에 enqueue()했었던 동일한 '고유 이름(여기서는 "myWork2")'인 작업을 '1st 작업',
         * 지금 enqueue()하려는 작업을 '2nd 작업'이라고 할 때,
         *
         * ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE: 1st 작업을 Cancel하고 2nd 작업 시작
         * ExistingPeriodicWorkPolicy.KEEP: 아무것도 하지 않음 (2nd 작업 실행 안됨)
         * ExistingPeriodicWorkPolicy.UPDATE:
         *     1st 작업을 취소하지 않고,
         *     작업의 Parameter (참조: https://kenel.tistory.com/152)나
         *     Constraint(참조: https://kenel.tistory.com/151)가
         *     2nd 작업의 것으로 업데이트됨. 대신 2nd 작업은 실행 안됨
         * ExistingPeriodicWorkPolicy.REPLACE: Deprecated됨. 대신 UPDATE를 사용할 것
         */
        workManager.enqueueUniquePeriodicWork("myWork2", ExistingPeriodicWorkPolicy.UPDATE, downloadRequest)

        // WorkInfo는 작업에 대한 정보를 담은 객체.
        // WorkManager.getWorkInfoByIdLiveData()는 LiveData<WorkInfo>를 반환.
        val workInfo = workManager.getWorkInfoByIdLiveData(downloadRequest.id)
        workInfo.observe(this, Observer {
            Log.i("interfacer_han", "(${this::class.simpleName}) DownloadWorker's state: ${it.state.name}")
        })
    }
}
