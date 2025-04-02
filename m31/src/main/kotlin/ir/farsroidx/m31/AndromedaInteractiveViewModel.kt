@file:Suppress("unused")

package ir.farsroidx.m31

/**
 * A ViewModel that handles both UI state and UI actions.
 * This ViewModel extends `AndromedaStatefulViewModel` and adds functionality to dispatch and handle actions.
 * It provides a mechanism to listen for new actions and trigger appropriate state changes.
 *
 * @param UiState The type of the UI state to be managed.
 * @param UiAction The type of actions that can be dispatched to the ViewModel.
 */
abstract class AndromedaInteractiveViewModel <UiState, UiAction> : AndromedaStatefulViewModel<UiState>() {

    // Stores the last emitted UI action, can be accessed for reference
    var lastAction: UiAction? = null
        private set

    /**
     * Dispatches a new action to the ViewModel for processing.
     * This method is used to send actions to the ViewModel, triggering corresponding changes in the UI state.
     * The action is passed to the `onNewAction` method, which handles the logic for the action.
     *
     * @param action The action to be processed by the ViewModel.
     */
    fun newAction(action: UiAction) {

        // Store the latest action
        lastAction = action

        // Handles the new action
        onAction(action = action)

    }

    /**
     * Handles the new action that has been dispatched to the ViewModel.
     * This method can be overridden by subclasses to define custom behavior for different actions.
     * It is the central place for handling business logic that changes the UI state based on actions.
     *
     * @param action The action to be processed and handled by the ViewModel.
     */
    open fun onAction(action: UiAction) {
        // Default implementation does nothing, can be overridden in subclasses
    }
}