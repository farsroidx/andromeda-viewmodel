@file:Suppress("unused", "SameParameterValue")

package ir.farsroidx.m31

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Base ViewModel that provides structured coroutine management and exception handling.
 * This ensures efficient background operations without memory leaks.
 */
abstract class AndromedaViewModel : ViewModel() {

    // SupervisorJob ensures independent failure handling of child coroutines
    private val supervisorJob = SupervisorJob()

    // CoroutineScope tied to ViewModel lifecycle with structured exception handling
    private val coroutineScope = CoroutineScope(viewModelScope.coroutineContext + supervisorJob)

    // Global CoroutineExceptionHandler to prevent application crashes
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        eLog("Throws Exception: ${throwable.message}")
        onUnhandledException(throwable)
    }

    /**
     * Handles unhandled exceptions from coroutines.
     * This method can be overridden in subclasses for custom error handling.
     *
     * @param throwable The exception that occurred.
     */
    protected open fun onUnhandledException(throwable: Throwable) {
        // Can be overridden to handle unhandled exceptions (e.g., show an error message)
    }

    /**
     * Launches a coroutine with the specified dispatcher and start mode.
     * This ensures uniform exception handling across all coroutine executions.
     *
     * @param dispatcher Coroutine dispatcher (e.g., IO, Main, Default).
     * @param start Coroutine start mode (default is CoroutineStart.DEFAULT).
     * @param block Suspend function to execute within the coroutine scope.
     */
    protected fun launchScope(
        dispatcher: CoroutineDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope.launch(dispatcher + coroutineExceptionHandler, start) { block.invoke(this) }
    }

    /** Launches a coroutine in the IO dispatcher for background operations (e.g., database, network). */
    protected fun ioViewModelScope(block: suspend CoroutineScope.() -> Unit) =
        launchScope(Dispatchers.IO, block = block)

    /** Launches a coroutine in the Main dispatcher for UI-related tasks. */
    protected fun mainViewModelScope(block: suspend CoroutineScope.() -> Unit) =
        launchScope(Dispatchers.Main, block = block)

    /** Launches a coroutine in the Default dispatcher for CPU-intensive computations. */
    protected fun defaultViewModelScope(block: suspend CoroutineScope.() -> Unit) =
        launchScope(Dispatchers.Default, block = block)

    /** Launches a coroutine in the Unconfined dispatcher for unrestricted execution. */
    protected fun unconfinedViewModelScope(block: suspend CoroutineScope.() -> Unit) =
        launchScope(Dispatchers.Unconfined, block = block)

    /**
     * Executes a suspend function in a specified dispatcher using `withContext`.
     * This is useful for performing background work inside another suspend function.
     *
     * @param dispatcher The dispatcher where the block will be executed.
     * @param block The suspend function to execute.
     * @return The result of the executed function.
     */
    protected suspend fun <R> withContextScope(
        dispatcher: CoroutineDispatcher,
        block: suspend CoroutineScope.() -> R
    ): R = withContext(dispatcher + coroutineExceptionHandler, block)

    /** Executes a suspend function in the IO dispatcher for background work like network requests. */
    protected suspend fun <R> ioContext(block: suspend CoroutineScope.() -> R) =
        withContextScope(Dispatchers.IO, block)

    /** Executes a suspend function in the Main dispatcher for UI-related tasks. */
    protected suspend fun <R> mainContext(block: suspend CoroutineScope.() -> R) =
        withContextScope(Dispatchers.Main, block)

    /** Executes a suspend function in the Default dispatcher for CPU-intensive operations. */
    protected suspend fun <R> defaultContext(block: suspend CoroutineScope.() -> R) =
        withContextScope(Dispatchers.Default, block)

    /** Executes a suspend function in the Unconfined dispatcher for unrestricted execution flow. */
    protected suspend fun <R> unconfinedContext(block: suspend CoroutineScope.() -> R) =
        withContextScope(Dispatchers.Unconfined, block)

    /**
     * Cancels all active jobs in the ViewModel scope.
     * This method ensures that no background work is left running when ViewModel is cleared.
     */
    protected fun cancelJobs() {
        // Cancels all child coroutines of the current scope
        coroutineScope.coroutineContext.cancelChildren()
    }

    /**
     * Ensures all coroutines are properly cancelled when ViewModel is destroyed.
     * This prevents memory leaks from lingering background tasks.
     */
    override fun onCleared() {
        super.onCleared()
        // Using the cancelJobs method here to cancel all active jobs in the ViewModel
        cancelJobs()
    }
}