@file:Suppress("unused")

package ir.farsroidx.m31

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Use AndromedaViewModelViewState instead of AndromedaViewModel to better manage UI state.
 * ```
 * class YourViewModel : AndromedaViewModelViewState<YourState>()
 * ```
 */
abstract class AndromedaViewModel : ViewModel() {

    companion object {
        private const val TAG = "AndromedaViewModel"
    }

    private val _jobs = mutableListOf<Job>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception occurred: $throwable")
        onJobException(throwable)
    }

    protected fun viewModelScope(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        callback: suspend CoroutineScope.() -> Unit
    ) {

        val job =
            viewModelScope.launch(dispatcher + coroutineExceptionHandler) { callback() }

        job.invokeOnCompletion { throwable ->

            if (throwable is CancellationException) {

                _jobs.remove(job)

                Log.d(TAG, "Job cancelled")

            } else if (throwable != null) {

                onJobException(throwable)

                Log.e(TAG, "Job failed: $throwable")

            } else {
                Log.d(TAG, "Job completed successfully")
            }
        }

        _jobs.add(job)
    }

    override fun onCleared() {
        super.onCleared()

        for (job in _jobs) { job.cancel() }

        _jobs.clear()
    }

    protected open fun onJobException(throwable: Throwable) {
        // TODO: Nothing to Change
    }
}
