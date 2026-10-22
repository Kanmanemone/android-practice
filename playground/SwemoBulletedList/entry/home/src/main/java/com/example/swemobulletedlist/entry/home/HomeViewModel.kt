package com.example.swemobulletedlist.entry.home

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavBackStack
import com.example.swemobulletedlist.entry.api.EntryKey
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    entryKeys: Set<@JvmSuppressWildcards EntryKey>,
    private val navBackStack: NavBackStack<EntryKey>,
) : ViewModel() {
    val entryKeys = entryKeys.filterNot { it === HomeKey }.sortedBy { it.title }

    fun navigateTo(key: EntryKey) {
        navBackStack.add(key)
    }
}
