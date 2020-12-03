package github.leavesc.eventlivedata

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * @Author: leavesC
 * @Date: 2020/7/9 22:37
 * @Desc:
 * GitHub：https://github.com/leavesC
 */
open class EventLiveData<Data> {

    private companion object {

        private const val START_VERSION = -1

        private val NOT_SET = Any()

        private fun assertMainThread(methodName: String) {
            check(EventTaskExecutor.isMainThread) {
                ("Cannot invoke $methodName on a background thread")
            }
        }

    }

    /* synthetic access */
    private val mDataLock = Any()

    private val mObservers = EventSafeIterableMap<Observer<Data>, ObserverWrapper<Data>>()

    // how many observers are in active state
    internal var mActiveCount = 0

    @Volatile
    private var mData: Any?

    // when setData is called, we set the pending data and actual data swap happens on the main
    // thread
    /* synthetic access */
    @Volatile
    private var mPendingData: Any? = NOT_SET

    private var mVersion: Int

    private var mDispatchingValue = false

    private var mDispatchInvalidated = false

    private val mPostValueRunnable = Runnable {
        var newValue: Any?
        synchronized(mDataLock) {
            newValue = mPendingData
            mPendingData = NOT_SET
        }
        setValue(newValue as Data)
    }

    /**
     * Creates a LiveData initialized with the given `value`.
     *
     * @param value initial value
     */
    constructor(value: Data) {
        mData = value
        mVersion = START_VERSION + 1
    }

    /**
     * Creates a LiveData with no value assigned to it.
     */
    constructor() {
        mData = NOT_SET
        mVersion = START_VERSION
    }

    private fun considerNotify(observer: ObserverWrapper<Data>) {
        if (!observer.mActive) {
            return
        }
        // Check latest state b4 dispatch. Maybe it changed state but we didn't get the event yet.
        //
        // we still first check observer.active to keep it as the entrance for events. So even if
        // the observer moved to an active state, if we've not received that event, we better not
        // notify for a more predictable notification order.
        if (!observer.shouldBeActive()) {
            observer.activeStateChanged(false)
            return
        }
        if (observer.mLastVersion >= mVersion) {
            return
        }
        observer.mLastVersion = mVersion
        observer.mObserver.onChanged(mData as Data)
    }

    /* synthetic access */
    internal fun dispatchingValue(observerWrapper: ObserverWrapper<Data>?) {
        var initiator: ObserverWrapper<Data>? = observerWrapper
        if (mDispatchingValue) {
            mDispatchInvalidated = true
            return
        }
        mDispatchingValue = true
        do {
            mDispatchInvalidated = false
            if (initiator != null) {
                considerNotify(initiator)
                initiator = null
            } else {
                val iterator: Iterator<Map.Entry<Observer<Data>, ObserverWrapper<Data>>> =
                    mObservers.iteratorWithAdditions()
                while (iterator.hasNext()) {
                    considerNotify(iterator.next().value)
                    if (mDispatchInvalidated) {
                        break
                    }
                }
            }
        } while (mDispatchInvalidated)
        mDispatchingValue = false
    }

    /**
     * 不接收黏性消息，即不会收到在 observe 之前的消息
     * @param owner
     * @param observer
     * @param isLifecycleIntensive
     * 为 true 时，在 onResume 之后和 onDestroy 之前均能收到 Observer 回调
     * 为 false 时，在 onCreate 之后和 onDestroy 之前均能收到 Observer 回调
     */
    @MainThread
    @JvmOverloads
    fun observe(
        owner: LifecycleOwner,
        observer: Observer<Data>,
        isLifecycleIntensive: Boolean = true
    ) {
        observeLifecycleObserver(
            funName = "observe",
            owner = owner,
            observer = observer,
            sticky = false,
            isLifecycleIntensive = isLifecycleIntensive
        )
    }

    /**
     * 接收黏性消息，即会收到在 observe 之前的消息
     * @param owner
     * @param observer
     * @param isLifecycleIntensive
     * 为 true 时，在 onResume 之后和 onDestroy 之前均能收到 Observer 回调
     * 为 false 时，在 onCreate 之后和 onDestroy 之前均能收到 Observer 回调
     */
    @MainThread
    @JvmOverloads
    fun observeSticky(
        owner: LifecycleOwner,
        observer: Observer<Data>,
        isLifecycleIntensive: Boolean = true
    ) {
        observeLifecycleObserver(
            funName = "observeSticky",
            owner = owner,
            observer = observer,
            sticky = true,
            isLifecycleIntensive = isLifecycleIntensive
        )
    }

    /**
     * 不接收黏性消息，即不会收到在 observe 之前的消息
     * 不具备生命周期安全的保障
     * @param observer
     */
    @MainThread
    fun observeForever(observer: Observer<Data>) {
        observeAlwaysActiveObserver(
            funName = "observeForever",
            observer = observer,
            sticky = false
        )
    }

