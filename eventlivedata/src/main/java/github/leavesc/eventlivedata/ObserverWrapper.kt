package github.leavesc.eventlivedata

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * @Author: leavesC
 * @Date: 2020/7/9 23:11
 * @Desc:
 * GitHub：https://github.com/leavesC
 */
internal sealed class ObserverWrapper<T> constructor(
    protected val eventLiveData: EventLiveData<T>,
    internal val mObserver: Observer<T>,
    var mLastVersion: Int
) {

    internal var mActive = false

    abstract fun shouldBeActive(): Boolean

    open fun isAttachedTo(owner: LifecycleOwner): Boolean {
        return false
    }

    open fun detachObserver() {

    }

    fun activeStateChanged(newActive: Boolean) {
        if (newActive == mActive) {
            return
        }
        // immediately set active state, so we'd never dispatch anything to inactive
        // owner
        mActive = newActive
        val wasInactive = eventLiveData.mActiveCount == 0
        eventLiveData.mActiveCount += if (mActive) 1 else -1
        if (wasInactive && mActive) {
            eventLiveData.onActive()
        }
        if (eventLiveData.mActiveCount == 0 && !mActive) {
            eventLiveData.onInactive()
        }
        if (mActive) {
            eventLiveData.dispatchingValue(this)
        }
    }

}

internal class LifecycleBoundObserver<T>(
    eventLiveData: EventLiveData<T>,
    observer: Observer<T>,
    mLastVersion: Int,
    private val mOwner: LifecycleOwner,
    private val stateAtLeast: Lifecycle.State
) : ObserverWrapper<T>(eventLiveData, observer, mLastVersion), LifecycleEventObserver {

    override fun shouldBeActive(): Boolean {
        return mOwner.lifecycle.currentState.isAtLeast(stateAtLeast)
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        if (mOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            eventLiveData.removeObserver(mObserver)
            return
        }
        activeStateChanged(shouldBeActive())
    }

    override fun isAttachedTo(owner: LifecycleOwner): Boolean {
        return mOwner === owner
    }

    override fun detachObserver() {
        mOwner.lifecycle.removeObserver(this)
    }

}

internal class AlwaysActiveObserver<T> constructor(
    eventLiveData: EventLiveData<T>,
    observer: Observer<T>,
    mLastVersion: Int
) : ObserverWrapper<T>(eventLiveData, observer, mLastVersion) {

    override fun shouldBeActive(): Boolean {
        return true
    }

}