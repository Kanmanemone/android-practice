package com.example.fragmentlifecyclelogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.fragmentlifecyclelogger.databinding.ActivitySecondBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity : AppCompatActivity(), OnBottomNavUiChangeListener {

    private lateinit var binding: ActivitySecondBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("interfacer_han", "SecondActivity.onCreate()")
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView
        navView.setOnItemSelectedListener { item ->
            Log.i("interfacer_han", "(MainActivity) ${item.title} clicked")
            navController.navigate(item.itemId)
            true
        }

        navController = findNavController(R.id.nav_host_fragment_activity_second)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home2, R.id.navigation_dashboard2, R.id.navigation_notifications2
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController) // 위에 있는 커스텀 리스너로 대체

        navigateFragment()
    }

    override fun onStart() {
        Log.i("interfacer_han", "SecondActivity.onStart()")
        super.onStart()
    }

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(newIntent)
        intent = newIntent
    }

    private fun navigateFragment() {
        Log.i(
            "interfacer_han", "(SecondActivity) intent.getStringExtra(\"fragment_info_key\"): ${
                intent.getStringExtra("fragment_info_key")
            }"
        )

        /* 위에 있는 onNewIntent()가 있어야,
         * 스택에 있다가 재호출(onResume())된 Activity의 intent.getStringExtra("fragment_info_key")의 값이
         * 하나로 고정되지 않는다.
         */
        when (intent.getStringExtra("fragment_info_key")) {
            null -> {/* 아무 것도 안함. 기본값으로 설정된 프래그먼트 호출됨 */
            }

            "Home2Fragment" -> {
                navController.navigate(R.id.navigation_home2)
            }

            "Dashboard2Fragment" -> {
                navController.navigate(R.id.navigation_dashboard2)
            }

            "Notifications2Fragment" -> {
                navController.navigate(R.id.navigation_notifications2)
            }
        }
    }

    override fun onResume() {
        Log.i("interfacer_han", "SecondActivity.onResume()")
        super.onResume()

        navigateFragment()
    }

    override fun onPause() {
        Log.i("interfacer_han", "SecondActivity.onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.i("interfacer_han", "SecondActivity.onStop()")
        super.onStop()
    }

    override fun onRestart() {
        Log.i("interfacer_han", "SecondActivity.onRestart()")
        super.onRestart()
    }

    override fun changeBottomNavUi(selectedItemId: Int) {
        val menuItem = navView.menu.findItem(selectedItemId)
        if (!menuItem.isChecked) {
            menuItem.isChecked = true
        }
    }
}