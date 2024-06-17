package com.example.chainingworkers

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date

class ChainInitiationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            val workManager = WorkManager.getInstance(applicationContext)

            // WorkRequest 객체 만들기
            val downloadRequest = OneTimeWorkRequest
                .Builder(DownloadWorker::class.java)
                .build()

            val otherWorker = OneTimeWorkRequest
                .Builder(OtherWorker::class.java)
                .build()

            val anotherWorker = OneTimeWorkRequest
                .Builder(AnotherWorker::class.java)
                .build()

            Log.i("interfacer_han", "(${this::class.simpleName}) Started at ${getNowTime()}")

            workManager
                .beginWith(otherWorker)
                .then(anotherWorker)
                .then(downloadRequest)
                .enqueue()

            Log.i("interfacer_han", "(${this::class.simpleName}) Finished at ${getNowTime()}")
            return Result.success()

        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun getNowTime(): String {
        val time = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        return time.format(Date())
    }
}