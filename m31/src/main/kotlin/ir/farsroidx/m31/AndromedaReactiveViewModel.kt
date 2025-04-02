@file:Suppress("unused")

package ir.farsroidx.m31

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A reactive ViewModel that handles both UI state and UI events.
 * This ViewModel extends from `AndromedaInteractiveViewModel` and adds functionality
 * to manage and emit UI events, providing both LiveData and StateFlow
 * for event observation.
 *
 * @param UiState The type of the UI state.
 * @param UiAction The type of actions that can be dispatched to the ViewModel.
 * @param UiEvent The type of events to be emitted and observed by the UI.
 */
abstract class AndromedaReactiveViewModel <UiState, UiAction, UiEvent> : AndromedaInteractiveViewModel<UiState, UiAction>() {

    private val _uiEventLiveData = MutableLiveData<UiEvent?>(null)
    private val _uiEventFlow     = MutableStateFlow<UiEvent?>(null)

    /**
     * LiveData for observing UI event (used by the UI layer)
     * LiveData allows observing data changes on the main thread.
     * UI components can observe these events and react accordingly.
     * Example usage:
     * ```
     * viewModel.uiEventLiveData.observe(viewLifecycleOwner) { newUiEvent ->
     *     Log.d(TAG, newUiEvent.toString())
     * }
     * ```
     * */
    val uiEventLiveData: LiveData<UiEvent?> = _uiEventLiveData

    /**
     * StateFlow for collecting UI events (used by other layers in the app).
     * StateFlow allows state collection and observation, ensuring that events
     * are observed and propagated efficiently in a reactive manner.
     * Example usage:
     * ```
     * val uiEvent by viewModel.uiEventFlow.collectAsState()
     * Log.d(TAG, uiEvent.toString())
     * ```
     */
    val uiEventFlow: StateFlow<UiEvent?> = _uiEventFlow.asStateFlow()

    // Stores the last emitted UI event, can be accessed for reference
    var lastEvent: UiEvent? = null
        private set

    /**
     * Emits a new UI event to both LiveData and StateFlow, and stores it as the last event.
     * This ensures that both LiveData and StateFlow are updated in sync, and the most recent event
     * is stored for future reference.
     *
     * This function performs the following steps:
     * 1. Checks if there are active observers for the LiveData.
     *    - If there are active observers, the new event is sent to LiveData.
     *    - If the current thread is the main thread, `setValue()` is used to update LiveData.
     *    - If the current thread is a background thread, `postValue()` is used to avoid threading issues.
     * 2. The new event is also emitted to the StateFlow, which is thread-safe and can be updated from any thread.
     * 3. The latest event is stored for future reference (if needed).
     *
     * Note:
     * - `LiveData` can only be updated on the main thread using `setValue()`. For background threads, `postValue()` is used.
     * - `StateFlow` is thread-safe and can be updated from any thread without restriction.
     *
     * @param newEvent The new event to be sent to both LiveData and StateFlow.
     */
    protected fun newEvent(newEvent: UiEvent?) {

        // Check if there are active observers for LiveData
        if (uiEventLiveData.hasObservers()) {

            // Check if the current thread is the main thread
            if (Looper.myLooper() == Looper.getMainLooper()) {
                // Use setValue on the main thread to avoid thread exceptions
                _uiEventLiveData.value = newEvent
            } else {
                // Use postValue on background threads for LiveData
                _uiEventLiveData.postValue(newEvent)
            }
        }

        // Update flow (can be updated from any thread)
        _uiEventFlow.value = newEvent

        // Store the latest event
        lastEvent = newEvent
    }

    /**
     * Clears all UI events by setting LiveData and StateFlow to null.
     * This method is useful for resetting the event state when no events are active.
     * Example usage:
     * ```
     * viewModel.clearEvents()
     * ```
     */
    fun clearEvents() {
        // Clear events by setting them to null
        newEvent(null)
    }
}