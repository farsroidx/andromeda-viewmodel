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
 * Use AndromedaViewStateViewModel instead of AndromedaViewModel to better manage UI state.
 * ```
 * class YourViewModel : AndromedaViewStateViewModel<YourStateType>()
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

    protected fun doInDefaultScope(
        callback: suspend CoroutineScope.() -> Unit
    ) = viewModelScope(Dispatchers.Default, callback)

    protected fun doInIOScope(
        callback: suspend CoroutineScope.() -> Unit
    ) = viewModelScope(Dispatchers.IO, callback)

    protected fun doInMainScope(
        callback: suspend CoroutineScope.() -> Unit
    ) = viewModelScope(Dispatchers.Main, callback)

    protected fun doInUnconfinedScope(
        callback: suspend CoroutineScope.() -> Unit
    ) = viewModelScope(Dispatchers.Unconfined, callback)

    protected fun viewModelScope(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        callback: suspend CoroutineScope.() -> Unit
    ) {

        val job =
            viewModelScope.launch(dispatcher + coroutineExceptionHandler) { callback() }

        job.invokeOnCompletion { throwable ->

            if (throwable is CancellationException) {

                _jobs.remove(job)

                Log.d(TAG, "Job Cancelled.")

            } else if (throwable != null) {

                onJobException(throwable)

                Log.e(TAG, "Job Failed: ${throwable.message}.")

            } else {
                Log.d(TAG, "Job completed Successfully!")
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
