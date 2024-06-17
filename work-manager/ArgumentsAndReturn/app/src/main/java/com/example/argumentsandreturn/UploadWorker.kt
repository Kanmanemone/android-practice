package com.example.argumentsandreturn

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date

class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val KEY_UPLOAD_WORKER = "key_upload_worker"
    }

    override fun doWork(): Result {
        try {
            Log.i("interfacer_han", "(${this::class.simpleName}) Started at ${getNowTime()}")

            val inputtedData = inputData.getInt(MainActivity.KEY_LUCKY_NUMBER, -1)
            Log.i("interfacer_han", "(${this::class.simpleName}) Inputted data: ${inputtedData}")

            for (i in 0..100) {
                Log.i("interfacer_han", "(${this::class.simpleName}) Progress: ${i}/100")
            }

            val outputData = Data
                .Builder()
                .putString(KEY_UPLOAD_WORKER, "Hello, MainActivity! I'm UploadWorker")
                .build()

            Log.i("interfacer_han", "(${this::class.simpleName}) Finished at ${getNowTime()}")
            return Result.success(outputData)

        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun getNowTime(): String {
        val time = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        return time.format(Date())
    }
}