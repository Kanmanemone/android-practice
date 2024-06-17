package com.example.parallelchaining

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

            val otherWorker2 = OneTimeWorkRequest
                .Builder(OtherWorker2::class.java)
                .build()

            val anotherWorker = OneTimeWorkRequest
                .Builder(AnotherWorker::class.java)
                .build()

            val anotherWorker2 = OneTimeWorkRequest
                .Builder(AnotherWorker2::class.java)
                .build()

            val anotherWorker3 = OneTimeWorkRequest
                .Builder(AnotherWorker3::class.java)
                .build()

            val parallelChain1 = mutableListOf<OneTimeWorkRequest>().apply {
                add(otherWorker)
                add(otherWorker2)
            }

            val parallelChain2 = mutableListOf<OneTimeWorkRequest>().apply {
                add(anotherWorker)
                add(anotherWorker2)
                add(anotherWorker3)
            }

            Log.i("interfacer_han", "(${this::class.simpleName}) Started at ${getNowTime()}")

            workManager
                .beginWith(parallelChain1)
                .then(parallelChain2)
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