    /**
     * 接收黏性消息，即会收到在 observe 之前的消息
     * 不具备生命周期安全的保障
     * @param observer
     */
    @MainThread
    fun observeForeverSticky(observer: Observer<Data>) {
        observeAlwaysActiveObserver(
            funName = "observeForeverSticky",
            observer = observer,
            sticky = true
        )
    }

    private fun observeLifecycleObserver(
        funName: String,
        owner: LifecycleOwner,
        observer: Observer<Data>,
        sticky: Boolean,
        isLifecycleIntensive: Boolean
    ) {
        assertMainThread(funName)
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        val lastVersion = if (sticky) START_VERSION else mVersion
        val stateAtLeast =
            if (isLifecycleIntensive) Lifecycle.State.STARTED else Lifecycle.State.CREATED
        val wrapper = LifecycleBoundObserver(
            this,
            observer,
            lastVersion,
            owner,
            stateAtLeast
        )
        val existing = mObservers.putIfAbsent(observer, wrapper)
        require(!(existing != null && !existing.isAttachedTo(owner))) {
            ("Cannot add the same observer with different lifecycles")
        }
        if (existing != null) {
            return
        }
        owner.lifecycle.addObserver(wrapper)
    }

    private fun observeAlwaysActiveObserver(
        funName: String,
        observer: Observer<Data>,
        sticky: Boolean
    ) {
        assertMainThread(funName)
        val lastVersion = if (sticky) START_VERSION else mVersion
        val wrapper = AlwaysActiveObserver(
            this,
            observer,
            lastVersion
        )
        val existing = mObservers.putIfAbsent(observer, wrapper)
        require(existing !is LifecycleBoundObserver) {
            ("Cannot add the same observer with different lifecycles")
        }
        if (existing != null) {
            return
        }
        wrapper.activeStateChanged(true)
    }

    /**
     * Removes the given observer from the observers list.
     *
     * @param observer The Observer to receive events.
     */
    @MainThread
    fun removeObserver(observer: Observer<Data>) {
        assertMainThread(
            "removeObserver"
        )
        val removed = mObservers.remove(observer) ?: return
        removed.detachObserver()
        removed.activeStateChanged(false)
    }

    /**
     * Removes all observers that are tied to the given [LifecycleOwner].
     *
     * @param owner The `LifecycleOwner` scope for the observers to be removed.
     */
    @MainThread
    fun removeObservers(owner: LifecycleOwner) {
        assertMainThread(
            "removeObservers"
        )
        for (mObserver in mObservers) {
            if (mObserver.value.isAttachedTo(owner)) {
                removeObserver(mObserver.key)
            }
        }
    }

    /**
     * Sets the value. If there are active observers, the value will be dispatched to them.
     *
     *
     * This method must be called from the main thread. If you need set a value from a background
     * thread, you can use [.postValue]
     *
     * @param value The new value
     */
    @MainThread
    fun setValue(value: Data) {
        assertMainThread("setValue")
        mVersion++
        mData = value
        dispatchingValue(null)
    }

    /**
     * Posts a task to a main thread to set the given value. So if you have a following code
     * executed in the main thread:
     * <pre class="prettyprint">
     * liveData.postValue("a");
     * liveData.setValue("b");
    </pre> *
     * The value "b" would be set at first and later the main thread would override it with
     * the value "a".
     *
     *
     * If you called this method multiple times before a main thread executed a posted task, only
     * the last value would be dispatched.
     *
     * @param value The new value
     */
    fun postValue(value: Data) {
        var postTask: Boolean
        synchronized(mDataLock) {
            postTask = mPendingData === NOT_SET
            mPendingData = value
        }
        if (!postTask) {
            return
        }
        EventTaskExecutor.postToMainThread(
            mPostValueRunnable
        )
    }

    fun submitValue(value: Data) {
        if (EventTaskExecutor.isMainThread) {
            setValue(value)
        } else {
            postValue(value)
        }
    }

    /**
     * Returns the current value.
     * Note that calling this method on a background thread does not guarantee that the latest
     * value set will be received.
     *
     * @return the current value
     */
    fun getValue(): Data? {
        val data = mData
        return if (data !== NOT_SET) {
            data as Data
        } else null
    }

    /**
     * Called when the number of active observers change to 1 from 0.
     *
     *
     * This callback can be used to know that this LiveData is being used thus should be kept
     * up to date.
     */
    open fun onActive() {

    }

    /**
     * Called when the number of active observers change from 1 to 0.
     *
     *
     * This does not mean that there are no observers left, there may still be observers but their
     * lifecycle states aren't [Lifecycle.State.STARTED] or [Lifecycle.State.RESUMED]
     * (like an Activity in the back stack).
     *
     *
     * You can check if there are observers via [.hasObservers].
     */
    open fun onInactive() {

    }

    /**
     * Returns true if this LiveData has observers.
     *
     * @return true if this LiveData has observers
     */
    fun hasObservers(): Boolean {
        return mObservers.size() > 0
    }

    /**
     * Returns true if this LiveData has active observers.
     *
     * @return true if this LiveData has active observers
     */
    fun hasActiveObservers(): Boolean {
        return mActiveCount > 0
    }

}