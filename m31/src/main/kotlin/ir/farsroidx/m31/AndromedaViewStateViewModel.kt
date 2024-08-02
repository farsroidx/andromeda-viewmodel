@file:Suppress("unused")

package ir.farsroidx.m31

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class AndromedaViewStateViewModel <S: Any> : AndromedaViewModel() {

    private var _onViewStateChange: (S) -> Unit = {}

    private var _lifecycleOwner: LifecycleOwner? = null

    private val _liveViewStateChange = MutableLiveData<S>()
    val liveViewStateChange: LiveData<S> = _liveViewStateChange

    private val _viewStateChange: MutableState<S?> = mutableStateOf(null)
    val viewStateChange: State<S?> = _viewStateChange

    private val _viewStateChangeFlow: MutableStateFlow<S?> = MutableStateFlow(null)
    val viewStateChangeFlow: StateFlow<S?> = _viewStateChangeFlow.asStateFlow()

    fun bindLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this._lifecycleOwner = lifecycleOwner
    }

    fun bindStateChangeCallback(
        lifecycleOwner: LifecycleOwner, onStateChangeCallback: (S) -> Unit
    ) {
        this._lifecycleOwner    = lifecycleOwner
        this._onViewStateChange = onStateChangeCallback
    }

    protected fun updateViewState(viewState: S, asPostValue: Boolean = false) {

        if (asPostValue) {
            this._liveViewStateChange.postValue(viewState)
        }

        viewModelScope(Dispatchers.Main) {

            if (!asPostValue) {
                _liveViewStateChange.value = viewState
            }

            _viewStateChange.value     = viewState
            _viewStateChangeFlow.value = viewState

            _lifecycleOwner?.let { lifecycleOwner ->

                lifecycleOwner.lifecycleScope.launch {

                    if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.DESTROYED) {

                        _onViewStateChange(viewState)

                    } else {
                        // TODO: Handle the state change if needed
                    }
                }
            }
        }
    }

    fun resetViewState() = viewModelScope(Dispatchers.Main) {
        _viewStateChange.value     = null
        _viewStateChangeFlow.value = null
    }
}
