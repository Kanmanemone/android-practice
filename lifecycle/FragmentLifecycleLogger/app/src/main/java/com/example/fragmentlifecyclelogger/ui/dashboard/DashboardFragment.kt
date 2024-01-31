package com.example.fragmentlifecyclelogger.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.fragmentlifecyclelogger.OnBottomNavUiChangeListener
import com.example.fragmentlifecyclelogger.R
import com.example.fragmentlifecyclelogger.SecondActivity
import com.example.fragmentlifecyclelogger.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onCreateView()")
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val button: Button = binding.buttonDashboard
        button.setOnClickListener {
            val intent = Intent(activity, SecondActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) // 이 플래그가 없으면, Activity를 전환할 때마다 Activity가 onCreate()됨
            intent.putExtra("fragment_info_key", "Dashboard2Fragment")
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onDestoryView()")
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onViewStateRestored()")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onStart()")
        super.onStart()

        val nowActivity = activity
        if(nowActivity is OnBottomNavUiChangeListener) {
            nowActivity.changeBottomNavUi(R.id.navigation_dashboard)
        }
    }

    override fun onResume() {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onResume()")
        super.onResume()
    }

    override fun onPause() {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onStop()")
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onSaveInstanceState()")
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        Log.i("interfacer_han", "(MainActivity) DashboardFragment.onDestroy()")
        super.onDestroy()
    }
}