@file:Suppress("unused", "RedundantSetter")

package ir.farsroidx.m31

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A base ViewModel class that holds and manages UI state using both LiveData and StateFlow.
 * This class allows the UI layer to observe state changes through LiveData and other layers to collect state changes via StateFlow.
 * It ensures that state updates are properly synchronized with the correct thread and handles both main and background thread updates.
 *
 * @param UiState The type of the UI state that will be held in the ViewModel.
 */
abstract class AndromedaStatefulViewModel <UiState> : AndromedaViewModel() {

    private val mDefaultState: UiState by lazy { instanceWithDefaults() }

    private val _uiStateLiveData = MutableLiveData<UiState>(mDefaultState)
    private val _uiStateFlow     = MutableStateFlow<UiState>(mDefaultState)

    /**
     * LiveData for observing UI state (used by UI layer)
     * It provides an observable data container that can be used for UI updates in the main thread.
     * ```
     * viewModel.uiStateLiveData.observe(viewLifecycleOwner) { newUiState ->
     *     Log.d(TAG, newUiState.toString())
     * }
     * ```
     * */
    val uiStateLiveData: LiveData<UiState> = _uiStateLiveData

    /**
     * StateFlow for collecting UI state (used by other layers in the app)
     * It provides a flow-based mechanism for state collection in the app, ensuring state updates propagate smoothly.
     * ```
     * val uiState by viewModel.uiStateFlow.collectAsState()
     *
     * Log.d(TAG, uiState.toString())
     * ```
     * */
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow.asStateFlow()

    // Stores the last emitted UI state, can be accessed for reference
    var lastState: UiState = mDefaultState
        private set

    /**
     * Updates the UI state in both LiveData and StateFlow.
     *
     * This function performs the following operations:
     * 1. Checks if there are active observers for the LiveData:
     *    - If there are active observers, the new state is sent to LiveData.
     *    - If the current thread is the main thread, `setValue()` is used to update LiveData.
     *    - If the current thread is a background thread, `postValue()` is used to avoid threading exceptions.
     * 2. The new state is also emitted to the StateFlow, which is thread-safe and can be updated from any thread.
     * 3. The latest UI state is stored in `lastState` for future reference.
     *
     * Note:
     * - `LiveData` can only be updated on the main thread using `setValue()`. For background threads, `postValue()` is used to safely update the value without causing thread exceptions.
     * - `StateFlow` is thread-safe and can be updated from any thread without restriction, making it suitable for handling state in concurrent environments.
     *
     * @param newState The new UI state to be set in both LiveData and StateFlow.
     */
    protected fun setUiState(newState: UiState) {

        // Check if there are active observers for LiveData
        if (uiStateLiveData.hasObservers()) {

            // Check if the current thread is the main thread
            if (Looper.myLooper() == Looper.getMainLooper()) {
                // Use setValue on the main thread to avoid thread exceptions
                _uiStateLiveData.value = newState
            } else {
                // Use postValue on background threads for LiveData
                _uiStateLiveData.postValue(newState)
            }
        }

        // Update flow (can be updated from any thread)
        _uiStateFlow.value = newState

        // Store the latest state
        lastState = newState
    }

    /**
     * Clears all UI states by setting LiveData and StateFlow to default.
     * This method is useful for resetting the ui state when no states are active.
     * Example usage:
     * ```
     * viewModel.clearState()
     * ```
     */
    fun clearState() {
        // Clear State by setting them to default
        setUiState(mDefaultState)
    }
}