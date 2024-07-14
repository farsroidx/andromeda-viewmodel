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
import kotlinx.coroutines.launch

abstract class AndromedaViewStateViewModel <S: Any> : AndromedaViewModel() {

    private var _onViewStateChange: (S) -> Unit = {}

    private var _lifecycleOwner: LifecycleOwner? = null

    private val _liveViewStateChange = MutableLiveData<S>()
    val liveViewStateChange: LiveData<S> = _liveViewStateChange

    private val _composeViewStateChange: MutableState<S?> = mutableStateOf(null)
    val composeViewStateChange: State<S?> = _composeViewStateChange

    fun bindLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this._lifecycleOwner = lifecycleOwner
    }

    fun bindLifecycleOwner(
        lifecycleOwner: LifecycleOwner, onStateChange: (S) -> Unit
    ) {
        this._lifecycleOwner    = lifecycleOwner
        this._onViewStateChange = onStateChange
    }

    protected fun updateViewState(viewState: S, asPostValue: Boolean = false) {

        if (asPostValue) {
            this._liveViewStateChange.postValue(viewState)
        } else {
            this._liveViewStateChange.value = viewState
        }

        this._composeViewStateChange.value = viewState

        this._lifecycleOwner?.let { lifecycleOwner ->

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
