package com.example.constraints

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date

class DownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            Log.i("interfacer_han", "(${this::class.simpleName}) Started at ${getNowTime()}")

            for (i in 0..100) {
                Log.i("interfacer_han", "(${this::class.simpleName}) Progress: ${i}/100")
            }

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