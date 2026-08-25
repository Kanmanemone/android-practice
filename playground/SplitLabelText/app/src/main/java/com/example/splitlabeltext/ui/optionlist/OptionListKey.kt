package com.example.splitlabeltext.ui.optionlist

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavBackStack
import com.example.splitlabeltext.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
object OptionListKey : MyNavKey {
    override val title = "옵션 목록"
    override val screen: @Composable () -> Unit = {
        val viewModel = hiltViewModel<OptionListViewModel>()
        OptionListScreen(
            entries = viewModel.entries,
            onSelect = viewModel::select,
        )
    }
}

@HiltViewModel
class OptionListViewModel @Inject constructor(
    entries: Set<@JvmSuppressWildcards MyNavKey>,
    private val navBackStack: NavBackStack<MyNavKey>,
) : ViewModel() {
    val entries = entries.filterNot { it === OptionListKey }.sortedBy { it.title }

    fun select(entry: MyNavKey) {
        navBackStack.add(entry)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionListModule {
    @Provides
    @IntoSet
    fun provideOptionListKey(): MyNavKey = OptionListKey

    @Provides
    @Singleton
    fun provideNavBackStack(): NavBackStack<MyNavKey> = NavBackStack(OptionListKey)
}
