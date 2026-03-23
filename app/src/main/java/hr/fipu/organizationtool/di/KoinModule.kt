package hr.fipu.organizationtool.di

import hr.fipu.organizationtool.data.AppDatabase
import hr.fipu.organizationtool.data.TaskRepository
import hr.fipu.organizationtool.ui.TaskViewModel
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().taskDao() }
    single { TaskRepository(get()) }
    viewModel { TaskViewModel(get(), androidApplication()) }
}
